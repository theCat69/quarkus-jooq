package com.fvd.jooq.db;

import com.fvd.jooq.db.batching.BatchProcessor;
import com.fvd.jooq.db.model.Column;
import com.fvd.jooq.db.model.Table;
import io.smallrye.mutiny.Uni;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@ApplicationScoped
public class PostgresQueries implements BatchProcessor {

  @ConfigProperty(name = "quarkus.liquibase.schemas", defaultValue = "")
  String schema;

  private final Pool pgPool;

  @Inject
  public PostgresQueries(Pool pgPool) {
    this.pgPool = pgPool;
  }

  public Uni<Void> createSchemaIfNotExist() {
    if (StringUtils.isNotBlank(schema)) {
      log.info("1");
      return pgPool.query("CREATE SCHEMA IF NOT EXISTS " + schema)
        .execute().replaceWithVoid();
    }
    return Uni.createFrom().voidItem();
  }

  public Uni<Void> dropTableIfExists(String tableName) {
    log.info("2");
    return pgPool.query("DROP TABLE IF EXISTS " + getTableName(tableName, schema))
      .execute().replaceWithVoid();
  }

  public static String getTableName(String tableName, String schema) {
    return StringUtils.isNotBlank(schema) ? schema + "." + tableName : tableName;
  }

  @SneakyThrows
  public Uni<Table> createTableDefinition(Table table) {
    log.info("3");
    //create table
    return pgPool.query("CREATE TABLE " + getTableName(table.getName(), schema) + " (" +
      table.getColumns().stream().map(col -> col.getCreateTableString(schema)).collect(Collectors.joining(", ")) +
      " )").execute().replaceWith(table);
  }

  @Override
  public Uni<Void> processBatch(List<Map<String, Object>> batch, Table table, SqlConnection sqlConnection) {
    log.info("4");
    return sqlConnection.preparedQuery("INSERT INTO " + getTableName(table.getName(), schema) + " (" +
        table.getColumns().stream().map(Column::getName).collect(Collectors.joining(", ")) +
        ") VALUES (" + IntStream.range(0, table.getColumns().size())
        .mapToObj(i -> {
          i = i + 1;
          return "$" + i;
        }).collect(Collectors.joining(", ")) + ")")
      .executeBatch(batch.stream().map(map -> Tuple.from(map.values().toArray())).toList())
      .invoke(() -> log.info("Processed {} elements on table {}", batch.size(), table.getName()))
      .replaceWithVoid();
  }

  @Override
  public <T> Uni<@Nullable T> withTransaction(Function<SqlConnection, Uni<@Nullable T>> function) {
    return pgPool.withTransaction(function);
  }

}
