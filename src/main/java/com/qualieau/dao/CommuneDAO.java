package com.qualieau.dao;

import java.sql.*;
import com.qualieau.model.Commune;
import java.util.ArrayList;
import java.util.List;

public class CommuneDAO {
	private Connection connection;

	public CommuneDAO(Connection connection) {
		this.connection = connection;
	}
	
	/**
     * Mettre en œuvre la logique de recherche principale
     */
    public List<Commune> searchCommune(String keyword) throws SQLException {
        List<Commune> list = new ArrayList<>();
        // Critères de recherche : Recherche par nom, code postal ou code INSEE
        String sql = "SELECT * FROM Commune WHERE nom LIKE ? OR code_postal = ? OR code_insee = ? LIMIT 50";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, keyword + "%"); 
            ps.setString(2, keyword);      
            ps.setString(3, keyword);       
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapToCommune(rs));
            }
        }
        return list;
    }

    private Commune mapToCommune(ResultSet rs) throws SQLException {
        Commune c = new Commune();
        c.setNom(rs.getString("nom"));             
        c.setCodePostal(rs.getString("code_postal")); 
        c.setDepartement(rs.getString("departement")); 
        c.setRegion(rs.getString("region"));        
        c.setCodeInsee(rs.getString("code_insee"));   
        return c;
    }
}
