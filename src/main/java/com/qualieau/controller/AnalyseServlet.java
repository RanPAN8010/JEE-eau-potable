package com.qualieau.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.qualieau.dao.AnalyseDAO;
import com.qualieau.util.DBConnection;
import com.qualieau.model.Analyse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.time.LocalDate;

@WebServlet("/api/analyses")
public class AnalyseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
        
    public AnalyseServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String type = request.getParameter("type");
        String valeur = request.getParameter("valeur");

        if ("list_villes".equals(type)) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT DISTINCT code_insee FROM analyse ORDER BY code_insee ASC";
                try (PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    
                    StringBuilder json = new StringBuilder("[");
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) json.append(",");
                        json.append("\"").append(rs.getString("code_insee")).append("\"");
                        first = false;
                    }
                    json.append("]");
                    response.getWriter().write(json.toString());
                    return;
                }
            } catch (Exception e) {
                response.setStatus(500);
                response.getWriter().write("{\"error\": \"Impossible de lister les villes\"}");
                return;
            }
        }

        if (valeur == null || valeur.trim().isEmpty()) {
            valeur = request.getParameter("codeInsee");
        }

        if (valeur == null || valeur.trim().isEmpty()) {
            response.setStatus(400);
            response.getWriter().write("{\"error\": \"Paramètre 'valeur' manquant\"}");
            return;
        }

        if (type == null) type = "insee"; 

        try (Connection conn = DBConnection.getConnection()) {
            AnalyseDAO dao = new AnalyseDAO(conn);
            List<Analyse> analyses = dao.searchAnalyses(type, valeur);

            String moisStr = request.getParameter("mois");
            int moisFiltre = 0;
            if (moisStr != null && !moisStr.isEmpty()) {
                try {
                    moisFiltre = Integer.parseInt(moisStr);
                } catch (NumberFormatException e) {
                    moisFiltre = 0; 
                }
            }
            LocalDate limitDate = LocalDate.now().minusMonths(moisFiltre);

            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            
            for (Analyse a : analyses) {
                if (moisFiltre > 0 && a.getDatePrelevement() != null && a.getDatePrelevement().isBefore(limitDate)) {
                    continue; 
                }

                // 🪄 CORRECTION : On applique le décodeur magique ici !
                String paramClean = a.getParametre() != null ? fixEncoding(a.getParametre()).trim() : "";
                String uniteClean = a.getUnite() != null ? fixEncoding(a.getUnite()).trim() : "";
                
                if (!paramClean.isEmpty() && !uniteClean.isEmpty()) {
                    if (paramClean.endsWith(uniteClean)) {
                        paramClean = paramClean.substring(0, paramClean.length() - uniteClean.length()).trim();
                    }
                }

                if (!first) json.append(",");
                json.append("{")
                    .append("\"date\":\"").append(a.getDatePrelevement()).append("\",")
                    .append("\"parametre\":\"").append(escapeJson(paramClean)).append("\",")
                    .append("\"valeur\":").append(a.getValeur()).append(",")
                    .append("\"unite\":\"").append(escapeJson(uniteClean)).append("\",")
                    .append("\"conforme\":").append(a.isConforme())
                    .append("}");
                first = false;
            }
            json.append("]");
            
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\": \"Erreur technique lors de la recherche\"}");
        }
    }
    
    // Le fameux décodeur pour sauver les accents
    private String fixEncoding(String input) {
        if (input == null) return "";
        byte[] bytes = input.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}