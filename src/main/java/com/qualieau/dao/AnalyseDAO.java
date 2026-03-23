package com.qualieau.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.qualieau.model.Analyse;

/**
 * Classe DAO pour la gestion des analyses de l'eau.
 * Elle permet d'interagir avec la table "Analyse" dans la base de données.
 * @author Ran
 * @version 1.1
 */
public class AnalyseDAO {
    /** La connexion active à la base de données. */
    private Connection connection;

    /**
     * Initialise le DAO avec une connexion SQL.
     * @param connection L'objet {@link Connection} à utiliser pour les opérations.
     */ 
    public AnalyseDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Récupère les analyses en fonction du type de recherche (commune ou département).
     * Limite les résultats à 1000 pour éviter de surcharger le réseau.
     *
     * @param type "insee" ou "departement"
     * @param param Le code INSEE ou le code département
     * @return Une liste d'analyses
     */
    public List<Analyse> searchAnalyses(String type, String param) throws SQLException {
        List<Analyse> list = new ArrayList<>();
        String sql;
        
        if ("departement".equals(type)) {
            sql = "SELECT * FROM analyse WHERE code_insee LIKE ? ORDER BY date_prelevement DESC LIMIT 1000";
        } else {
            sql = "SELECT * FROM analyse WHERE code_insee = ? ORDER BY date_prelevement DESC LIMIT 1000";
        }
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if ("departement".equals(type)) {
                ps.setString(1, param + "%"); 
            } else {
                ps.setString(1, param);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Analyse a = new Analyse();
                    
                    Date date = rs.getDate("date_prelevement");
                    if (date != null) a.setDatePrelevement(date.toLocalDate());
                    
                    // L'encodage est maintenant géré par la DBConnection, plus besoin de traduction ISO/UTF-8 !
                    a.setParametre(rs.getString("parametre"));
                    a.setUnite(rs.getString("unite"));
                    
                    String valStr = rs.getString("valeur");
                    try { 
                        if (valStr != null) a.setValeur(Double.parseDouble(valStr.replace(",", "."))); 
                    } catch (Exception e) { 
                        a.setValeur(0.0); 
                    }
                    
                    String conformeStr = rs.getString("conforme");
                    a.setConforme(conformeStr != null && conformeStr.trim().equalsIgnoreCase("C"));
                    
                    list.add(a);
                }
            }
        }
        return list;
    }
}