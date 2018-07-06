package com.mpfm.outstore.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mpfm.outstore.dao.UpdateFAMDaoFactory;

/*
发送数据到客户端
更新客户端的基本信息
*/
@WebServlet("/UpdateFAM")
public class UpdateFAMServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=UTF-8");
		String RQ = UpdateFAMDaoFactory.INSTANCE.getFhkhAKcdid();
		response.getWriter().println(RQ);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
