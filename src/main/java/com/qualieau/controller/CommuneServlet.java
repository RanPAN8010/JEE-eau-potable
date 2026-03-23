package com.qualieau.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.qualieau.dao.CommuneDAO;
import com.qualieau.util.DBConnection;
import com.qualieau.model.Commune;
import java.sql.Connection;
import java.util.List;

@WebServlet("/api/communes")
public class CommuneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public CommuneServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 🚨 CORRECTION : Les autorisations CORS pour React !
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String query = request.getParameter("query");
        if (query == null || query.trim().isEmpty()) {
            response.getWriter().write("[]");
            return;
        }

        // 🛡️ CORRECTION : On autorise l'apostrophe (ex: L'Aigle) mais on bloque le reste
        if (query.matches(".*[^a-zA-ZÀ-ÿ0-9\\s\\-'].*")) {
            response.setStatus(404);
            response.getWriter().write("{\"error\": \"Ville non trouvée\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            CommuneDAO dao = new CommuneDAO(conn);
            List<Commune> resultats = dao.searchCommune(query.trim());
            
            if (resultats.isEmpty()) {
                response.setStatus(404);
                response.getWriter().write("{\"error\": \"Ville non trouvée\"}");
                return;
            }

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < resultats.size(); i++) {
                Commune c = resultats.get(i);
                json.append("{")
                    .append("\"nom\":\"").append(escapeJson(c.getNom())).append("\",")
                    .append("\"codeInsee\":\"").append(c.getCodeInsee()).append("\"")
                    .append("}");
                if (i < resultats.size() - 1) json.append(",");
            }
            json.append("]");
            
            response.getWriter().write(json.toString());
            
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\": \"Service indisponible\"}");
        }
    }
    
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}