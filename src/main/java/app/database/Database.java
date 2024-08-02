package app.database;

import java.sql.*;

public class Database {
    private Connection connection;
    private Statement statement;

    public Database(final String dbPath) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public ResultSet getResultSet(String table, String condition) {
        String query = "SELECT * FROM " + table;
        if (condition != null && !condition.isEmpty()) {
            query += " WHERE " + condition;
        }
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void addTuple(String table, String tupleType, String value) {
        String sql = "INSERT INTO " + table + " (" + tupleType + ") VALUES " + value;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTuple(String table, String value, String condition) {
        String sql = "UPDATE " + table + " SET " + value + " WHERE " + condition;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeTuple(String table, String condition) {
        String sql = "DELETE FROM " + table + " WHERE " + condition;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
