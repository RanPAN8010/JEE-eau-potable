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
    public List<Commune> searchCommune(String query) throws SQLException {
        List<Commune> list = new ArrayList<>();
        // Critères de recherche : Recherche par nomou code INSEE
        String sql = "SELECT code_insee, nom FROM Commune WHERE nom LIKE ? OR code_insee = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
        	ps.setString(1, "%" + query + "%"); // 模糊匹配名字
            ps.setString(2, query);             // 精确匹配 INSEE 代码      
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	Commune c = new Commune();
                c.setCodeInsee(rs.getString("code_insee"));
                c.setNom(rs.getString("nom"));
                list.add(c);
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
