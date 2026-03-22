package com.qualieau.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
<<<<<<< Updated upstream
import com.qualieau.model.Analyse;
/**
 * Classe DAO pour la gestion des analyses de l'eau.
 * Elle permet d'interagir avec la table "Analyse" dans la base de données.
 * * @author Ran
 * @version 1.0
 */
public class AnalyseDAO {
	/** La connexion active à la base de données. */
	private Connection connection;

	/**
     * Initialise le DAO avec une connexion SQL.
     * * @param connection L'objet {@link Connection} à utiliser pour les opérations.
     */	
	public AnalyseDAO(Connection connection) {
        this.connection = connection;
    }

	/**
     * Récupère tous les enregistrements historiques pour une ville donnée via son code INSEE.
     * Les résultats sont classés par date de prélèvement de la plus récente à la plus ancienne.
     *
     * @param codeInsee Le code INSEE identifiant la commune.
     * @return Une {@link List} d'objets {@link Analyse} correspondant aux critères.
     * @throws SQLException Si une erreur de communication avec la base de données survient.
     */
    public List<Analyse> getAnalyseByCommune(String codeInsee) throws SQLException {
        List<Analyse> list = new ArrayList<>();
        String sql = "SELECT * FROM Analyse WHERE code_insee = ? ORDER BY date_prelevement DESC";
        //Toutes les requêtes utilisent PreparedStatement afin d'empêcher les injections SQL
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codeInsee);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Analyse a = new Analyse();
                a.setDatePrelevement(rs.getDate("date_prelevement").toLocalDate()); // [cite: 50, 160]
                a.setParametre(rs.getString("parametre"));
                a.setValeur(rs.getDouble("valeur"));     
                a.setUnite(rs.getString("unite"));    
                a.setConforme(rs.getBoolean("conforme")); 
                list.add(a);
=======
import java.nio.charset.StandardCharsets; 
import com.qualieau.model.Analyse;

public class AnalyseDAO {
    private Connection connection;

    public AnalyseDAO(Connection connection) {
        this.connection = connection;
    }

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
                    
                    // --- LE DÉCODEUR MAGIQUE (RÉPARATION À LA VOLÉE) ---
                    String parametreStr = rs.getString("parametre");
                    if (parametreStr != null) {
                        // Si on voit un caractère cassé "Ã", on traduit ISO -> UTF-8
                        if (parametreStr.contains("Ã")) {
                            parametreStr = new String(parametreStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        }
                        a.setParametre(parametreStr);
                    }
                    
                    String uniteStr = rs.getString("unite");
                    if (uniteStr != null) {
                        if (uniteStr.contains("Ã")) {
                            uniteStr = new String(uniteStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        }
                        a.setUnite(uniteStr);
                    }
                    // --------------------------------------------------
                    
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
>>>>>>> Stashed changes
            }
        }
        return list;
    }
<<<<<<< Updated upstream
}
=======
}
>>>>>>> Stashed changes
