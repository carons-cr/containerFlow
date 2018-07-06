package com.mpfm.outstore.dao;

public class GetrqByrqxhDaoFactory {
	public static GetrqByrqxhDaoFactory INSTANCE;
	static {
		INSTANCE = new GetrqByrqxhDaoFactory();
	}

	private GetrqByrqxhDaoFactory() {
		super();
	}

	/**
	 * 查询用户是否存在
	 */
	public String  getRQByrqxh(String data) {
		return new GetrqByrqxhDao().getRQByrqxh(data);
	}

}
