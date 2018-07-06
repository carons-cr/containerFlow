package com.mpfm.query.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mpfm.query.daos.InQueryByKeyWordDaoFactory;
import com.mpfm.query.tools.DataUtil;


/**
 * Servlet implementation class InQueryByly
 */
@WebServlet("/InQueryByKeyWord")
public class InQueryByKeyWord extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	DataUtil dataUtil = new DataUtil();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=UTF-8");
		
		// 获取请求数据
		String data = dataUtil.getBodyData(request);
		System.out.println(data);
		/*data数据类型
		 * { \"keyword\": \"P6422\",\"date\": \"2017-03-01\",\"kcdid\": ""}
		 * 
		 * */
		String RQ = InQueryByKeyWordDaoFactory.INSTANCE.QueryByKeyWord(data);
		response.getWriter().println(RQ);
		
	}

	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
