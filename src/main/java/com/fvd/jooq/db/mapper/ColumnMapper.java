package com.fvd.jooq.db.mapper;

import com.fvd.jooq.db.model.Column;
import com.fvd.jooq.db.model.Column.ColumnBuilder;
import com.fvd.jooq.db.model.ForeignKey;
import com.fvd.jooq.db.model.PrimaryKey;
import org.codejargon.fluentjdbc.api.query.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ColumnMapper implements Mapper<Column> {
  @Override
  public Column map(ResultSet rs) throws SQLException {

    String constraint_types = rs.getString("constraint_types");
    List<String> constraintTypes = constraint_types != null ?
      List.of(constraint_types.split("\\|")) :
      List.of();

    ColumnBuilder<?, ?> columnBuilder;
    if (constraintTypes.contains("R")) {
      columnBuilder = ForeignKey.builder()
        .fkTableName(rs.getString("fk_table_name"))
        .fkColumnName(rs.getString("fk_column_name"));
    } else if (constraintTypes.contains("P")) {
      columnBuilder = PrimaryKey.builder();
    } else {
      columnBuilder = Column.builder();
    }

    return columnBuilder.name(rs.getString("column_name"))
      .tableName(rs.getString("table_name"))
      .dataType(rs.getString("data_type"))
      .dataLength(rs.getInt("data_length"))
      .dataPrecision(rs.getInt("data_precision"))
      .dataScale(rs.getInt("data_scale"))
      .nullable("Y".equalsIgnoreCase(rs.getString("nullable")))
      .build();
  }
}
