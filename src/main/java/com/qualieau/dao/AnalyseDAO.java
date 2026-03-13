package com.qualieau.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.qualieau.model.Analyse;

public class AnalyseDAO {
	private Connection connection;
	
	public AnalyseDAO(Connection connection) {
        this.connection = connection;
    }

	/**
     * Récupérer tous les enregistrements historiques pour une ville donnée
     */
    public List<Analyse> getAnalyseByCommune(String codeInsee) throws SQLException {
        List<Analyse> list = new ArrayList<>();
        // La requête SQL doit ici utiliser l'index idx_insee_analyse
        // Le temps de réponse doit être inférieur à 2 secondes
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
