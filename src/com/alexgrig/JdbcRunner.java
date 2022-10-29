package com.alexgrig;


import java.sql.Connection;
import java.sql.SQLException;

public class JdbcRunner {

    public static void main(String[] args) {
       try (Connection connection = ConnectionManager.open()) {

           System.out.println(connection.getTransactionIsolation());

       } catch (SQLException ex) {
           System.out.println("error " + ex.getSQLState());
       }
    }
}
