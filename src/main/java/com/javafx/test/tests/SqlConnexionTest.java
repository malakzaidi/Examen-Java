package com.javafx.test.tests;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnexionTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/restaurant_db";
        String username = "root"; // Declaring the username variable
        String password = "12345"; // Replace with your MySQL password

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connection successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Connection failed.");
        }
    }
}
