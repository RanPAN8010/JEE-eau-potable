package com.qualieau.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.qualieau.util.DBConnection;
import com.qualieau.model.Analyse;
import java.sql.Connection;
import java.util.List;

class AnalyseDAOTest {

<<<<<<< Updated upstream
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}
	@Test
=======
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }
    
    @Test
>>>>>>> Stashed changes
    void testGetAnalyseByCommune() {
        // 使用一个确定存在数据的 INSEE 代码
        String testInsee = "01007"; 

        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                AnalyseDAO dao = new AnalyseDAO(conn);
                
                // 记录开始时间，验证性能
                long startTime = System.currentTimeMillis();
<<<<<<< Updated upstream
                List<Analyse> results = dao.getAnalyseByCommune(testInsee);
=======
                
                // --- 🟢 MODIFICATION ICI : Appel de la nouvelle méthode ---
                List<Analyse> results = dao.searchAnalyses("insee", testInsee);
                // ---------------------------------------------------------
                
>>>>>>> Stashed changes
                long duration = System.currentTimeMillis() - startTime;

                // 1. 验证结果不为空
                assertNotNull(results, "La liste ne doit pas être null");
                
                // 2. 验证性能：必须小于 2000ms
                assertTrue(duration < 2000, "La requête est trop lente : " + duration + "ms");
                
                if (!results.isEmpty()) {
                    Analyse first = results.get(0);
                    // 3. 验证字段映射
                    assertNotNull(first.getDatePrelevement());
                    assertNotNull(first.getParametre());
                    
                    System.out.println("Test réussi pour " + testInsee + " : " + results.size() + " analyses trouvées en " + duration + "ms");
                } else {
                    System.out.println("Attention : Aucune donnée trouvée pour " + testInsee + " mais la requête a fonctionné.");
                }
            }
        });
    }
<<<<<<< Updated upstream
}
=======
}
>>>>>>> Stashed changes
