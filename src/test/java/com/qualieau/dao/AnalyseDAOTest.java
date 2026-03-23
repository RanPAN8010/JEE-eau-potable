package com.qualieau.dao;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.qualieau.util.DBConnection;
import com.qualieau.model.Analyse;
import java.sql.Connection;
import java.util.List;

class AnalyseDAOTest {

    @Test
    void testGetAnalyseByCommune() {
        String testInsee = "01007"; 

        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                AnalyseDAO dao = new AnalyseDAO(conn);
                
                long startTime = System.currentTimeMillis();
                // Utilisation de notre nouvelle méthode unifiée
                List<Analyse> results = dao.searchAnalyses("insee", testInsee);
                long duration = System.currentTimeMillis() - startTime;

                assertNotNull(results, "La liste ne doit pas être null");
                assertTrue(duration < 2000, "La requête est trop lente : " + duration + "ms");
                
                if (!results.isEmpty()) {
                    Analyse first = results.get(0);
                    assertNotNull(first.getDatePrelevement());
                    assertNotNull(first.getParametre());
                    System.out.println("Test réussi pour " + testInsee + " : " + results.size() + " analyses trouvées en " + duration + "ms");
                }
            }
        });
    }
}