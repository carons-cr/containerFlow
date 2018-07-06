package com.mpfm.outstore.dao;

public class GetOutStoreDaoFactory {
	public static GetOutStoreDaoFactory INSTANCE;
	static {
		INSTANCE = new GetOutStoreDaoFactory();
	}
	private GetOutStoreDaoFactory() {
		super();
	}
	public String  getOutStoreDao(String data) {
		return new GetOutStoreDao().getOutStoreDao(data);
	}	
}
