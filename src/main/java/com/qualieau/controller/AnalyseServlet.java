package com.qualieau.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
<<<<<<< Updated upstream
import java.nio.charset.StandardCharsets;
=======
>>>>>>> Stashed changes
import com.qualieau.dao.AnalyseDAO;
import com.qualieau.util.DBConnection;
import com.qualieau.model.Analyse;
import java.sql.Connection;
<<<<<<< Updated upstream
import java.util.List;

/**
 * Servlet implementation class AnalyseServlet
 * Servlet gérant la récupération des analyses 
 * de qualité d'eau pour une commune spécifique.
 * @author Ran
 * @version 1.1
 */
@WebServlet("/api/analyses")
public class AnalyseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AnalyseServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String codeInsee = request.getParameter("codeInsee");

        if (codeInsee == null || codeInsee.trim().isEmpty()) {
            response.setStatus(400);
            response.getWriter().write("{\"error\": \"Code INSEE manquant\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            AnalyseDAO dao = new AnalyseDAO(conn);
            List<Analyse> analyses = dao.getAnalyseByCommune(codeInsee);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < analyses.size(); i++) {
                Analyse a = analyses.get(i);
                json.append("{")
                    .append("\"date\":\"").append(a.getDatePrelevement()).append("\",")
                    // 修复检测参数名的乱码 Correction de l'encodage pour le nom du paramètre
                    .append("\"parametre\":\"").append(escapeJson(fixEncoding(a.getParametre()))).append("\",")
                    .append("\"valeur\":").append(a.getValeur()).append(",")
                    // 修复单位的乱码 Correction de l'encodage pour l'unité de mesure
                    .append("\"unite\":\"").append(escapeJson(fixEncoding(a.getUnite()))).append("\",")
                    .append("\"conforme\":").append(a.isConforme())
                    .append("}");
                if (i < analyses.size() - 1) json.append(",");
=======
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

            // 📅 CORRECTION : Sécurité anti-crash sur le filtre de date
            String moisStr = request.getParameter("mois");
            int moisFiltre = 0;
            if (moisStr != null && !moisStr.isEmpty()) {
                try {
                    moisFiltre = Integer.parseInt(moisStr);
                } catch (NumberFormatException e) {
                    moisFiltre = 0; // On ignore le filtre si ce n'est pas un nombre valide
                }
            }
            LocalDate limitDate = LocalDate.now().minusMonths(moisFiltre);

            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            
            for (Analyse a : analyses) {
                if (moisFiltre > 0 && a.getDatePrelevement() != null && a.getDatePrelevement().isBefore(limitDate)) {
                    continue; 
                }

                String paramClean = a.getParametre() != null ? a.getParametre().trim() : "";
                String uniteClean = a.getUnite() != null ? a.getUnite().trim() : "";
                
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
>>>>>>> Stashed changes
            }
            json.append("]");
            
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            response.setStatus(500);
<<<<<<< Updated upstream
            response.getWriter().write("{\"error\": \"Erreur technique\"}");
        }
	}
	
	/**
	 * 基础转义逻辑：防止城市名中的双引号或反斜杠破坏手动拼接的 JSON 结构。
	 * Logique d'échappement de base : empêche les guillemets 
	 * ou les barres obliques de corrompre la structure JSON.
	 */
	private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
	
	private String fixEncoding(String input) {
        if (input == null) return "";
        // 将数据库读出的错误编码字节流重新映射回正确的法语字符
        // vRemappage du flux d'octets erroné issu de la base de données vers les caractères français corrects.
        byte[] bytes = input.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
=======
            response.getWriter().write("{\"error\": \"Erreur technique lors de la recherche\"}");
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
>>>>>>> Stashed changes
