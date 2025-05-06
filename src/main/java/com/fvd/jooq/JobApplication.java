package com.fvd.jooq;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStepN;

import java.util.List;
import java.util.Map;

@Slf4j
@QuarkusMain
public class JobApplication implements QuarkusApplication {

  @Inject
  @Named("dslAlt")
  DSLContext dslAlt;
  @Inject
  DSLContext postgresDsl;

  @Override
  @SneakyThrows
  public int run(String... args) throws Exception {
    log.info("start");
    //verify nothing is inside
    dslAlt.meta().getSchemas("public")
      .getFirst().getTables().forEach(table -> log.info("table in alt before : {}", table));
    //migrate
    postgresDsl.meta().getSchemas("public")
      .getFirst().getTables().forEach(table -> {
        log.info("table found : {}", table);
        //Drop old data
        dslAlt.dropTableIfExists(table)
          .execute();
        // Create table if not exist
        dslAlt.createTableIfNotExists(table.getName())
          .columns(table.fields())
          .execute();
        // Fetch records
        List<Map<String, Object>> maps = postgresDsl.selectFrom(table).fetch().intoMaps();
        //Insert records
        InsertValuesStepN<?> insertStep = dslAlt.insertInto(table, table.fields());
        maps.forEach(map ->
          insertStep.values(map.values())
        );
        insertStep.execute();
      });
    //Verify
    dslAlt.meta().getSchemas("public")
      .getFirst().getTables().forEach(table -> {
        log.info("table in alt after : {}", table);
        dslAlt.selectFrom(table).fetch().forEach(record -> {
          log.info("Record found ! {}", record);
        });
      });
    log.info("end");
    return 0;
  }
}
