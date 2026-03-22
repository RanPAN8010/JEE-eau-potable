package com.qualieau.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
<<<<<<< Updated upstream
    private static final String DOCKER_URL = "jdbc:mysql://db-mysql:3306/qualieau_db?serverTimezone=UTC";
    private static final String LOCAL_URL = "jdbc:mysql://localhost:3306/qualieau_db?serverTimezone=UTC";
=======
    // --- ON AJOUTE LES PARAMÈTRES MAGIQUES POUR L'UTF-8 ---
    // On ajoute : &useUnicode=true&characterEncoding=UTF-8
    private static final String DOCKER_URL = "jdbc:mysql://db-mysql:3306/qualieau_db?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static final String LOCAL_URL = "jdbc:mysql://localhost:3306/qualieau_db?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    
    // Assure-toi que le USER et PASSWORD correspondent bien à ton docker-compose.yml
>>>>>>> Stashed changes
    private static final String USER = "root"; 
    private static final String PASSWORD = "root"; 

    public static Connection getConnection() throws SQLException {
        try {
<<<<<<< Updated upstream
            Class.forName("com.mysql.cj.jdbc.Driver");

            try {
                return DriverManager.getConnection(DOCKER_URL, USER, PASSWORD);
            } catch (SQLException e) {
=======
            // Chargement explicite du driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            try {
                // Tentative de connexion via le réseau Docker
                return DriverManager.getConnection(DOCKER_URL, USER, PASSWORD);
            } catch (SQLException e) {
                // Si échec (ex: exécution locale), tentative via localhost
>>>>>>> Stashed changes
                return DriverManager.getConnection(LOCAL_URL, USER, PASSWORD);
            }
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Pilote JDBC MySQL introuvable.", e);
        }
    }
}