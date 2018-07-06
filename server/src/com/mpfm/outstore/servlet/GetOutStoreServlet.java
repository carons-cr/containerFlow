package com.mpfm.outstore.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mpfm.outstore.dao.GetOutStoreDaoFactory;
import com.mpfm.outstore.tools.DataUtil;

/**
 * Servlet implementation class login data 格式
 * 
 * { ckxz: '02', tokcdid: 'BJZXKCENTR', outstoreb:
 * '[{"add_sign":2,"ckxz":"02","id":7,"rqinit":0,"rqxh":"949494","rqys":12,"wpid":""}]',
 * kcdid: 'DJGMFHCK', khid: 'ZL0002', cph: '46494', jbr: 7 }
 */
@WebServlet("/getoutstore")
public class GetOutStoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DataUtil dataUtil = new DataUtil();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=UTF-8");
		String data = dataUtil.getBodyData(request);
		String message = GetOutStoreDaoFactory.INSTANCE.getOutStoreDao(data);
		response.getWriter().println(message);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
