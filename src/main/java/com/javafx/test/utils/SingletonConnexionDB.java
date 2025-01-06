package com.javafx.test.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class SingletonConnexionDB {
    private static Connection connection;

    private SingletonConnexionDB() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/restaurant_db",
                        "root",
                        "12345"
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
