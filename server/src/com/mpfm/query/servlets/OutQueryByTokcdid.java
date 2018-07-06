package com.mpfm.query.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mpfm.query.daos.OutQueryByKeyWordDaoFactory;
import com.mpfm.query.tools.DataUtil;


/**
 * Servlet implementation class OutQueryByTokcdid1
 */
@WebServlet("/OutQueryByTokcdid")
public class OutQueryByTokcdid extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			DataUtil dataUtil = new DataUtil();
	    
		
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/json; charset=UTF-8");
			
			// {'keyword':'','date':'2017-08-01','kcdid':'MAF'}
			String data = dataUtil.getBodyData(request);
			System.out.println(data);
			
			String getOutQueryByRkly = OutQueryByKeyWordDaoFactory.INSTANCE.QueryByKeyWord(data);
			
			response.getWriter().println(getOutQueryByRkly);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
