package com.mpfm.query.daos;


import com.fasterxml.jackson.databind.ObjectMapper;

public class OutQueryByKeyWordDaoFactory {
	ObjectMapper mapper = new ObjectMapper();
	public static OutQueryByKeyWordDaoFactory INSTANCE;
	static {
		INSTANCE=new OutQueryByKeyWordDaoFactory();
	}
	private OutQueryByKeyWordDaoFactory() {
		super();
	}
	public String QueryByKeyWord(String data) {
		
		 return new OutQueryByKeyWordDao().QueryByKeyWord(data,"");
	}

}
