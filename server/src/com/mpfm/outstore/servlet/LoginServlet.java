package com.mpfm.outstore.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.mpfm.outstore.dao.LoginDaoFactory;
import com.mpfm.outstore.tools.DataUtil;

/**
 * Servlet implementation class login
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DataUtil dataUtil = new DataUtil();
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=UTF-8");
		String data = dataUtil.getBodyData(request);
		String sendUsers=LoginDaoFactory.INSTANCE.getUsersRole(data);
		response.getWriter().println(sendUsers);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
