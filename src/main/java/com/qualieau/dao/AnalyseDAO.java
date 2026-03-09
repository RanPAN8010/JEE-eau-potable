package com.qualieau.dao;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class AnalyseDAO {

	// 根据市镇 ID 获取分析记录，并确保查询响应 < 2s [cite: 68]
    public List<Analyse> getAnalysesByCommune(int communeId) throws SQLException {
        List<Analyse> analyses = new ArrayList<>();
        String sql = "SELECT * FROM Analyse WHERE commune_id = ?"; // 使用了索引列 [cite: 70]
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, communeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
            	Analyse a = new Analyse();
                // 从数据库列映射到 Java 对象属性
                a.setId(rs.getInt("id"));
                a.setDate(rs.getDate("date_prelevement")); // 对应 UI: Date
                a.setParametre(rs.getString("parametre")); // 对应 UI: Paramètre
                a.setValeur(rs.getDouble("valeur"));       // 对应 UI: Valeur
                a.setUnite(rs.getString("unite"));         // 对应 UI: Unité
                a.setConforme(rs.getBoolean("conforme"));   // 对应 UI: Conformité (红/绿灯)
                
                analyses.add(a);
            }
        }
        return analyses;
    }

}
