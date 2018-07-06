package com.mpfm.query.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpfm.outstore.tools.DBFactory;
import com.mpfm.query.tools.DataUtil;


public class OutQueryByKeyWordDao {
	

	Map<String, Object> map = new HashMap<String, Object>();

	/**
	 * 根据关键字查询
	 * @param data 
	 * @return json字符串
	 */
	
	public String QueryByKeyWord(String data, String is_admin_kcdid) {
		
		ObjectMapper mapper = new ObjectMapper();
		Connection conn = null;
		PreparedStatement ps = null;
		List<Map<String,Object>> list = new ArrayList<Map<String, Object>>(); 
		DataUtil dataUtil = new DataUtil();
		
		try {
			Map map = mapper.readValue(data, Map.class);

			is_admin_kcdid = (String) map.get("kcdid");
			
			String time1 = (String) map.get("start_date"); // 2017-08-01
			String time2 = time1.replaceAll("-","");
			String start_date = time2.substring(2);      //170801
		
			
			String keyword =  (String) map.get("keyword"); 	
			
			String stime1 = (String) map.get("end_date"); // 2017-08-01
			String end_date;
			if(stime1 == "") {
				end_date = stime1;
			}else {
				String stime2 = stime1.replaceAll("-","");
				end_date = stime2.substring(2);          //170801
			}
			
			// 系统当天日期
			Date day = new Date();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        String day1 = sdf.format(day).replaceAll("-","");
	        String today = day1.substring(2);
	        
	        
			conn = DBFactory.INSTANCE.getConnection();
				
				// 1.开始日期+kcdid
				if(keyword == "" && end_date == "" && start_date != "" ) {
					
					if(is_admin_kcdid == "all") {
						
						String AllRqToday_Admin = "SELECT b.ckid,e.kcdmc,d.rqys,d.rqxh \r\n" + 
								"FROM outstoreb b,outstored d,storeplace e \r\n" + 
								"WHERE b.ckid = d.ckid \r\n" + 
								"AND b.tokcdid = e.kcdid \r\n" + 
								"AND substring(b.ckid,1,6) >= ? "+
								"AND substring(b.ckid,1,6) <= ? ;";
						
						ps = conn.prepareStatement(AllRqToday_Admin);
						
						ps.setString(1, start_date);
						ps.setString(2, today);
						
					}else {
						String AllRqToday = "SELECT b.ckid,e.kcdmc,d.rqys,d.rqxh \r\n" + 
								"FROM outstoreb b,outstored d,storeplace e \r\n" + 
								"WHERE b.ckid = d.ckid \r\n" + 
								"AND b.tokcdid = e.kcdid \r\n" + 
								"AND b.kcdid = ? \r\n" + 
								"AND substring(b.ckid,1,6) >= ? "+
								"AND substring(b.ckid,1,6) <= ? ;";
						
						ps = conn.prepareStatement(AllRqToday);
						
						ps.setString(1, is_admin_kcdid);
						ps.setString(2, start_date);
						ps.setString(3, today);
					}
					
					// 2.关键字+开始日期+kcdid
				}else if(keyword != "" && end_date == "" && start_date != "") {
					
					if(is_admin_kcdid == "all") {
						
						String OneRqInOneDay_Admin = "SELECT b.ckid,e.kcdmc,d.rqys,d.rqxh \r\n" + 
								"FROM outstoreb b,outstored d,storeplace e \r\n" + 
								"WHERE b.ckid = d.ckid \r\n" + 
								"AND b.tokcdid = e.kcdid \r\n" + 
								"AND (e.kcdmc LIKE '%'+ ? + '%'\r\n" + 
								"OR d.rqxh LIKE '%'+ ? + '%') \r\n" + 
								"AND substring(b.ckid,1,6) >= ? ;";
						
						ps = conn.prepareStatement(OneRqInOneDay_Admin);
						
						ps.setString(1, keyword);
						ps.setString(2, keyword);
						ps.setString(3, start_date);
						
					}else {
						String OneRqInOneDay = "SELECT b.ckid,e.kcdmc,d.rqys,d.rqxh \r\n" +
								"FROM outstoreb b,outstored d,storeplace e \r\n" + 
								"WHERE b.ckid = d.ckid \r\n" + 
								"AND b.tokcdid = e.kcdid \r\n" + 
								"AND (e.kcdmc LIKE '%'+ ? + '%' \r\n" + 
								"OR d.rqxh LIKE '%'+ ? + '%') \r\n" + 
								"AND b.kcdid = ? \r\n" + 
								"AND substring(b.ckid,1,6) >= ? ;";
						
						ps = conn.prepareStatement(OneRqInOneDay);
						
						ps.setString(1, keyword);
						ps.setString(2, keyword);
						ps.setString(3, is_admin_kcdid);
						ps.setString(4, start_date);
					}
					
					// 3.开始日期+结束日期+kcdid 
				}else if(keyword == "" && end_date != "" && start_date != "") {
					
					int start_datei = Integer.parseInt(start_date);
					int end_datei = Integer.parseInt(end_date);
					
					if(is_admin_kcdid == "all") {
						
						String AllRqInPeroid_Admin = "SELECT b.ckid,e.kcdmc,d.rqys,d.rqxh \r\n" +
								"FROM outstoreb b,outstored d,storeplace e \r\n" +
								"WHERE b.ckid = d.ckid \r\n" + 
								"AND b.tokcdid = e.kcdid \r\n" +
								"AND substring(b.ckid,1,6) >= ? "+
								"AND substring(b.ckid,1,6) <= ? ;";
						
						ps = conn.prepareStatement(AllRqInPeroid_Admin);
						
						ps.setInt(1, start_datei);
						ps.setInt(2, end_datei);
						
					}else {
						String AllRqInPeroid = "SELECT b.ckid,e.kcdmc,d.rqys,d.rqxh \r\n" + 
								"FROM outstoreb b,outstored d,storeplace e \r\n" +
								"WHERE b.ckid = d.ckid \r\n" + 
								"AND b.tokcdid = e.kcdid \r\n" +
								"AND b.kcdid = ? \r\n" + 
								"AND substring(b.ckid,1,6) >= ? "+
								"AND substring(b.ckid,1,6) <= ?;";
						
						ps = conn.prepareStatement(AllRqInPeroid);
						
						ps.setString(1, is_admin_kcdid);
						ps.setInt(2, start_datei);
						ps.setInt(3, end_datei);
					}
					
					// 4.关键词+开始日期+结束日期+kcdid
				}else if(keyword != "" && end_date != "" && start_date != "") {
					
					int start_datei = Integer.parseInt(start_date);
					int end_datei = Integer.parseInt(end_date);
					
					if(is_admin_kcdid == "all") {
						
						String OneRqInPeroid_Admin = "SELECT b.ckid,e.kcdmc,d.rqys,d.rqxh \r\n" +
								"FROM outstoreb b,outstored d,storeplace e \r\n" +
								"WHERE b.ckid = d.ckid \r\n" + 
								"AND b.tokcdid = e.kcdid \r\n" +
								"AND (e.kcdmc LIKE '%'+ ? + '%' \r\n" + 
								"OR d.rqxh LIKE '%'+ ? + '%') \r\n" + 
								"AND substring(b.ckid,1,6) >= ? "+
								"AND substring(b.ckid,1,6) <= ? ;";
						
						ps = conn.prepareStatement(OneRqInPeroid_Admin);
						
						ps.setString(1, keyword);
						ps.setString(2, keyword);
						ps.setInt(3, start_datei);
						ps.setInt(4, end_datei);
						
					}else {
						String OneRqInPeroid = "SELECT b.ckid,e.kcdmc,d.rqys,d.rqxh \r\n" +
								"FROM outstoreb b,outstored d,storeplace e \r\n" +
								"WHERE b.ckid = d.ckid \r\n" + 
								"AND b.tokcdid = e.kcdid \r\n" +
								"AND (e.kcdmc LIKE '%'+ ? + '%' \r\n" + 
								"OR d.rqxh LIKE '%'+ ? + '%') \r\n" + 
								"AND b.kcdid = ? \r\n" + 
								"AND substring(b.ckid,1,6) >= ? "+
								"AND substring(b.ckid,1,6) <= ?;";
						
						ps = conn.prepareStatement(OneRqInPeroid);
						
						ps.setString(1, keyword);
						ps.setString(2, keyword);
						ps.setString(3, is_admin_kcdid);
						ps.setInt(4, start_datei);
						ps.setInt(5, end_datei);
						
					}
				}else {
					System.out.println("查询失败，未知的查询方式");
				}
				
		ResultSet rs = ps.executeQuery();
			
		while (rs.next()) {
				Map<String, Object> toMap = new HashMap<String, Object>();
				toMap.put("OutQuery_rkdh", rs.getString("ckid"));
				toMap.put("OutQuery_ly", rs.getString("kcdmc"));
				toMap.put("OutQuery_rqxh", rs.getString("rqxh"));
	     		toMap.put("OutQuery_rks", rs.getInt("rqys"));
	     		
				toMap.put("state", 1);
				list.add(toMap);
			}
		} catch (Exception e) {
			System.out.println("系统错误" + e);
		} finally {
			DBFactory.INSTANCE.closeConnection(conn);
		}
		
		// list转成json
		String jsonResult = dataUtil.listToJson(list);
		System.out.println(jsonResult);
		
		return jsonResult;
	}
}

	
