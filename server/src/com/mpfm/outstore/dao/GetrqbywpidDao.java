package com.mpfm.outstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpfm.outstore.tools.DBFactory;


public class GetrqbywpidDao {
	Connection conn = null;
	PreparedStatement ps;

	public String getRQbywpid(String data) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = "{\"state\": 0}";
		try {
			Map map = mapper.readValue(data, Map.class);
			String wpid = (String) map.get("wpid");
			String kcdid = (String) map.get("kcdid");
			String rqxh = getRqxhByWpid(wpid);
			if (rqxh == null) {
				return jsonStr;
			}
	String maxrkid = getCkidByKcdidAndRqxh(kcdid, rqxh);
				if (maxrkid == null) {
				return "{\"state\": 0}";
			}
			String rkid = maxrkid + kcdid;
	/*		if(!rqxh.equals(checkWpid(rkid, wpid))) {
				return "{\"state\": 2}";
			}*/
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select rqkyl from instored where rkid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, rkid);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Map<String, Object> toMap = new HashMap<String, Object>();
				toMap.put("rqxh", rqxh);
				toMap.put("rqys", rs.getInt("rqkyl"));
				toMap.put("state", 1);
				jsonStr = mapper.writeValueAsString(toMap);
			}

		} catch (Exception e) {
			System.out.println("系统错误" + e);
			jsonStr = "{\"state\": 0}";
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return jsonStr;
	}

	public String checkWpid(String rkid, String wpid) {
		String rqxh = null;
		try {
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select rqxh from instored where rkid=? AND wpid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, rkid);
			ps.setString(2, wpid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rqxh = rs.getString(1);
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			System.out.println("系统错误5" + e);
			return null;
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return rqxh;
	}
	public String getCkidByKcdidAndRqxh(String kcdid, String rqxh) {
		String rkid = null;
		try {
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select MAX(LEFT(rkid,10)) from instored where kcdid=? AND rqxh=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, kcdid);
			ps.setString(2, rqxh);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rkid = rs.getString(1);
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			System.out.println("系统错误3" + e);
			return null;
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return rkid;
	}

	// 根据零件号查询容器型号
	public String getRqxhByWpid(String wpid) {
		String rqxh = null;
		try {
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select rqxh from palletpart where wpid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, wpid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rqxh = rs.getString("rqxh");
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			System.out.println("系统错误" + e);
			return null;
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return rqxh;
	}
}
