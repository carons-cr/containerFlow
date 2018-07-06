package com.mpfm.outstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpfm.outstore.tools.DBFactory;
import com.mpfm.outstore.tools.DataUtil;

public class LoginDao {
	/**
	 * 登录处理，登录成功state=1，其他state=0
	 */
	public String getUsersRole(String data) {
		ObjectMapper mapper = new ObjectMapper();
		Connection conn = null;
		PreparedStatement ps;
		String jsonStr = "{\"state\": 0}";
		try {
			Map map = mapper.readValue(data, Map.class);
			String userid = (String) map.get("userid");
			String password = (String) map.get("password");
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select * from users where userid = ? and  password = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userid);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Map<String, Object> userToMap = new HashMap<String, Object>();
				userToMap.put("id", rs.getInt("id"));
				userToMap.put("username", rs.getString("username"));
				userToMap.put("kcdid", rs.getString("kcdid"));
				userToMap.put("userid", userid);
				String json = mapper.writeValueAsString(userToMap);
				jsonStr = new DataUtil().encapsulation(json);
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			System.out.println("系统错误" + e);
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return jsonStr;
	}

}