package com.mpfm.instore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpfm.outstore.tools.DBFactory;



/**
 * @author caron
 * 根据据来源单号获得容器信息和入库来源
 */
public class GetRqInfoAndRklyDao {

	List<Map<String, Object>> rqInfoList=null;
	String rkly="";
	//根据rkid去instored表查询rkly以及所有wpid,rqxh,rqys,rqss将其存入rqInfoList
	public void setRqInfoAndRklyByRkid(Connection connection,String rkid){
		PreparedStatement preparedStatement=null;
		
		try {
			//以rkid为条件去instored表查询所有wpid,rqxh,rqys,rqss将其存入rqInfoList
			String checkRqInfoSql = "select wpid,rqxh,rqys,rqss from instored where rkid = ? ";
			preparedStatement = connection.prepareStatement(checkRqInfoSql);
			preparedStatement.setString(1, rkid);
			ResultSet checkRqInfoResultSet = preparedStatement.executeQuery();
			while (checkRqInfoResultSet.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("wpid", checkRqInfoResultSet.getString("wpid"));
				map.put("rqxh", checkRqInfoResultSet.getString("rqxh"));
				map.put("rqys", checkRqInfoResultSet.getInt("rqys"));
				map.put("rqss", checkRqInfoResultSet.getInt("rqss"));
				rqInfoList.add(map);
			}
			
			//以rkid为条件查询该出库点的rkly
			String checkRklySql = "select rkly from instoreb where rkid = ? ";
			preparedStatement = connection.prepareStatement(checkRklySql);
			preparedStatement.setString(1, rkid);
			ResultSet checkRklyResultSet = preparedStatement.executeQuery();
			if (checkRklyResultSet.next()) {
				rkly = checkRklyResultSet.getString("rkly");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	//根据ckid去onstored,storeplace表查询rkly以及所有wpid,rqxh,rqys,rqss将其存入rqInfoList
		public void setRqInfoAndRklyByCkid(Connection connection,String ckid){
			PreparedStatement preparedStatement=null;	
			try {
				
				//以ckid为条件去outstored表查询所有wpid,rqxh,rqys,rqss将其存入rqInfoList
				String checkRqInfoSql = "select wpid,rqxh,rqys from outstored where ckid = ? ";
				preparedStatement = connection.prepareStatement(checkRqInfoSql);
				preparedStatement.setString(1, ckid);
				ResultSet checkRqInfoResultSet = preparedStatement.executeQuery();
				while (checkRqInfoResultSet.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("wpid", checkRqInfoResultSet.getString("wpid"));
					map.put("rqxh", checkRqInfoResultSet.getString("rqxh"));
					map.put("rqys", checkRqInfoResultSet.getInt("rqys"));
					map.put("rqss", checkRqInfoResultSet.getInt("rqys"));
					rqInfoList.add(map);
				}
				
				//以ckid为条件去outstoreb表和storeplace表查询该出库点的kcdmc,将其存入rkly
				String checkRklySql = "select kcdmc from storeplace where kcdid = (select kcdid from outstoreb where ckid = ?)";
				preparedStatement=connection.prepareStatement(checkRklySql);
	  			preparedStatement.setString(1,ckid );
	  			ResultSet checkRklyResultSet = preparedStatement.executeQuery();
	  			if(checkRklyResultSet.next()) {
	  				rkly=checkRklyResultSet.getString("kcdmc");
	  			}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	
	/**根据lyid获得一些入库详细信息，即该来源单号的所有容器信息rqInfoList，rkly
	输入{ \"lyid\": \"1703140001W35A1A\"}
	输出{"\rqInfoList\":[{\"wpid\":\"无\",\"rqxh\":\"25185\",\"rqys\":\"12\",\"rqss\":\"12\"}],\"rkly\":\"二厂总装\"}*/
	public String getRqInfoAndRkly(String data) {
		ObjectMapper objectMapper = null;
		Connection connection = null;
		PreparedStatement preparedStatement;
		Map mapFromData;
		String ckid="";
		String rkid="";
		String jsonStr="";
		try {
			//将界面传入的json数据转为map,并从其中取出所需的值lyid
			objectMapper = new ObjectMapper();
			mapFromData = objectMapper.readValue(data, Map.class);
			ckid = (String) mapFromData.get("lyid");
			rqInfoList = new ArrayList<Map<String, Object>>();
			connection = DBFactory.INSTANCE.getConnection();
			
			//根据ckid去入库表中查询是否有rkid，如果有则代表已保存过直接从入库库表查询相应数据，如果没有则需要去出库表查询所需数据
  			preparedStatement=connection.prepareStatement("select rkid from instoreb where ckid = ?");
  			preparedStatement.setString(1,ckid );
  			ResultSet resultSet = preparedStatement.executeQuery();
  			if(resultSet.next()) {
  				rkid=resultSet.getString("rkid");
  			    setRqInfoAndRklyByRkid(connection, rkid);
  			}else {
		        setRqInfoAndRklyByCkid(connection, ckid);
  			}
  		    //将所需要的数据rqInfoList,rkly存入map，并转为json字符串返回
			Map<String, Object> mapResult=new HashMap<String,Object>();
			mapResult.put("rqInfoList", rqInfoList);
			mapResult.put("rkly", rkly);
			jsonStr=objectMapper.writeValueAsString(mapResult);
		} catch (Exception e) {
			System.out.println("系统错误" + e);
		} finally {
			DBFactory.INSTANCE.closeConnection(connection);
		}
		return jsonStr;
	}
}
