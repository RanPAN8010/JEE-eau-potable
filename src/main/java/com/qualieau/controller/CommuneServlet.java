package com.qualieau.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.qualieau.dao.CommuneDAO;
import com.qualieau.util.DBConnection;
import com.qualieau.model.Commune;
import java.sql.Connection;
import java.util.List;

/**
 * Servlet pour la gestion des requêtes liées aux communes.
 * Cette classe permet de rechercher des communes via une API REST.
 * @author Ran
 * @version 1.0
 */
@WebServlet("/api/communes")
public class CommuneServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * Constructeur par défaut de la servlet.
     */
    public CommuneServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * Gère les requêtes HTTP GET pour la recherche de communes.
	 * Cette méthode récupère le paramètre "query", interroge la base de données
	 * et retourne les résultats sous forme de tableau JSON.
	 * @param request  L'objet {@link HttpServletRequest} contenant la requête du client.
     * @param response L'objet {@link HttpServletResponse} pour envoyer la réponse au client.
     * @throws ServletException Si une erreur spécifique à la servlet se produit.
     * @throws IOException      Si une erreur d'entrée/sortie est détectée.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 设置返回格式为 JSON
		// Définir le format de réponse en tant que JSON.
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String query = request.getParameter("query");
        String dept = request.getParameter("departement");
        String reg = request.getParameter("region");
        
        try (Connection conn = DBConnection.getConnection()) {
            CommuneDAO dao = new CommuneDAO(conn);// 调用 DAO 进行搜索
            List<Commune> resultats = dao.searchCommune(query.trim());
            
            // 逻辑判断：是主搜索还是区域筛选
            if (query != null && !query.trim().isEmpty()) {
                resultats = dao.searchCommune(query.trim());
            } else if ((dept != null && !dept.isEmpty()) || (reg != null && !reg.isEmpty())) {
                resultats = dao.findByZone(dept, reg);
            } else {
                response.getWriter().write("[]");
                return;
            }
            // 手动构建符合 DSL 要求的 JSON 格式
            //Construire manuellement le format JSON conforme.
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < resultats.size(); i++) {
                Commune c = resultats.get(i);
                json.append("{")
	                .append("\"nom\":\"").append(escapeJson(c.getNom())).append("\",")
	                .append("\"codeInsee\":\"").append(c.getCodeInsee()).append("\",")
	                .append("\"codePostal\":\"").append(c.getCodePostal()).append("\",")
	                .append("\"departement\":\"").append(escapeJson(c.getDepartement())).append("\",")
	                .append("\"region\":\"").append(escapeJson(c.getRegion())).append("\",")
	                .append("\"latitude\":").append(c.getLatitude()).append(",")
	                .append("\"longitude\":").append(c.getLongitude())
	                .append("}");
                if (i < resultats.size() - 1) json.append(",");
            }
            json.append("]");
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            // PVL 案例 12：错误处理
        	// Gestion des erreurs comme PVL.
            response.setStatus(500);
            response.getWriter().write("{\"error\": \"Service indisponible\"}");
        }
	}
	/**
	 * 基础转义逻辑：防止城市名中的双引号或反斜杠破坏手动拼接的 JSON 结构。
	 * Logique d'échappement de base pour sécuriser les chaînes JSON.
	 * Empêche les guillemets ou les barres obliques de corrompre la structure JSON manuelle.
	 * @param input La chaîne de caractères à échapper.
	 * @return La chaîne échappée sécurisée pour le JSON.
	 */
	private String escapeJson(String input) {
	    if (input == null) {
	        return "";
	    }
	    // 处理反斜杠和双引号，这是 JSON 字符串中最容易导致格式错误的字符
	    // Gérer les barres obliques inverses et les guillemets, 
	    // caractères les plus critiques du JSON.
	    return input.replace("\\", "\\\\")
	                .replace("\"", "\\\"");
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
