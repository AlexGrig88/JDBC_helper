package com.alexgrig;


import com.alexgrig.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionRunner {

    public static void main(String[] args) throws SQLException {
        long flightId = 8;
        var deleteFlightSql = "DELETE FROM flight WHERE id = ?";
        var deleteTicketsSql = "DELETE FROM ticket WHERE flight_id = ?";
        Connection connection = null;
        PreparedStatement deleteFlightStatement = null;
        PreparedStatement deleteTicketsStatement = null;
        try {
            connection = ConnectionManager.open();
            deleteFlightStatement = connection.prepareStatement(deleteFlightSql);
            deleteTicketsStatement = connection.prepareStatement(deleteTicketsSql);

            connection.setAutoCommit(false);

            deleteFlightStatement.setLong(1, flightId);
            deleteTicketsStatement.setLong(1, flightId);

            deleteTicketsStatement.executeUpdate();
            if (true) {
                throw new RuntimeException("Ooops");
            }
            deleteFlightStatement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);

        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (deleteFlightStatement != null) {
                deleteFlightStatement.close();
            }
            if (deleteTicketsStatement != null) {
                deleteTicketsStatement.close();
            }
        }
    }

    // Пример работы с отправкой нескольких запросов, упакованных в batch
    // (имеет смысл для операций delete/update/insert или DDL)
    public static void exampleBatching() throws SQLException {
        long flightId = 8;
        var deleteFlightSql = "DELETE FROM flight WHERE id = " + flightId;
        var deleteTicketsSql = "DELETE FROM ticket WHERE flight_id = " + flightId;
        var createTableSql = "CREATE TABLE test4 (id SERIAL PRIMARY KEY);";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionManager.open();
            connection.setAutoCommit(false);

            statement = connection.createStatement();
            statement.addBatch(deleteTicketsSql);
            statement.addBatch(deleteFlightSql);
            statement.addBatch(createTableSql);

            var ints = statement.executeBatch();

            connection.commit();
            connection.setAutoCommit(true);

        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }
}
