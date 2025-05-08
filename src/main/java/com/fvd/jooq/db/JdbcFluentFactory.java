package com.fvd.jooq.db;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.listen.AfterQueryListener;
import org.codejargon.fluentjdbc.api.query.listen.ExecutionDetails;

import javax.sql.DataSource;

@UtilityClass
@Slf4j
public class JdbcFluentFactory {

  public FluentJdbc createFluentJdbc(DataSource dataSource, DBType dbType) {
    return new FluentJdbcBuilder()
      .connectionProvider(dataSource)
      .afterQueryListener(new AppQueryListener(dbType))
      .build();
  }

  public enum DBType {
    POSTGRES,
    ORACLE
  }

  private record AppQueryListener(DBType dbType) implements AfterQueryListener {
    @Override
    public void listen(ExecutionDetails execution) {
      if (execution.success()) {
        log.debug("Query on {} took {} ms to execute: {}", dbType, execution.executionTimeMs(), execution.sql());
      }
    }
  }

}
