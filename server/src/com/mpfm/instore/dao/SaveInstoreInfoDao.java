package com.mpfm.instore.dao;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpfm.outstore.tools.DBFactory;


/**
 * @author caron
 * 根据来源单号和用户id保存入库信息
 */
public class SaveInstoreInfoDao {

	//根据kcdid生成并获得rkid
	public String getRkid(Connection connection,String kcdid) {
		String recentRkid="";
		String rkid="";
		try {
			PreparedStatement preparedStatement=connection.prepareStatement("select rkid from instoreb where kcdid=? AND rksj =(select MAX(rksj) from instoreb where kcdid= ? )");
			preparedStatement.setString(1, kcdid);
			preparedStatement.setString(2, kcdid);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				//获得最新的rkid
				recentRkid = resultSet.getString("rkid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//根据入库的日期以及最新的rkid生成入库时的rkid
		String dateStr = new SimpleDateFormat("yyMMdd").format(new Date());
		int count = 0;
		if (!recentRkid.equals("") && recentRkid.length()>0 && dateStr.equals(recentRkid.substring(0, 6))) {
			count = 1 + Integer.parseInt(recentRkid.substring(6, 10));
		} else {
			count = 1;
		}
		rkid = dateStr + String.format("%04d", count) + kcdid;
		return rkid;
	}
	
	//根据ckid获得入库基本信息，cph,rkxz,khid,rkly
	public Map<String,Object> getBasicInstoreInfo(Connection connection,String ckid){
		PreparedStatement preparedStatement=null;
		Map<String,Object> map=new HashMap<String,Object>();
		try {
			connection = DBFactory.INSTANCE.getConnection();
			String sql = "select cph,ckxz,khid,kcdid from outstoreb where ckid = ? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, ckid);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				map.put("cph",resultSet.getString("cph"));
				map.put("rkxz",resultSet.getString("ckxz"));
				map.put("khid",resultSet.getString("khid"));
				map.put("rkly",resultSet.getString("kcdid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	//根据界面上每条容器信息的数据获得此次入库的同一入库性质的该型号容器的该零件的wpys
  	public int getWpys(Connection connection,String ckid,String rkxz,String rqxh,String wpid){
  		PreparedStatement preparedStatement=null;
  		int wpys=0;
  		try {
  		    //从出库表查询得到同一入库性质，同一容器型号，同一零件号的wpys
  			connection = DBFactory.INSTANCE.getConnection();
  			String sql = "select wpys from outstored where ckid = ? and ckxz = ? and rqxh=? and wpid=?";
  			preparedStatement = connection.prepareStatement(sql);
  			preparedStatement.setString(1, ckid);
  			preparedStatement.setString(2, rkxz);
  			preparedStatement.setString(3, rqxh);
  			preparedStatement.setString(4, wpid);
  			ResultSet resultSet = preparedStatement.executeQuery();
  			if (resultSet.next()) {
  				wpys=resultSet.getInt("wpys");
  			}   
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  		return wpys;
  	}
	
	//根据界面上每条容器信息的数据获得该库存点的同一入库性质的该型号容器的rqzl
    public int getRqzl(Connection connection,String kcdid,String rqxh,String rkxz,int rqss) {
    	int recentRqzl=0;
		int rqzl=0;
		try {
			//获得入库的库存点的同一入库性质，同一容器型号的最大rqzl
			PreparedStatement preparedStatement=connection.prepareStatement("select rqzl from instored where kcdid=? and rqxh = ? and rkxz = ? order by id desc");
			preparedStatement.setString(1, kcdid);
			preparedStatement.setString(2, rqxh);
			preparedStatement.setString(3, rkxz);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				recentRqzl = resultSet.getInt("rqzl");
			}
		} catch (Exception e) {
			//System.out.println("系统错误4" + e);
			e.printStackTrace();
		}
		//将入库前库存点的同一入库性质，同一容器型号的最大rqzl加上rqss得到入库后的rqzl
		rqzl=recentRqzl+rqss;
		return rqzl;
    }
    
    //根据界面上每条容器信息的数据获得该仓库的该型号容器的rqkyl
    public int getRqkyl(Connection connection,String kcdid,String rqxh,int rqss) {
    	int recentRqkyl=0;
		int rqkyl=0;
		try {
			//获得入库的库存点的同一容器型号的最大rqkyl
			//此处不应该查找最近时间的rkid的rqkyl，因为此时最新的rkid在instored表中还没有
			PreparedStatement preparedStatement=connection.prepareStatement("select rqkyl from instored where kcdid=? and rqxh = ? order by id desc");
			preparedStatement.setString(1, kcdid);
			preparedStatement.setString(2, rqxh);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				recentRqkyl = resultSet.getInt("rqkyl");
			}
		} catch (Exception e) {
			//System.out.println("系统错误5" + e);
			e.printStackTrace();
		}
		//将入库前库存点的同一容器型号的最大rqkyl加上rqss得到入库后的rqkyl
		rqkyl=recentRqkyl+rqss;
		return rqkyl;
    }
    
    /**输入：{\"kcdid\":\"MAF\",\"uerid\":\"zxk\",\"lyid\":"\1703140001W35A1A\",\"kcdmc\":\"捷富凯中心库\",\"rkly:\"二厂总装,\"rqInfoList\":[{\"wpid\":\"无\",\"rqxh\":\"25185\",\"rqys\":\"12\"}]}
	 * 输出：{\"saveTab\":\"1"\}
	 * * //插入：rkid,ckid,kcdid,rkly,cph,rkxz,rksj,khid,jbr,spr(instoreb)
	 * rkid,wpid,wpys,wpss,rqxh,rqys,rqss,rqinit,rqzl,rqkyl(instored)*/
    //根据界面的数据去出库表查询入库表中所需要的数据，插入入库记录到入库表
  	public String saveInstoreInfo(String data) {
  		Connection connection = null;
  		PreparedStatement preparedStatement=null;
  		String saveTab="0";//更新记录条数，如果是0代表未保存过   
  		ObjectMapper objectMapper=null;
  		Map mapFromData=null;
  		Map<String,Object> map=null;
  		String ckid="";
  		String kcdid="";
  		String rkly="";
  		ArrayList<Map<String,Object>> rQInfoList=null;
  		String userid="";
  		
  		String rkid="";
  		String rkxz="";
  		Date rksj=null;
  		String cph="";
  		String khid="";
  		int wpys=0;
  		String rqxh="";
  		String wpid="";
  		int rqys=0;
  		int rqss=0;
  		int rqzl=0;
        int rqkyl=0;      
        Map<String,Object> mapResult=new  HashMap<String,Object>();
        String jsonStr="";
  		try {
  			//读取从界面获取的数据lyid,kcdid,rkly,rqInfoList,userid
  			objectMapper = new ObjectMapper();
  			mapFromData = objectMapper.readValue(data, Map.class);
  			ckid=(String) mapFromData.get("lyid");
  			kcdid=(String) mapFromData.get("kcdid");
  			rQInfoList =(ArrayList<Map<String, Object>>) mapFromData.get("rqInfoList");
  			userid=(String) mapFromData.get("userid");
  			connection = DBFactory.INSTANCE.getConnection();
  			connection.setAutoCommit(false);
  			
  			//根据ckid去入库表中查询是否有rkid，如果有则代表已保存过返回saveTab=2，如果没有则进行入库数据的保存
  			preparedStatement=connection.prepareStatement("select rkid from instoreb where ckid = ?");
  			preparedStatement.setString(1,ckid );
  			ResultSet resultSet = preparedStatement.executeQuery();
  			if(resultSet.next()) {//先定位到当前行
  				saveTab="2"; //代表已经保存过的，这已经是第二或第n次进行保存操作了
  			}else {
  				//根据kcdid得到此次入库的rkid
  				rkid=getRkid(connection,kcdid);
  				//根据ckid去出库表获得相关入库基本信息rkxz，cph，khid
  				map=getBasicInstoreInfo(connection,ckid);
  				rkxz=(String) map.get("rkxz");    
  				cph=(String) map.get("cph");
  				khid=(String) map.get("khid");
  				rkly=(String) map.get("rkly");
  				
  				//将此次入库的基本信息插入到instoreb表中
  				preparedStatement=connection.prepareStatement("insert into instoreb (ckid,rkid,kcdid,rkly,rkxz,cph,khid,jbr) VALUES (?,?,?,?,?,?,?,?)");
  				preparedStatement.setString(1, ckid);
  				preparedStatement.setString(2, rkid);
  				preparedStatement.setString(3, kcdid);
  				preparedStatement.setString(4, rkly);
  				preparedStatement.setString(5, rkxz);
  				preparedStatement.setString(6, cph);
  				preparedStatement.setString(7, khid);
  				preparedStatement.setString(8, userid);
  				int updateInstorebCount=preparedStatement.executeUpdate();
  			    if(updateInstorebCount==1) {
  			        //将此次入库的详细信息，即所有的入库容器记录插入到instored表中 	
  			    	int updateInstoredCount=0;
  	  			    for (Map<String, Object> rqInfo : rQInfoList) {
  	  				    preparedStatement=connection.prepareStatement("insert into instored (rkid,kcdid,rkxz,wpid,wpys,wpss,rqxh,rqys,rqss,rqinit,rqzl,rqkyl) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");			
  	  				    //得到界面一条容器记录中的wpid,rqxh,rqys,rqss
  	  					wpid=(String) rqInfo.get("wpid");
  	  					rqxh=(String) rqInfo.get("rqxh");
  	  					//根据已知信息去出库表中查询并获得wpys
  	  					wpys=getWpys(connection,ckid,rkxz,rqxh,wpid);
  	  					rqys=(int) rqInfo.get("rqys");
  	  					rqss=(int) rqInfo.get("rqss");    
  	  					//根据已知信息去入库表中得到此次入库后的rqzl,rqkyl
  	  					rqzl=getRqzl(connection,kcdid,rqxh,rkxz,rqss);
  	  					rqkyl=getRqkyl(connection,kcdid,rqxh,rqss);
  	  					//将界面上每条容器信息的记录进行批量存入instored表
  	  					preparedStatement.setString(1, rkid);
  	  					preparedStatement.setString(2, kcdid);
  	  					preparedStatement.setString(3, rkxz);
  	  					preparedStatement.setString(4, wpid);
  	  					preparedStatement.setInt(5, wpys);
  	  					preparedStatement.setInt(6, wpys);
  	  					preparedStatement.setString(7,rqxh);
  	  					preparedStatement.setInt(8, rqys);
  	  					preparedStatement.setInt(9, rqss);
  	  					preparedStatement.setInt(10, rqys);
  	  					preparedStatement.setInt(11, rqzl);
  	  					preparedStatement.setInt(12, rqkyl);
  	  					//最终没采用批处理，因为这样会导致所有查询结束之后再插入，无法在每次插入之前执行查询操作
  	  					//preparedStatement.addBatch();
  	  				    updateInstoredCount+=preparedStatement.executeUpdate();
  	  				}
	  				if(updateInstoredCount>0) {
  	  					saveTab="1";//1代表保存成功
  	  				}
  			    }
  				
  			}
  			mapResult.put("saveTab", saveTab);
			jsonStr=objectMapper.writeValueAsString(mapResult);
			//System.out.println(jsonStr);
  			connection.commit();
  		}catch (SQLException sqlException) {
  			sqlException.printStackTrace();
  			try {
  				// 产生的任何SQL异常都需要进行回滚,并设置为系统默认的提交方式,即为TRUE
  				if (connection != null) {
  					connection.rollback();
  					connection.setAutoCommit(true);
  				}
  			} catch (SQLException sqlException1) {
  				sqlException1.printStackTrace();
  			}
  		}catch (Exception e) {
  			e.printStackTrace();
  		} finally {
  			DBFactory.INSTANCE.closeConnection(connection);
  		}	
  		return jsonStr;		
  	}
    
    
}
