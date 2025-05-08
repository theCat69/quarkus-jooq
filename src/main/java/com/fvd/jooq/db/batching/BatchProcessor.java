package com.fvd.jooq.db.batching;

import com.fvd.jooq.db.model.Table;

import java.util.List;
import java.util.Map;

public interface BatchProcessor {
  void processBatch(List<Map<String, Object>> batch, Table table);
}