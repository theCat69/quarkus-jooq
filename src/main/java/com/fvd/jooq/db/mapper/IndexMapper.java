//package com.fvd.jooq.db.mapper;
//
//import com.fvd.jooq.db.model.Index;
//import org.codejargon.fluentjdbc.api.query.Mapper;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
//public class IndexMapper implements Mapper<Index> {
//  @Override
//  public Index map(ResultSet rs) throws SQLException {
//    String columns = rs.getString("columns");
//    List<String> columnNames =  columns != null ?
//      List.of(columns.split("\\|")) :
//      List.of();
//
//
//    return Index.builder()
//      .name(rs.getString("index_name"))
//      .tableName(rs.getString("table_name"))
//      .uniqueness("UNIQUE".equalsIgnoreCase(rs.getString("uniqueness")))
//      .columnNames(columnNames)
//      .build();
//  }
//}
