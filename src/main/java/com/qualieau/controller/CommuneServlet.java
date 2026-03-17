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
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String query = request.getParameter("query"); 
        try (Connection conn = DBConnection.getConnection()) {
            CommuneDAO dao = new CommuneDAO(conn);            // 调用 DAO 进行搜索
            List<Commune> resultats = dao.searchCommune(query);
         // 手动构建符合 DSL 要求的 JSON 格式
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < resultats.size(); i++) {
                Commune c = resultats.get(i);
                json.append("{")
                    .append("\"nom\":\"").append(c.getNom()).append("\",")
                    .append("\"codePostal\":\"").append(c.getCodePostal()).append("\",")
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
