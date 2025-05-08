package com.fvd.jooq.db.model;

import com.fvd.jooq.db.PostgresQueries;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class Index {
  final String name;
  final String tableName;
  final Boolean uniqueness;
  final List<String> columnNames;

  public boolean isPrimaryKeyIndex(List<Column> columnsList) {
    if (columnNames.isEmpty() || columnsList.isEmpty()) {
      return false;
    }

    List<Column> columnListMatch = columnsList.stream()
      .filter(col -> col instanceof PrimaryKey && columnNames.contains(col.getName())).toList();

    return columnListMatch.stream().map(Column::getName).toList().containsAll(columnNames);
  }

  public String getCreateIndexString(String schema) {
    return "CREATE " + (uniqueness ? "UNIQUE " : "") + "INDEX " + name + " ON " + PostgresQueries.getTableName(tableName, schema) +
      " (" + String.join(", ", columnNames) + ")";
  }
}
