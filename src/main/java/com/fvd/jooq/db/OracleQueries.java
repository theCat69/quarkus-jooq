package com.fvd.jooq.db;

import com.fvd.jooq.db.mapper.ColumnMapper;
import com.fvd.jooq.db.mapper.IndexMapper;
import com.fvd.jooq.db.model.Column;
import com.fvd.jooq.db.model.Index;
import com.fvd.jooq.db.model.Table;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.codejargon.fluentjdbc.api.FluentJdbc;

import java.util.List;

@ApplicationScoped
public class OracleQueries {

  private final FluentJdbc oracleJdbc;

  @Inject
  public OracleQueries(@DataSource("alt") AgroalDataSource datasource) {
    this.oracleJdbc = JdbcFluentFactory.createFluentJdbc(datasource);
  }

  public Table findTableDefinition(String tableName) {
    // Get column definitions
    List<Column> columns = oracleJdbc.query().select(
        "SELECT atc.column_name, atc.data_type, atc.data_length, atc.data_precision, atc.data_scale, atc.nullable, atc.table_name, " +
          " MAX(acc_fk.table_name) as fk_table_name, MAX(acc_fk.column_name) as fk_column_name, " +
          "    LISTAGG(ac.constraint_type, '|') within GROUP (order by atc.column_id) as constraint_types" +
          "          FROM all_tab_columns atc" +
          "          LEFT JOIN all_cons_columns acc on acc.table_name = atc.table_name and acc.column_name = atc.column_name" +
          "          LEFT JOIN all_constraints ac on acc.constraint_name = ac.constraint_name" +
          "          LEFT JOIN all_cons_columns acc_fk on acc_fk.constraint_name = ac.r_constraint_name" +
          "          WHERE atc.table_name = :table" +
          "          GROUP BY atc.column_name, atc.data_type, atc.data_length, atc.data_precision, atc.data_scale, atc.nullable, atc.table_name, " +
          "          atc.column_id" +
          " ORDER BY atc.column_id")
      .namedParam("table", tableName.toUpperCase())
      .listResult(new ColumnMapper());

    List<Index> indexes = oracleJdbc.query().select(
        "SELECT i.index_name, i.uniqueness, i.table_name, " +
          "LISTAGG(c.column_name, '|') WITHIN GROUP (ORDER BY c.column_position) as columns " +
          "FROM all_indexes i " +
          "JOIN all_ind_columns c ON i.owner = c.index_owner AND i.index_name = c.index_name " +
          "WHERE i.table_name = :table " +
          "GROUP BY i.index_name, i.uniqueness, i.table_name")
      .namedParam("table", tableName.toUpperCase())
      .listResult(new IndexMapper())
      .stream().filter(index -> !index.isPrimaryKeyIndex(columns)).toList();

    return Table.builder()
      .name(tableName)
      .columns(columns)
      .indexes(indexes)
      .build();
  }
}
