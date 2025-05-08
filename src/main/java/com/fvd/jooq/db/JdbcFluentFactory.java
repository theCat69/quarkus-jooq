package com.fvd.jooq.db;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.listen.AfterQueryListener;

import javax.sql.DataSource;

@UtilityClass
@Slf4j
public class JdbcFluentFactory {

  private final AfterQueryListener listener = execution -> {
    if (execution.success()) {
      log.debug("Query took {} ms to execute: {}", execution.executionTimeMs(), execution.sql());
    }
  };

  public FluentJdbc createFluentJdbc(DataSource dataSource) {
    return new FluentJdbcBuilder()
      .connectionProvider(dataSource)
      .afterQueryListener(listener)
      .build();
  }
}
