package com.mpfm.outstore.dao;

public class GetrqbywpidDaoFactory {
	public static GetrqbywpidDaoFactory INSTANCE;
	static {
		INSTANCE = new GetrqbywpidDaoFactory();
	}

	private GetrqbywpidDaoFactory() {
		super();
	}

	/**
	 * 查询用户是否存在
	 */
	public String  getRQbywpid(String data) {
		return new GetrqbywpidDao().getRQbywpid(data);
	}

}
