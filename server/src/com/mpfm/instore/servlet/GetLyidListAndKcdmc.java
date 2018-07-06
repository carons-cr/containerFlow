package com.mpfm.instore.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mpfm.instore.dao.GetLyidListAndKcdmcDaoFactory;
import com.mpfm.instore.util.DataUtil;



@WebServlet("/GetLyidListAndKcdmc")
public class GetLyidListAndKcdmc extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	DataUtil dataUtil = new DataUtil();

	/**输入data,数据格式{ \"kcdid\": \"MAF\"}
	 * 输出lyidListAndKcdmc,数据格式{\"kcdmc\":\"捷富凯中心库\",\"lyidList\":[{\"ckid\":\"1703140001W35A1A\"}
	,{\"ckid\":\"1703100001W35A1A\"}]}
	 * */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=UTF-8");
		String data = dataUtil.getBodyData(request);
		String lyidListAndKcdmc = GetLyidListAndKcdmcDaoFactory.INSTANCE.getLyidListAndKcdmc(data);
		//System.out.println(data);
		//System.out.println(1);
		response.getWriter().println(lyidListAndKcdmc);
		//System.out.println(lyidListAndKcdmc);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	//	System.out.println(2);
		doGet(request, response);
	}
}
