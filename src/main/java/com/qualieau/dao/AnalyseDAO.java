package com.qualieau.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
            }
        }
        return list;
    }
}
