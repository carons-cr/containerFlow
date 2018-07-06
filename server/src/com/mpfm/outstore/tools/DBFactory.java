package com.mpfm.outstore.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//获取连接
public class DBFactory {
	String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	String dbURL = "jdbc:sqlserver://120.25.234.207;DatabaseName=pfmsdb";
	String userName = "sa";
	String userPwd = "ZUOtx007";
/*	 String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	 String dbURL = "jdbc:sqlserver://127.0.0.1;DatabaseName=pfmsdb";
	 String userName = "sa";
	 String userPwd = "root";*/
	public static DBFactory INSTANCE;
	static {
		INSTANCE = new DBFactory();
			}
	public Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(dbURL, userName, userPwd);
		} catch (SQLException e) {
			 System.out.println("获取失败"+e);
		} catch (ClassNotFoundException e) {
			 System.out.println("驱动链接失败"+e);
		}
		return conn;
	}
	public void closeConnection(Connection connection) {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
