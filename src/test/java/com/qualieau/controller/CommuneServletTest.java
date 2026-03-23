package com.qualieau.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.qualieau.dao.CommuneDAO;
import com.qualieau.util.DBConnection;
import com.qualieau.model.Commune;
import java.sql.Connection;
import java.util.List;

class CommuneServletTest {

    @Test
    void testSearchLogicForApi() {
        String testQuery = "Rouen";
        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                CommuneDAO dao = new CommuneDAO(conn);
                List<Commune> results = dao.searchCommune(testQuery);
                
                assertNotNull(results);
                assertFalse(results.isEmpty(), "La recherche de 'Rouen' devrait retourner des résultats.");
                
                Commune first = results.get(0);
                assertTrue(first.getNom().toLowerCase().contains("rouen"));
            }
        });
    }

    @Test
    void testEmptyQueryHandling() {
        String emptyQuery = "";
        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                CommuneDAO dao = new CommuneDAO(conn);
                List<Commune> results = dao.searchCommune(emptyQuery);
                assertNotNull(results);
            }
        });
    }
}