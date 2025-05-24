package com.fvd.jooq.db.batching;

import com.fvd.jooq.db.model.Table;
import io.smallrye.mutiny.Uni;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.mutiny.sqlclient.SqlConnection;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface BatchProcessor {
  Uni<Void> processBatch(List<Map<String, Object>> batch, Table table, SqlConnection sqlConnection);
  <T> Uni<@Nullable T> withTransaction(Function<SqlConnection, Uni<@Nullable T>> function);
}