package com.qualieau.service.test;

import static org.junit.jupiter.api.Assertions.*;
import com.qualieau.dao.AnalyseDAO;
import com.qualieau.model.Analyse;
import com.qualieau.util.DBConnection;
import com.qualieau.service.QualiteEauService;
import java.sql.Connection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class QualiteEauServiceTest {
	
	private QualiteEauService waterService;
    private Connection connection;

	@BeforeAll
	void setUp() throws Exception {
        // 获取真实数据库连接，验证实际数据环境
        this.connection = DBConnection.getConnection();
        AnalyseDAO dao = new AnalyseDAO(this.connection);
        this.waterService = new QualiteEauService(dao);
    }

	@Test
    void testCalculerTauxConformite() {
        assertDoesNotThrow(() -> {
            // 使用之前导入成功的城市代码进行测试 (例如 AMBRONAY: 01007)
            String codeInsee = "01007";
            double taux = waterService.calculerTauxConformite(codeInsee);
            
            System.out.println("Taux de conformité : " + taux + "%");
            
            // 验证业务逻辑：合规率不能超过 100% 或低于 0%
            assertTrue(taux >= 0 && taux <= 100, "Le taux doit être une valeur réaliste.");
        });
    }
	
	@Test
    void testRecupererAlertes() {
        assertDoesNotThrow(() -> {
            String codeInsee = "01007";
            List<Analyse> alertes = waterService.recupererAlertes(codeInsee);
            
            // 验证 DSL 要求：只显示不合规项
            for (Analyse alerte : alertes) {
                assertFalse(alerte.isConforme(), "La liste d'alertes ne doit contenir que des analyses non-conformes.");
            }
        });
    }

}
