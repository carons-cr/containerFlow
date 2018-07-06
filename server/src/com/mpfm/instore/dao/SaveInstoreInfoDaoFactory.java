package com.mpfm.instore.dao;

public class SaveInstoreInfoDaoFactory {
	public static SaveInstoreInfoDaoFactory INSTANCE;
	static {
		INSTANCE=new SaveInstoreInfoDaoFactory();
	}
	private SaveInstoreInfoDaoFactory() {
		super();
	}
	public String saveInstoreInfo(String data) {
		return new SaveInstoreInfoDao().saveInstoreInfo(data);
	}

}
