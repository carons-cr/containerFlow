package com.mpfm.outstore.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mpfm.outstore.dao.GetrqByrqxhDaoFactory;
import com.mpfm.outstore.tools.DataUtil;

/**
 * 
 * data数据类型 { \"rqxh\": \"P6422\",\"kcdid\": \"WHZRK\"}
 */
@WebServlet("/Getrqbyrqxh")
public class GetrqByrqxhServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DataUtil dataUtil = new DataUtil();
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=UTF-8");
		String data = dataUtil.getBodyData(request);

		String RQ = GetrqByrqxhDaoFactory.INSTANCE.getRQByrqxh(data);
		response.getWriter().println(RQ);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
