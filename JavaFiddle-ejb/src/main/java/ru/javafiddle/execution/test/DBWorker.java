package ru.javafiddle.execution.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by nk on 13.03.2016.
 */
public class DBWorker {
    private final String HOST = "jdbc:postgresql://localhost:5435/javafiddle";
    private final String USERNAME = "javafiddle";
    private final String PASSWORD = "javafiddle";

    private Connection connection;

    public DBWorker() {
        try {
            connection = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
