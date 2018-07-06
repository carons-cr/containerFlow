package com.mpfm.outstore.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpfm.outstore.tools.DBFactory;
// "{"state":1,'mdd':'[{\"kcdid\":\"99900W0406B-000\",\"kcdmc\":\"外销件\"},{\"kcdid\":\"BJZXKCENTR\",\"kcdmc\":\"备件中心库 Centr\"}]', 'fhkh':'[{\"khid\":\"ZL0001\",\"khmc\":\"武汉中人瑞众\"},{\"khid\":\"ZL0002\",\"khmc\":\"重庆立达奥特\"}]'}";
/*发送的数据格式*/
public class UpdateFAMDao {
	ObjectMapper mapper = new ObjectMapper();
	PreparedStatement ps;
	//发送数据
	public String getFhkhAKcdid() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = "{\"state\": 0}";
		String kcd = getKcd();
		String fhkh = getFhkh();
		try {
			if (kcd != null || fhkh != null) {
				Map<String, Object> toMap = new HashMap<String, Object>();
				toMap.put("mdd", kcd);
				toMap.put("fhkh",fhkh);
				toMap.put("state", 1);
				jsonStr = mapper.writeValueAsString(toMap);
			}
		} catch (JsonProcessingException e) {
			return "{\"state\": -}";
		}
		return jsonStr;
	}

	// 获取所有库存点编号
	public String getKcd() {
		Connection conn = null;
		String kcd = null;
		try {
			List<Map<String, Object>> list = new ArrayList<>();
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select * from storeplace";
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Map<String, Object> toMap = new HashMap<String, Object>();
				toMap.put("kcdid", rs.getString("kcdid"));
				toMap.put("kcdmc", rs.getString("kcdmc"));
				list.add(toMap);
			}
			kcd = mapper.writeValueAsString(list);
		} catch (Exception e) {
			System.out.println("系统错误" + e);
			return null;
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return kcd;
	}

	// 获取所有库存点编号
	public String getFhkh() {
		Connection conn = null;
		String fhkh = null;
		try {
			List<Map<String, Object>> list = new ArrayList<>();
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select * from customer";
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Map<String, Object> toMap = new HashMap<String, Object>();
				toMap.put("khid", rs.getString("khid"));
				toMap.put("khmc", rs.getString("khmc"));
				list.add(toMap);
			}
			fhkh = mapper.writeValueAsString(list);
		} catch (Exception e) {
			System.out.println("系统错误" + e);
			return null;
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return fhkh;
	}
}