package com.alexgrig;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcRunner {

    public static void main(String[] args) {

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
}
