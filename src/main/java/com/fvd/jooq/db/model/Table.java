package com.fvd.jooq.db.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class Table {
  final String name;
  final List<Column> columns;
  final List<Index> indexes;

  public PrimaryKey getPrimaryKey() {
    return columns.stream()
      .filter(col -> col instanceof PrimaryKey)
      .map(PrimaryKey.class::cast)
      .findFirst().orElse(null);
  }
}
