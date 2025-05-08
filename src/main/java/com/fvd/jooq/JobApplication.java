package com.fvd.jooq;

import com.fvd.jooq.db.OracleQueries;
import com.fvd.jooq.db.PostgresQueries;
import com.fvd.jooq.db.model.Table;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@Slf4j
@QuarkusMain
@RequiredArgsConstructor
public class JobApplication implements QuarkusApplication {

  private final PostgresQueries postgresQueries;
  private final OracleQueries oracleQueries;

  @ConfigProperty(name = "com.fvd.app.tables")
  List<String> tableNames;

  @Override
  @SneakyThrows
  public int run(String... args) throws Exception {
    log.info("start");
    //TODO prevoir un try catch pour
    // - D'abord récupérer les tables prééxistantes et créer des tables temporaires avec un prefix TMP_ par exemple
    // - Ensuite on drop les tables "normals" et on travail dessus
    // - En cas d'erreur on drop tout les tables et on recréé les tables "normals" avec les tables temporaires
    // - En finally on supprime les tables temporaires.
    // TODO prevoir de drop le schema avec une property si on veut forcer un schema propre
    postgresQueries.createSchemaIfNotExist();
    tableNames.reversed().forEach(postgresQueries::dropTableIfExists);
    tableNames.forEach(tableName -> {
      Table table = oracleQueries.findTableDefinition(tableName);
      log.info("Table def found : {}", table);
      postgresQueries.createTableDefinition(table);
      oracleQueries.fetchDatasInBatchAndProcess(table, postgresQueries);
    });
    log.info("end");
    return 0;
  }
}
