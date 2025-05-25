package com.fvd.jooq.db.mapper;


import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.RowSet;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class MapMapper {
  public List<Map<String, Object>> map(RowSet<Row> rs) {
    List<String> columnNames = rs.columnsNames();
    RowIterator<Row> iter = rs.iterator();
    List<Map<String, Object>> resultList = new ArrayList<>();

    while(iter.hasNext()) {
      Row row = iter.next();
      Map<String, Object> rowMap = new LinkedHashMap<>();
      for(String columnName: columnNames) {
        rowMap.put(columnName, row.getValue(columnName));
      }
      resultList.add(rowMap);
    }

    return resultList;
  }
}
