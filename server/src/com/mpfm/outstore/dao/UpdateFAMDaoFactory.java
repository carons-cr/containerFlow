package com.mpfm.outstore.dao;

public class UpdateFAMDaoFactory {

	public static UpdateFAMDaoFactory INSTANCE;
	static {
		INSTANCE = new UpdateFAMDaoFactory();
	}
	private UpdateFAMDaoFactory() {
		super();
	}
	public String  getFhkhAKcdid() {
		return new UpdateFAMDao().getFhkhAKcdid();
	}
}
