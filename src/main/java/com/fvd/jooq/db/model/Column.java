package com.fvd.jooq.db.model;

import com.fvd.jooq.db.mapper.ColumnTypeMapper;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString
public class Column {
  final String name;
  final String tableName;
  final String dataType;
  final Integer dataLength;
  final Integer dataPrecision;
  final Integer dataScale;
  final Boolean nullable;

  public String getCreateTableString(String schema) {
    return " " + name + " " + ColumnTypeMapper.mapOracleTypeToPostgres(this) + " " + (nullable ? "" : "NOT NULL ");
  }
}
