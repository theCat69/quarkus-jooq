package com.fvd.jooq.db;

import com.fvd.jooq.db.batching.BatchProcessor;
import com.fvd.jooq.db.model.Table;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class PostgresQueries implements BatchProcessor {

  @ConfigProperty(name = "quarkus.liquibase.schemas", defaultValue = "")
  String schema;

  private final FluentJdbc postgresJdbc;

  @Inject
  public PostgresQueries(AgroalDataSource dataSource) {
    this.postgresJdbc = JdbcFluentFactory.createFluentJdbc(dataSource);
  }

  public void createSchemaIfNotExist() {
    if (StringUtils.isNotBlank(schema)) {
      postgresJdbc.query().update("CREATE SCHEMA IF NOT EXISTS " + schema).run();
    }
  }

  public void dropTableIfExists(String tableName) {
    postgresJdbc.query().update("DROP TABLE IF EXISTS " + getTableName(tableName, schema)).run();
  }

  public static String getTableName(String tableName, String schema) {
    return StringUtils.isNotBlank(schema) ? schema + "." + tableName : tableName;
  }

  public void createTableDefinition(Table table) {
    //create table
    postgresJdbc.query().update("CREATE TABLE " + getTableName(table.getName(), schema) + " (" +
      table.getColumns().stream().map(col -> col.getCreateTableString(schema)).collect(Collectors.joining(", ")) +
      " )").run();
    //add indexes
    table.getIndexes().forEach(index -> postgresJdbc.query().update(index.getCreateIndexString(schema)).run());
  }

  @Override
  public void processBatch(List<Map<String, Object>> batch) {
    log.info("Result found ! {} ", batch);
//    postgresJdbc.query().update("INSERT INTO")
  }
}
