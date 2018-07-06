package com.mpfm.instore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpfm.outstore.tools.DBFactory;

/**
 * @author caron
 * 根据库存点id获得所有来源单号和库存点名称
 */
public class GetLyidListAndKcdmcDao {

	/**根据kcdid获得一些入库基本信息，即该库存点所有未经入库的lyid，kcdmc
	输入{ \"kcdid\": \"MAF\"}
	输出{\"kcdmc\":\"捷富凯中心库\",\"lyidList\":[{\"ckid\":\"1703140001W35A1A\"}
	,{\"ckid\":\"1703100001W35A1A\"}]}*/
	public String getLyidListAndKcdmc(String data) {
		ObjectMapper objectMapper = new ObjectMapper();
		Connection connection = null;
		PreparedStatement preparedStatement;
		Map mapFromData;
		List<Map<String, Object>> lyidList = new ArrayList<Map<String, Object>>();
		String kcdmc = "";
		String jsonStr="";
		try {
			//将传入的json数据转为map,并从其中取出所需的值kcdid
			mapFromData = objectMapper.readValue(data, Map.class);
			String kcdid = (String) mapFromData.get("kcdid");
			//建立数据库连接
			connection = DBFactory.INSTANCE.getConnection();
			//以kcdid为条件查询所有ckid,将其存入lyidList
			String checkCkidSql1="select ckid from outstoreb where tokcdid = ? and (ckxz = '01' or ckxz = '03')";
			//不知道为什么not in在这不起作用,暂时没用这种方法，以后再看下
			//String checkCkidSql2=checkCkidSql1+"and ckid not in (select ckid from instoreb where kcdid=?)";
			preparedStatement = connection.prepareStatement(checkCkidSql1);
			preparedStatement.setString(1, kcdid);
			//preparedStatement.setString(2, kcdid);
			ResultSet checkCkidResultSet1 = preparedStatement.executeQuery();
			while (checkCkidResultSet1.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ckid", checkCkidResultSet1.getString("ckid"));
				lyidList.add(map);
			}
			//如果该来源单号已入库，则从入库单号集中删除
			String checkCkidSql2="select ckid from instoreb where kcdid = ?";
			preparedStatement = connection.prepareStatement(checkCkidSql2);
			preparedStatement.setString(1, kcdid);
			ResultSet checkCkidResultSet2 = preparedStatement.executeQuery();
			while (checkCkidResultSet2.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ckid", checkCkidResultSet2.getString("ckid"));
				for(int i=0;i<lyidList.size();i++) {
					if(((String)lyidList.get(i).get("ckid")).equals((String)map.get("ckid"))) {
						lyidList.remove(i);
					}
				}
			}
			//以kcdid为条件查询kcdmc,将其存入kcdmc
			String checkKcdmcSql = "select kcdmc from storeplace where kcdid = ? ";
			preparedStatement = connection.prepareStatement(checkKcdmcSql);
			preparedStatement.setString(1, kcdid);
			ResultSet checkKcdmcResultSet = preparedStatement.executeQuery();
			if (checkKcdmcResultSet.next()) {
				kcdmc = checkKcdmcResultSet.getString("kcdmc");
			}
			//将所需要的数据lyidList,kcdmc存入map，并转为json字符串返回
			Map<String, Object> mapResult=new HashMap<String,Object>();
			mapResult.put("lyidList", lyidList);
			mapResult.put("kcdmc", kcdmc);
			jsonStr=objectMapper.writeValueAsString(mapResult);
			//System.out.println(jsonStr);
		} catch (Exception e) {
			//System.out.println("系统错误" + e);
			e.printStackTrace();
		} finally {
			DBFactory.INSTANCE.closeConnection(connection);
		}
		return jsonStr;
	}
	
}
