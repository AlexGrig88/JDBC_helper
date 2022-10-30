package com.alexgrig;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {

    public static void main(String[] args) {
        try {
            var ticketsByFlightId = getTicketsByFlightId(2L);
            ticketsByFlightId.forEach(System.out::println);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    /*
        EXAMPLES for PreparedStatement interface
    * */

    private static List<Long> getFlightsBetween(LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = "SELECT id FROM flight WHERE departure_date BETWEEN ? AND ?";
        List<Long> result = new ArrayList<>();
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setFetchSize(50);
            preparedStatement.setQueryTimeout(10);
            preparedStatement.setMaxRows(100);

            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));
            System.out.println(preparedStatement);

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getLong("id"));
            }
        }

        return result;
    }

    private static List<Long> getTicketsByFlightId(Long flightId) throws SQLException {
        String sql = "SELECT id FROM ticket WHERE flight_id = ? ";
        List<Long> result = new ArrayList<>();
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, flightId);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
//                result.add(resultSet.getLong("id"));
                result.add(resultSet.getObject("id", Long.class)); // NULL safe
            }
        }

        return result;
    }

    private static void exampleStatement() {
        //        String sqlCreate = "CREATE TABLE IF NOT EXISTS info (id SERIAL PRIMARY KEY , data TEXT NOT NULL )";
//        String sqlInsert = "INSERT INTO info (data) VALUES ('test1'), ('test2'), ('test3');";
        String query = "SELECT id, passenger_name FROM ticket;";

        try (Connection connection = ConnectionManager.open();
             Statement statement = connection.createStatement()) {

            System.out.println(connection.getTransactionIsolation());
            System.out.println(connection.getSchema());
//           boolean executeResult = statement.execute(sqlInsert);
//           System.out.println("Добавлено строк: " + statement.getUpdateCount());
//           statement.executeUpdate(sqlInsert);
//           System.out.println(executeResult);
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                System.out.println("id = " + resultSet.getLong("id"));
                System.out.println("passenger name: " + resultSet.getString("passenger_name"));
                System.out.println("------------------");
            }

        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    private static void checkMetaData() throws SQLException {
        try (var connection = ConnectionManager.open()) {
            var metaData = connection.getMetaData();
            var catalogs = metaData.getCatalogs();
            while (catalogs.next()) {
                var catalog = catalogs.getString(1);
                var schemas = metaData.getSchemas();
                while (schemas.next()) {
                    var schema = schemas.getString("TABLE_SCHEM");
                    var tables = metaData.getTables(catalog, schema, "%", new String[] {"TABLE"});
                    if (schema.equals("public")) {
                        while (tables.next()) {
                            System.out.println(tables.getString("TABLE_NAME"));
                        }
                    }
                }
            }
        }
    }
}