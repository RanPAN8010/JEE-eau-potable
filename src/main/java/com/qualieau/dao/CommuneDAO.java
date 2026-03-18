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
	 * Permet de trouver des communes en filtrant par nom ou par code INSEE.
	 * * @param query Le terme de recherche (partie du nom ou code INSEE complet).
	 * @return Une {@link List} d'objets {@link Commune} correspondants.
	 * @throws SQLException Si une erreur survient lors de l'accès à la base de données.
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
}
