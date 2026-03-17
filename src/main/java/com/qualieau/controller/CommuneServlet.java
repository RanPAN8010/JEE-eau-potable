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
 * Servlet implementation class CommuneServlet
 */
@WebServlet("/api/communes")
public class CommuneServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CommuneServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 设置返回格式为 JSON
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String query = request.getParameter("query");
        if (query == null || query.trim().isEmpty()) {
            response.getWriter().write("[]");
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            CommuneDAO dao = new CommuneDAO(conn);            // 调用 DAO 进行搜索
            List<Commune> resultats = dao.searchCommune(query.trim());
         // 手动构建符合 DSL 要求的 JSON 格式
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < resultats.size(); i++) {
                Commune c = resultats.get(i);
                json.append("{")
                	// 在此处使用 fixEncoding 修复城市名
                    .append("\"nom\":\"").append(escapeJson(fixEncoding(c.getNom()))).append("\",")
                    .append("\"codeInsee\":\"").append(c.getCodeInsee()).append("\"")
                    .append("}");
                if (i < resultats.size() - 1) json.append(",");
            }
            json.append("]");
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            // PVL 案例 12：错误处理
            response.setStatus(500);
            response.getWriter().write("{\"error\": \"Service indisponible\"}");
        }
	}
	/**
	 * 基础转义逻辑：防止城市名中的双引号或反斜杠破坏手动拼接的 JSON 结构。
	 */
	private String escapeJson(String input) {
	    if (input == null) {
	        return "";
	    }
	    // 处理反斜杠和双引号，这是 JSON 字符串中最容易导致格式错误的字符
	    return input.replace("\\", "\\\\")
	                .replace("\"", "\\\"");
	}
	
	private String fixEncoding(String input) {
        if (input == null) return "";
        // 将数据库读出的错误编码字节流重新映射回正确的法语字符
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
