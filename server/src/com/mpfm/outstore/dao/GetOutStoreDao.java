package com.mpfm.outstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpfm.outstore.tools.DBFactory;

public class GetOutStoreDao {
	ObjectMapper mapper = new ObjectMapper();
	Connection conn = null;
	PreparedStatement ps;

	/**
	 * 出库处理，成功state=1，其他state=0
	 */
	public String getOutStoreDao(String data) {
		String jsonStr = "{\"state\": 0}";
		try {
			Map map = mapper.readValue(data, Map.class);
			String ckxz = (String) map.get("ckxz");
			String tokcdid = (String) map.get("tokcdid");
			String outstored = (String) map.get("outstored");
			String kcdid = (String) map.get("kcdid");
			String khid = (String) map.get("khid");
			String cph = (String) map.get("cph");
			String jbr = (String) map.get("jbr");
			String ckid = createCkid(kcdid);
			List<Map<String, Object>> listOutstored = outSdToMap(outstored, ckid, kcdid, ckxz);
			Map<String, Object> mapOutstoreb = outSbToMap(ckid, kcdid, tokcdid, ckxz, cph, khid, jbr);
			int result = installData(listOutstored, mapOutstoreb);
			if (result == 1) {
				jsonStr = "{\"state\": 1}";
			} else if (result == -1) {
				jsonStr = "{\"state\": -1}";
			} else {
				jsonStr = "{\"state\": 0}";
			}
		} catch (Exception e) {
			System.out.println("系统错误1" + e);
			return "{\"state\": -1}";
		}
		return jsonStr;
	}

	// 生成outstoreb 添加情况
	public Map<String, Object> outSbToMap(String ckid, String kcdid, String tokcdid, String ckxz, String cph,
			String khid, String jbr) {
		Map<String, Object> map = new HashMap<>();
		map.put("ckid", ckid);
		map.put("kcdid", kcdid);
		map.put("tokcdid", tokcdid);
		map.put("ckxz", ckxz);
		map.put("cph", cph);
		map.put("khid", khid);
		map.put("jbr", jbr);
		return map;
	}

	// 生成ckid出库id
	public String createCkid(String kcdid) {
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		String date = df.format(new Date());
		int count = 0;
		String ckid = getCkid(kcdid);

		if (ckid != null) {
			if (date.equals(ckid.substring(0, 6))) {
				count = 1 + Integer.parseInt(ckid.substring(6, 10));
			} else {
				count = 1;
			}
		} else {
			count = 1;
		}

		ckid = date + String.format("%04d", count) + kcdid;
		return ckid;
	}

	// 取出该库存点最新的出库单号
	public String getCkid(String kcdid) {
		String ckid = null;
		try {
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select ckid from outstoreb where kcdid= ? AND cksj =(SELECT MAX(cksj) FROM outstoreb where kcdid= ? )";
			ps = conn.prepareStatement(sql);
			ps.setString(1, kcdid);
			ps.setString(2, kcdid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ckid = rs.getString("ckid");
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			System.out.println("系统错误2" + e);
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return ckid;
	}

	// outstored json数据处理
	public List<Map<String, Object>> outSdToMap(String outstored, String ckid, String kcdid, String ckxz) {
		String string = new String();
		try {
			JavaType javaType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Map.class);
			List<Map<String, Object>> list = (List<Map<String, Object>>) mapper.readValue(outstored, javaType);
			List<Map<String, Object>> outStored = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> map : list) {
				String wpid = (String) map.get("wpid");
				String rqxh = (String) map.get("rqxh");
				// String ckxz = (String) map.get("ckxz");
				int rqys = Integer.parseInt((String) map.get("rqys"));
				Map<String, Object> outDMap = new HashMap<String, Object>();
				outDMap.put("kcdid", kcdid);
				outDMap.put("ckid", ckid);
				outDMap.put("ckxz", ckxz);
				outDMap.put("wpid", wpid);
				outDMap.put("wpys", getRqrlByWpid(wpid));
				outDMap.put("rqxh", rqxh);
				outDMap.put("rqinit", rqys);
				outDMap.put("rqys", rqys);
				outDMap.put("rqzl", getRqzl(kcdid, ckxz, rqxh) + rqys);
				outDMap.put("rqckl", getRqckl(kcdid, rqxh) + rqys);
				outDMap.put("rqkyl", getRqkyl(kcdid, rqxh) - rqys);
				outDMap.put("rkid", getRkidByKcdidAndRqxh(kcdid, rqxh) + kcdid);
				outStored.add(outDMap);
			}
			return outStored;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 获取最新库存点rqkyl
	public int getRqkyl(String kcdid, String rqxh) {
		String rkid = getRkidByKcdidAndRqxh(kcdid, rqxh) + kcdid;
		int rqkyl = 0;
		try {
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select rqkyl from instored where rkid= ? and rqxh = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, rkid);
			ps.setString(2, rqxh);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rqkyl = rs.getInt("rqkyl");
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			System.out.println("系统错误4" + e);
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return rqkyl;
	}

	// 获取最新的库存点某种容器出库编号,根据库存点和容器型号
	public String getCkidByKcdidAndRqxh(String kcdid, String rqxh) {
		String ckid = null;
		try {
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select MAX(LEFT(ckid,10)) from outstored where kcdid = ? AND rqxh = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, kcdid);
			ps.setString(2, rqxh);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ckid = rs.getString(1);
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			System.out.println("系统错误3" + e);
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return ckid;
	}

	// 获取最新的库存点某种容器入库编号,根据库存点和容器型号
	public String getRkidByKcdidAndRqxh(String kcdid, String rqxh) {
		String rkid = null;
		try {
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select MAX(LEFT(rkid,10)) from instored where kcdid= ? AND rqxh= ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, kcdid);
			ps.setString(2, rqxh);
			// ps.setString(3,wpid);
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

	// 获取最新的rqckl
	public int getRqckl(String kcdid, String rqxh) {
		String maxCkid = getCkidByKcdidAndRqxh(kcdid, rqxh);
		int rqckl = 0;
		String ckid = maxCkid + kcdid;
		// System.out.println(ckid);
		try {
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select rqckl from outstored where ckid= ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, ckid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rqckl = rs.getInt("rqckl");
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			System.out.println("系统错误4" + e);
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		// System.out.println(rqckl);
		return rqckl;
	}

	// 获取最新的库存点某种容器出库编号
	public String getCkidToRqzl(String kcdid, String ckxz, String rqxh) {
		String ckid = null;
		try {
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select MAX(LEFT(ckid,10)) from outstored where kcdid = ? AND ckxz = ? AND rqxh = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, kcdid);
			ps.setString(2, ckxz);
			ps.setString(3, rqxh);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ckid = rs.getString(1);
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			System.out.println("系统错误5" + e);
			ckid = null;
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return ckid;
	}

	public String getDate() {
		Date dt = new Date();
		String date = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		date = sdf.format(dt);
		return date;
	}

	// 获取最新的rqzl
	public int getRqzl(String kcdid, String chxz, String rqxh) {
		String maxCkid = getCkidToRqzl(kcdid, chxz, rqxh);
		if (maxCkid == null) {
			return 0;
		} else {
			int rqzl = 0;
			String ckid = maxCkid + kcdid;
			try {
				conn = DBFactory.INSTANCE.getConnection();
				String sql = "select rqzl from outstored where ckid= ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, ckid);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					rqzl = rs.getInt("rqzl");
				}
				ps.close();
				rs.close();
			} catch (Exception e) {
				System.out.println("系统错误6" + e);
			} finally {
				DBFactory.INSTANCE.closeConnection(conn);
			}
			return rqzl;
		}
	}

	// 根据wpid获取rqrl
	public int getRqrlByWpid(String wpid) {
		int rqrl = 0;
		try {
			conn = DBFactory.INSTANCE.getConnection();
			String sql = "select rqrl from palletpart where wpid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, wpid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rqrl = rs.getInt("rqrl");
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			System.out.println("系统错误7" + e);
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		return rqrl;
	}
	// 数据存储到数据库

	public int installData(List<Map<String, Object>> listOutstored, Map<String, Object> mapOutstoreb)
			throws SQLException {
		int result = 0;
		try {
			conn = DBFactory.INSTANCE.getConnection();
			conn.setAutoCommit(false);
			// 更新出库记录
			String sql1 = "INSERT INTO outstoreb (ckid,kcdid,ckxz,tokcdid,cph,khid,jbr) VALUES (?,?,?,?,?,?,?) ";
			ps = conn.prepareStatement(sql1);
			ps.setString(1, (String) mapOutstoreb.get("ckid"));
			ps.setString(2, (String) mapOutstoreb.get("kcdid"));
			ps.setString(3, (String) mapOutstoreb.get("ckxz"));
			ps.setString(4, (String) mapOutstoreb.get("tokcdid"));
			ps.setString(5, (String) mapOutstoreb.get("cph"));
			ps.setString(6, (String) mapOutstoreb.get("khid"));
			ps.setString(7, (String) mapOutstoreb.get("jbr"));
			ps.executeUpdate();

			String sql2 = "INSERT INTO outstored (ckid,kcdid,ckxz,wpid,rqxh,wpys,rqys,rqinit,rqzl,rqckl) VALUES (?,?,?,?,?,?,?,?,?,?) ";
			ps = conn.prepareStatement(sql2);
			for (Map<String, Object> list : listOutstored) {
				ps.setString(1, (String) list.get("ckid"));
				ps.setString(2, (String) list.get("kcdid"));
				ps.setString(3, (String) list.get("ckxz"));
				ps.setString(4, (String) list.get("wpid"));
				ps.setString(5, (String) list.get("rqxh"));
				ps.setInt(6, (int) list.get("wpys"));
				ps.setInt(7, (int) list.get("rqys"));
				ps.setInt(8, (int) list.get("rqinit"));
				ps.setInt(9, (int) list.get("rqzl"));
				ps.setInt(10, (int) list.get("rqckl"));
				ps.addBatch();
			}
			ps.executeBatch();
			String sql3 = "UPDATE instored SET rqkyl = ? WHERE rkid= ? AND rqxh = ? ";
			ps = conn.prepareStatement(sql3);
			for (Map<String, Object> list : listOutstored) {
				ps.setInt(1, (int) list.get("rqkyl"));
				ps.setString(2, (String) list.get("rkid"));
				ps.setString(3, (String) list.get("rqxh"));
				ps.addBatch();
			}
			ps.executeBatch();
			String sql4 = "update outstored set rqkyl=(select rqkyl from instored where id=(select max(id) from instored where "
					+ "kcdid= ? and rqxh= ?)) + (isnull((select top 1 isnull(rqckl,0) from outstored where "
					+ "kcdid= ? and  rqxh= ? order by id desc),0)) - rqckl from outstored where "
					+ "ckid= ? and kcdid= ? and rqxh= ?";
			ps = conn.prepareStatement(sql4);
			for (Map<String, Object> list : listOutstored) {
				String kcdid=(String)list.get("kcdid");
				String rqxh=(String)list.get("rqxh");
				ps.setString(1, kcdid);
				ps.setString(2, rqxh);
				ps.setString(3, kcdid);
				ps.setString(4, rqxh);
				ps.setString(5, (String)list.get("ckid"));
				ps.setString(6, kcdid);
				ps.setString(7, rqxh);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			ps.close();
			result = 1;
		} catch (SQLException se) {
			System.out.println(se);
			try {
				// 产生的任何SQL异常都需要进行回滚,并设置为系统默认的提交方式,即为TRUE
				if (conn != null) {
					conn.rollback();
					conn.setAutoCommit(true);
					result = -1;
				}
			} catch (SQLException se1) {
				System.out.println(se1);
			}
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}

		return result;
	}

}