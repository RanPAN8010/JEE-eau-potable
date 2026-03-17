package com.qualieau.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.qualieau.dao.CommuneDAO;
import com.qualieau.util.DBConnection;
import com.qualieau.model.Commune;
import java.sql.Connection;
import java.util.List;

class CommuneServletTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
    void testSearchLogicForApi() {
        // 模拟 API 接收到 "Rouen" 后的数据链路逻辑
        String testQuery = "Rouen";
        
        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                CommuneDAO dao = new CommuneDAO(conn);
                List<Commune> results = dao.searchCommune(testQuery);
                
                // 验证是否能查到结果（Rouen 肯定在数据库里）
                assertNotNull(results);
                assertFalse(results.isEmpty(), "La recherche de 'Rouen' devrait retourner des résultats.");
                
                // 验证第一条数据的合规性
                Commune first = results.get(0);
                assertTrue(first.getNom().toLowerCase().contains("rouen"));
                System.out.println("API Logic Test Success: Found " + results.size() + " communes.");
            }
        });
    }

    @Test
    void testEmptyQueryHandling() {
        // 模拟空搜索请求
        String emptyQuery = "";
        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                CommuneDAO dao = new CommuneDAO(conn);
                List<Commune> results = dao.searchCommune(emptyQuery);
                // 确保空搜索不会导致崩溃，且结果列表符合预期（通常为空或全量）
                assertNotNull(results);
            }
        });
    }

}
