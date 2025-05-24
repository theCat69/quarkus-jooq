package com.fvd.jooq.db.model;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString(callSuper = true)
public class ForeignKey extends Column {
  private final String fkTableName;
  private final String fkColumnName;

}
