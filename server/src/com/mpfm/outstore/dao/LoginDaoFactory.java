package com.mpfm.outstore.dao;
public class LoginDaoFactory {
	public static LoginDaoFactory INSTANCE;
	static {
		INSTANCE = new LoginDaoFactory();
	}
	private LoginDaoFactory() {
		super();
	}
	public String  getUsersRole(String data) {
		return new LoginDao().getUsersRole(data);
	}
}
