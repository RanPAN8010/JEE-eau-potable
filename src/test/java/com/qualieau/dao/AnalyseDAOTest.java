package com.qualieau.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.qualieau.model.Analyse;
import com.qualieau.util.DBConnection;

class AnalyseDAOTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
     * 测试用例：验证根据 INSEE 码获取分析列表。
     * 对应 PVL 案例 7 。
     */
    @Test
    public void testGetAnalyseByCommune() {

    	String testInsee = "01007"; 
        
        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                AnalyseDAO dao = new AnalyseDAO(conn);
                
                // 记录开始时间，验证性能
                long startTime = System.currentTimeMillis();
                List<Analyse> results = dao.getAnalyseByCommune(testInsee);
                long duration = System.currentTimeMillis() - startTime;

                // 1. 验证结果不为空
                assertNotNull(results, "La liste ne doit pas être null");
                
                // 2. 验证性能：必须小于 2000ms
                assertTrue(duration < 2000, "La requête est trop lente : " + duration + "ms");
                
                if (!results.isEmpty()) {
                    Analyse first = results.get(0);
                    // 3. 验证关键字段映射 (PVL 7: date, paramètre, valeur, unité, conformité) 
                    assertNotNull(first.getDatePrelevement(), "Date de prélèvement manquante");
                    assertNotNull(first.getParametre(), "Libellé du paramètre manquant");
                    assertNotNull(first.getValeur(), "Valeur de mesure manquante");
                    assertNotNull(first.getConforme(), "Indicateur de conformité manquant");
                    // 4. 验证排序逻辑：最新日期在前
                    if (results.size() > 1) {
                        Analyse second = results.get(1);
                        assertFalse(first.getDatePrelevement().isBefore(second.getDatePrelevement()), 
                            "Le tri par date (récent -> ancien) est incorrect");
                    }
                    System.out.println("Test réussi pour " + testInsee + " : " + results.size() + " analyses trouvées en " + duration + "ms");
                } else {
                    System.out.println("Attention : Aucune donnée trouvée pour " + testInsee + " mais la requête a fonctionné.");
                }
            }
        });
    }
    
    /**
     * 测试：确保测量值包含特殊字符时不崩溃
     */
    @Test
    void testSpecialCharacterHandling() {
        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                AnalyseDAO dao = new AnalyseDAO(conn);
                // 查找所有记录，检查是否存在带 < 或 > 的测量值
                List<Analyse> results = dao.getAnalyseByCommune("76540");
                boolean foundSpecial = results.stream()
                    .anyMatch(a -> a.getValeur().contains("<") || a.getValeur().contains(">"));
                
                if(foundSpecial) {
                    System.out.println("INFO: Valeurs spéciales (<, >) correctement gérées");
                }
            }
        });
    }

}
