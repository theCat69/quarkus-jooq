package com.fvd.jooq;

import com.fvd.jooq.db.mapper.MapMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.mutiny.sqlclient.Pool;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@RequiredArgsConstructor
public class MainTest {
  final Pool pgPool;
  final JobApplication jobApplication;

  @Test
  @SneakyThrows
  void mainTest() {
    //given liquibase
    //When
    jobApplication.run();
    //Then
    var depts = pgPool.query("SELECT * FROM POST_SCHEMA.DEPT").execute()
      .map(MapMapper::map)
      .await()
      .atMost(Duration.ofSeconds(10));

    var emps = pgPool.query("SELECT * FROM POST_SCHEMA.EMP").execute()
      .map(MapMapper::map)
      .await()
      .atMost(Duration.ofSeconds(10));

    assertThat(depts).hasSize(32);
    assertThat(emps).hasSize(2);

  }
}
