package com.mpfm.instore.dao;

public class GetRqInfoAndRklyDaoFactory {
	public static GetRqInfoAndRklyDaoFactory INSTANCE;
	static {
		INSTANCE=new GetRqInfoAndRklyDaoFactory();
	}
	private GetRqInfoAndRklyDaoFactory() {
		super();
	}
	public String getRqInfoAndRkly(String data) {
		return new GetRqInfoAndRklyDao().getRqInfoAndRkly(data);
	}
}
