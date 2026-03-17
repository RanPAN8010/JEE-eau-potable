package com.qualieau.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.qualieau.dao.AnalyseDAO;
import com.qualieau.util.DBConnection;
import com.qualieau.model.Analyse;
import java.sql.Connection;
import java.util.List;

/**
 * Servlet implementation class AnalyseServlet
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
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String codeInsee = request.getParameter("codeInsee");

        if (codeInsee == null || codeInsee.trim().isEmpty()) {
            response.setStatus(400);
            response.getWriter().write("{\"error\": \"Code INSEE manquant\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            AnalyseDAO dao = new AnalyseDAO(conn);
            // 注意：这里调用的是你在 DAO 里定义的方法名 getAnalyseByCommune
            List<Analyse> analyses = dao.getAnalyseByCommune(codeInsee);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < analyses.size(); i++) {
                Analyse a = analyses.get(i);
                json.append("{")
                    .append("\"date\":\"").append(a.getDatePrelevement()).append("\",")
                    .append("\"parametre\":\"").append(escapeJson(a.getParametre())).append("\",")
                    .append("\"valeur\":").append(a.getValeur()).append(",")
                    .append("\"unite\":\"").append(escapeJson(a.getUnite())).append("\",")
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
	 */
	private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
