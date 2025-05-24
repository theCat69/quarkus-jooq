package com.fvd.jooq.db;

import com.fvd.jooq.db.batching.BatchProcessor;
import com.fvd.jooq.db.mapper.ColumnMapper;
import com.fvd.jooq.db.mapper.MapMapper;
import com.fvd.jooq.db.model.Column;
import com.fvd.jooq.db.model.Table;
import io.quarkus.reactive.datasource.ReactiveDataSource;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class OracleQueries {

  @ConfigProperty(name = "com.fvd.app.batch.size")
  Integer batchSize;

  private final Pool oraPool;

  @Inject
  public OracleQueries(@ReactiveDataSource("alt") Pool oraPool) {
    this.oraPool = oraPool;
  }

  @SneakyThrows
  public Uni<Table> findTableDefinition(String tableName) {
    log.info("5");
    // Get column definitions
    return oraPool.preparedQuery(
        "SELECT atc.column_name, atc.data_type, atc.data_length, atc.data_precision, atc.data_scale, atc.nullable, atc.table_name, " +
          " MAX(acc_fk.table_name) as fk_table_name, MAX(acc_fk.column_name) as fk_column_name, " +
          "    LISTAGG(ac.constraint_type, '|') within GROUP (order by atc.column_id) as constraint_types" +
          "          FROM all_tab_columns atc" +
          "          LEFT JOIN all_cons_columns acc on acc.table_name = atc.table_name and acc.column_name = atc.column_name" +
          "          LEFT JOIN all_constraints ac on acc.constraint_name = ac.constraint_name" +
          "          LEFT JOIN all_cons_columns acc_fk on acc_fk.constraint_name = ac.r_constraint_name" +
          "          WHERE atc.table_name = ?" +
          "          GROUP BY atc.column_name, atc.data_type, atc.data_length, atc.data_precision, atc.data_scale, atc.nullable, atc.table_name, " +
          "          atc.column_id" +
          " ORDER BY atc.column_id")
      .execute(Tuple.of(tableName.toUpperCase()))
      .map(columns -> Table.builder()
        .name(tableName)
        .columns(ColumnMapper.map(columns))
        .build());


//    List<Index> indexes = oracleJdbc.query().select(
//        "SELECT i.index_name, i.uniqueness, i.table_name, " +
//          "LISTAGG(c.column_name, '|') WITHIN GROUP (ORDER BY c.column_position) as columns " +
//          "FROM all_indexes i " +
//          "JOIN all_ind_columns c ON i.owner = c.index_owner AND i.index_name = c.index_name " +
//          "WHERE i.table_name = :table " +
//          "GROUP BY i.index_name, i.uniqueness, i.table_name")
//      .namedParam("table", tableName.toUpperCase())
//      .listResult(new IndexMapper())
//      // On filtre les clé primaires
//      .stream().filter(index -> !index.isPrimaryKeyIndex(columns)).toList();

  }

  public Uni<Table> fetchDatasInBatchAndProcess(Table table, BatchProcessor batchProcessor) {
    log.info("6");

    String paginationSql =
      "SELECT " + table.getColumns().stream().map(Column::getName).collect(Collectors.joining(", ")) +
        " FROM (" +
        "  SELECT t.*, ROW_NUMBER() OVER (ORDER BY " + table.getPrimaryKey().getName() + ") AS rn " +
        "  FROM " + table.getName() + " t" +
        ") WHERE rn BETWEEN ? AND ?";

    return batchProcessor.withTransaction(sqlConnection -> {
      AtomicInteger currentStartRow = new AtomicInteger(1);

      return Multi.createBy().repeating()
        .uni(() ->
          Uni.createFrom()
            .item(() -> {
              int startRow = currentStartRow.get();
              currentStartRow.addAndGet(batchSize);
              int endRow = startRow + batchSize - 1;
              return Tuple.of(startRow, endRow);
            })
            .chain(tuple -> oraPool.preparedQuery(paginationSql).execute(tuple))
            .chain(result -> {
              List<Map<String, Object>> batch = MapMapper.map(result);
              // I can probably do better
              if (batch.isEmpty()) {
                return Uni.createFrom().failure(new RuntimeException("STOP"));
              }

              return batchProcessor.processBatch(batch, table, sqlConnection)
                .map(ignored -> batch.size());
            })
        )
        .whilst(batchSize -> Objects.equals(batchSize, this.batchSize)) // Continue while batches are full
        .onFailure(throwable -> {
          log.error("Error :", throwable);
          return "STOP".equals(throwable.getMessage());
        })
        .recoverWithItem(ignored -> null) // Convert the "STOP" signal to success
        .toUni().replaceWith(table);
    });
  }
}
