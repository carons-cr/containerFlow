package com.mpfm.outstore.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mpfm.outstore.dao.GetrqbywpidDaoFactory;
import com.mpfm.outstore.tools.DataUtil;

/**
 * Servlet implementation class GetrqbywpidServlet
 */
@WebServlet("/Getrqbywpid")
public class GetrqBywpidServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DataUtil dataUtil = new DataUtil();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=UTF-8");
		String data= dataUtil.getBodyData(request);
		/*data数据类型
		 * { \"wpid\": \"9808835480\",\"kcdid\": \"WHZRK\"}
		 * 
		 * */
		//data="{\"wpid\": \"25185\",\"kcdid\": \"99900W0406B-000\"}";
	//	System.out.println(data);
		String RQ = GetrqbywpidDaoFactory.INSTANCE.getRQbywpid(data);
	
		response.getWriter().println(RQ);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
