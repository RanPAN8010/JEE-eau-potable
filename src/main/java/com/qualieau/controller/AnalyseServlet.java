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
            }
            json.append("]");
            
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            response.setStatus(500);
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
