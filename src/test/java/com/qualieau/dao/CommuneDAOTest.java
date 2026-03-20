package com.qualieau.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.qualieau.model.Commune;
import com.qualieau.util.DBConnection;

class CommuneDAOTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
     * Test de la recherche principale par nom, CP ou INSEE.
     */
    @Test
    void testSearchCommune() {
        // 测试输入值：鲁昂 (PVL Case 1) 
        String query = "Rouen";

        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                CommuneDAO dao = new CommuneDAO(conn);

                // 记录开始时间，验证搜索性能 (PVL 11) [cite: 95-96]
                long startTime = System.currentTimeMillis();
                List<Commune> results = dao.searchCommune(query);
                long duration = System.currentTimeMillis() - startTime;

                // 1. 验证结果不为空 
                assertNotNull(results, "La liste de recherche ne doit pas être null");
                assertFalse(results.isEmpty(), "La recherche pour 'Rouen' devrait retourner au moins un résultat");

                // 2. 验证性能：搜索响应必须 ≤ 2 秒 
                assertTrue(duration < 2000, "La recherche est trop lente : " + duration + "ms");

                // 3. 验证字段完整性：名称、邮编、省份、大区、经纬度
                Commune first = results.get(0);
                assertNotNull(first.getNom(), "Le nom de la commune est manquant");
                assertNotNull(first.getCodeInsee(), "Le code INSEE est manquant");
                assertNotNull(first.getDepartement(), "Le département est manquant");
                // 验证之前导入的经纬度数据是否存在 
                assertNotNull(first.getLatitude(), "La latitude est manquante");
                assertNotEquals(0.0, first.getLatitude(), "La latitude ne doit pas être 0.0");

                System.out.println("SUCCESS: Recherche '" + query + "' trouvée en " + duration + "ms");
            }
        });
    }
    
    /**
     * Test de la recherche par Code Postal.
     */
    @Test
    void testSearchByCodePostal() {
        String cp = "76120"; // Le Grand-Quevilly 
        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                CommuneDAO dao = new CommuneDAO(conn);
                List<Commune> results = dao.searchCommune(cp);
                
                assertFalse(results.isEmpty(), "La recherche par code postal '76120' a échoué");
                assertTrue(results.stream().anyMatch(c -> c.getCodePostal().equals(cp)), 
                    "Le code postal dans les résultats ne correspond pas à la saisie");
            }
        });
    }
    
    /**
     * Test du filtrage par zone (Département / Région).
     */
    @Test
    void testFindByZone() {
        String region = "Normandie";
        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                CommuneDAO dao = new CommuneDAO(conn);
                // 仅按大区过滤
                List<Commune> results = dao.findByZone(null, region);
                
                assertNotNull(results);
                if (!results.isEmpty()) {
                    assertEquals(region, results.get(0).getRegion(), "Le filtrage par région est incorrect");
                }
            }
        });
    }
}
