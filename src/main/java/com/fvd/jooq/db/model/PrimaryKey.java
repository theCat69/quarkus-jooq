package com.fvd.jooq.db.model;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString(callSuper = true)
public class PrimaryKey extends Column {
  @Override
  public String getCreateTableString(String schema) {
    return super.getCreateTableString(schema) + " CONSTRAINT pk_" + super.getTableName() + " PRIMARY KEY";
  }
}
