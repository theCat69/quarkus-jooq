package com.fvd.jooq.db.model;

import com.fvd.jooq.db.PostgresQueries;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString(callSuper = true)
public class ForeignKey extends Column {
  private final String fkTableName;
  private final String fkColumnName;

  @Override
  public String getCreateTableString(String schema) {
    return super.getCreateTableString(schema) + " references " + PostgresQueries.getTableName(fkTableName, schema) +
      " (" + fkColumnName + ")";
  }
}
