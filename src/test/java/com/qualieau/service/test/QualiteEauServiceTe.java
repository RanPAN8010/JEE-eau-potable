package com.qualieau.service.test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.qualieau.dao.AnalyseDAO;
import com.qualieau.model.Analyse;
import com.qualieau.service.QualiteEauService;
import com.qualieau.util.DBConnection;

class QualiteEauServiceTe {
	private static QualiteEauService waterService;
    private static Connection connection;

    @BeforeAll
    public static void setUp() throws Exception {
        // 1. 获取真实数据库连接 [cite: 125-128]
        connection = DBConnection.getConnection();
        // 2. 组装 DAO 和 Service 链条 [cite: 146-154]
        AnalyseDAO dao = new AnalyseDAO(connection);
        waterService = new QualiteEauService(dao);
    }
    
    /**
     * 测试合规率计算逻辑。
     */
    @Test
    void testCalculerTauxConformite() {
        assertDoesNotThrow(() -> {
            // 使用存在数据的城市代码 (例如 AMBRONAY: 01007)
            String codeInsee = "01007";
            double taux = waterService.calculerTauxConformite(codeInsee);           
            System.out.println("DEBUG - Taux de conformité pour " + codeInsee + " : " + taux + "%");        
            // 验证业务逻辑：合规率必须在 [0, 100] 范围内
            assertTrue(taux >= 0 && taux <= 100, "Le taux doit être compris entre 0 et 100%.");
        });
    }
    
    /**
     * 测试告警提取功能。
     * 验证是否仅过滤出状态为 'N' (Non-conforme) 的记录。
     */
    @Test
    void testRecupererAlertes() {
        assertDoesNotThrow(() -> {
            String codeInsee = "01007";
            List<Analyse> alertes = waterService.recupererAlertes(codeInsee);            
            // 验证 DSL 要求：只显示不合规项
            for (Analyse alerte : alertes) {
                assertEquals("N", alerte.getConforme(), 
                    "La liste d'alertes ne doit contenir que des analyses non-conformes ('N').");
            }           
            System.out.println("DEBUG - Nombre d'alertes trouvées : " + alertes.size());
        });
    }
    
    /**
     * 验证空数据的边缘情况。
     * 如果城市不存在，根据 Service 定义应返回 100% 或默认值。
     */
    @Test
    void testCalculerTauxPourVilleInexistante() {
        assertDoesNotThrow(() -> {
            String codeInseeInexistant = "99999";
            double taux = waterService.calculerTauxConformite(codeInseeInexistant);
            
            // 默认无数据时视为 100% 合规
            assertEquals(100.0, taux, "Une ville sans données doit retourner 100% par défaut.");
        });
    }
}
