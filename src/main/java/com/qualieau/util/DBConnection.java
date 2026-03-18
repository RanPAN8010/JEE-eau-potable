package com.qualieau.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DOCKER_URL = "jdbc:mysql://db-mysql:3306/qualieau_db?serverTimezone=UTC";
    private static final String LOCAL_URL = "jdbc:mysql://localhost:3306/qualieau_db?serverTimezone=UTC";
    private static final String USER = "root"; 
    private static final String PASSWORD = "root"; 

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try {
                return DriverManager.getConnection(DOCKER_URL, USER, PASSWORD);
            } catch (SQLException e) {
                return DriverManager.getConnection(LOCAL_URL, USER, PASSWORD);
            }
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Pilote JDBC MySQL introuvable.", e);
        }
    }
}