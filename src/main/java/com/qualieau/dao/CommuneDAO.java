package com.qualieau.dao;

import java.sql.*;
import com.qualieau.model.Commune;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour la gestion des données des communes.
 * Cette classe permet d'effectuer des recherches sur les informations géographiques.
 * * @author Ran
 * @version 1.0
 */
public class CommuneDAO {
	/** La connexion à la base de données. */
	private Connection connection;

	/**
	 * Constructeur pour initialiser le DAO avec une connexion existante.
	 * * @param connection L'objet {@link Connection} pour exécuter les requêtes.
	 */
	public CommuneDAO(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * Mettre en œuvre la logique de recherche principale.
	 * Permet de trouver des communes en filtrant par nom ou par code INSEE ou code postal.
	 * * @param query Le terme de recherche (partie du nom ou code INSEE complet ou code postal).
	 * @return Une {@link List} d'objets {@link Commune} correspondants.
	 * @throws SQLException Si une erreur survient lors de l'accès à la base de données.
	 */
    public List<Commune> searchCommune(String query) throws SQLException {
        List<Commune> list = new ArrayList<>();
        // Critères de recherche : Recherche par nomou code INSEE
        String sql = "SELECT code_insee, nom, code_postal, departement, region, latitude, longitude " +
                "FROM commune WHERE nom LIKE ? OR code_insee = ? OR code_postal = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
        	ps.setString(1, "%" + query + "%");//recherche par nom
            ps.setString(2, query);//par code insee
            ps.setString(3, query);//par code postal
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	Commune c = new Commune();
                c.setCodeInsee(rs.getString("code_insee"));
                c.setNom(rs.getString("nom"));
                c.setCodePostal(rs.getString("code_postal"));
                c.setDepartement(rs.getString("departement"));
                c.setRegion(rs.getString("region"));
                c.setLatitude(rs.getDouble("latitude")); 
                c.setLongitude(rs.getDouble("longitude"));
                list.add(c);
            }
        }
        return list;
    }
    
    /**
     * 新增功能：按省份或大区筛选。 [cite: 15, 73]
     * 对应 DSL 中 "Filtrer par département / région" 的功能。 
     */
    public List<Commune> findByZone(String departement, String region) throws SQLException {
        List<Commune> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM commune WHERE 1=1 ");
        
        if (departement != null && !departement.isEmpty()) sql.append("AND departement = ? ");
        if (region != null && !region.isEmpty()) sql.append("AND region = ? ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (departement != null && !departement.isEmpty()) ps.setString(paramIndex++, departement);
            if (region != null && !region.isEmpty()) ps.setString(paramIndex++, region);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCommune(rs));
                }
            }
        }
        return list;
    }
    
    /**
     * 辅助方法：将 ResultSet 映射到 Commune 对象。
     */
    private Commune mapResultSetToCommune(ResultSet rs) throws SQLException {
        Commune c = new Commune();
        c.setCodeInsee(rs.getString("code_insee"));
        c.setNom(rs.getString("nom"));
        c.setCodePostal(rs.getString("code_postal"));
        c.setDepartement(rs.getString("departement"));
        c.setRegion(rs.getString("region"));
        // 关键：获取经纬度以支持地图可视化
        c.setLatitude(rs.getDouble("latitude"));
        c.setLongitude(rs.getDouble("longitude"));
        return c;
    }
}
