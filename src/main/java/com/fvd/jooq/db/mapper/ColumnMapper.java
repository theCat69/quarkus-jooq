package com.fvd.jooq.db.mapper;

import com.fvd.jooq.db.model.Column;
import com.fvd.jooq.db.model.Column.ColumnBuilder;
import com.fvd.jooq.db.model.ForeignKey;
import com.fvd.jooq.db.model.PrimaryKey;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.RowSet;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ColumnMapper {
  public List<Column> map(RowSet<Row> rs) {
    RowIterator<Row> iter = rs.iterator();
    List<Column> resultList = new ArrayList<>();
    do {
      Row row = iter.next();
      String constraint_types = row.getString("constraint_types");
      List<String> constraintTypes = constraint_types != null ?
        List.of(constraint_types.split("\\|")) :
        List.of();

      ColumnBuilder<?, ?> columnBuilder;
      if (constraintTypes.contains("R")) {
        columnBuilder = ForeignKey.builder()
          .fkTableName(row.getString("fk_table_name"))
          .fkColumnName(row.getString("fk_column_name"));
      } else if (constraintTypes.contains("P")) {
        columnBuilder = PrimaryKey.builder();
      } else {
        columnBuilder = Column.builder();
      }

      Column column = columnBuilder.name(row.getString("column_name"))
        .tableName(row.getString("table_name"))
        .dataType(row.getString("data_type"))
        .dataLength(row.getInteger("data_length"))
        .dataPrecision(row.getInteger("data_precision"))
        .dataScale(row.getInteger("data_scale"))
        .nullable("Y".equalsIgnoreCase(row.getString("nullable")))
        .build();

      resultList.add(column);
    } while (iter.hasNext());

    return resultList;
  }

}
