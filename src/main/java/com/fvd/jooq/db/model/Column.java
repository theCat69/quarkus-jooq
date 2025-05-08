package com.fvd.jooq.db.model;

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
    return " " + name + " " + mapOracleTypeToPostgres(this) + " " + (nullable ? "" : "NOT NULL ");
  }

  public static String mapOracleTypeToPostgres(Column column) {
    String oracleType = column.getDataType();

    return switch (oracleType) {
      case "NUMBER" -> {
        if (column.getDataPrecision()== null) {
          yield "NUMERIC";
        } else if (column.getDataScale() != null && column.getDataScale() > 0) {
          yield "NUMERIC(" + column.getDataPrecision() + "," + column.getDataScale() + ")";
        } else if (column.getDataPrecision() <= 4) {
          yield "SMALLINT";
        } else if (column.getDataPrecision() <= 9) {
          yield  "INTEGER";
        } else if (column.getDataPrecision() <= 18) {
          yield  "BIGINT";
        } else {
          yield "NUMERIC(" + column.getDataPrecision() + ")";
        }
      }
      case "VARCHAR2", "NVARCHAR2" -> "VARCHAR(" + column.getDataLength() + ")";
      case "CHAR", "NCHAR" -> "CHAR(" + column.getDataLength() + ")";
      case "CLOB", "NCLOB" -> "TEXT";
      case "BLOB", "RAW" -> "BYTEA";
      case "FLOAT" -> "DOUBLE PRECISION";
      default -> oracleType;
    };
  }
}
