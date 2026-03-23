package com.qualieau.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // 1. URL pour les requêtes à l'intérieur de Docker (port 3306 classique + UTF-8)
    private static final String DOCKER_URL = "jdbc:mysql://db-mysql:3306/qualieau_db?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    
    // 2. URL pour tes tests locaux sur ton PC (port 3307 du docker-compose + UTF-8)
    // Remplace 3307 par 3306
private static final String LOCAL_URL = "jdbc:mysql://localhost:3307/qualieau_db?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";

    private static final String USER = "root"; 
    private static final String PASSWORD = "root"; 

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try {
                // Tentative 1 : On est dans Docker ?
                return DriverManager.getConnection(DOCKER_URL, USER, PASSWORD);
            } catch (SQLException e) {
                // Tentative 2 : On est sur le PC pour lancer les tests Maven ?
                return DriverManager.getConnection(LOCAL_URL, USER, PASSWORD);
            }
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Pilote JDBC MySQL introuvable.", e);
        }
    }
}