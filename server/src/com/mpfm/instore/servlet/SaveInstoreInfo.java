package com.mpfm.instore.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mpfm.instore.dao.SaveInstoreInfoDaoFactory;
import com.mpfm.instore.util.DataUtil;


/**
 * @author caron
 * 根据来源单号和用户id保存入库信息
 */
@WebServlet("/SaveInstoreInfo")
public class SaveInstoreInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	DataUtil dataUtil = new DataUtil();
       
	/**输入data,数据格式{ \"lyid\": \"1703140001W35A1A\"}
	 * 输出{"\rqInfoList\":[{\"wpid\":\"无\",\"rqxh\":\"25185\",\"rqys\":\"12\",\"rqss\":\"12\"}]
	 * ,\"rkly\":\"二厂总装\"}
	 * */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=UTF-8");
		String data = dataUtil.getBodyData(request);
		//System.out.println(data);
		String saveTab = SaveInstoreInfoDaoFactory.INSTANCE.saveInstoreInfo(data);
		//System.out.println(saveTab);
		response.getWriter().println(saveTab);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
