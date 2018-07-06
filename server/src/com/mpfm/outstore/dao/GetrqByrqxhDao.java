package com.mpfm.outstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpfm.outstore.tools.DBFactory;

public class GetrqByrqxhDao {
	Connection conn = null;
	PreparedStatement ps;

	public String getRQByrqxh(String data) {
		ObjectMapper mapper = new ObjectMapper();
		Connection conn = null;
		PreparedStatement ps;
		String jsonStr = "{\"state\": 0}";
		try {
			Map map = mapper.readValue(data, Map.class);
			String rqxh = (String) map.get("rqxh");
			String kcdid = (String) map.get("kcdid");
			String maxrkid = getCkidByKcdidAndRqxh(kcdid, rqxh);
			if (maxrkid == null) {
				return jsonStr;
			}
			String rkid = maxrkid + kcdid;
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select instored.rqkyl,palletbase.rqmc from instored,palletbase where palletbase.rqxh = ? and instored.rkid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, rqxh);
			ps.setString(2, rkid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Map<String, Object> toMap = new HashMap<String, Object>();
				toMap.put("rqmc", rs.getString("rqmc"));
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

}
