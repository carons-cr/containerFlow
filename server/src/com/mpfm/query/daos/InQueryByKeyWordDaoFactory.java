package com.mpfm.query.daos;


import com.fasterxml.jackson.databind.ObjectMapper;

public class InQueryByKeyWordDaoFactory {
	ObjectMapper mapper = new ObjectMapper();
	public static InQueryByKeyWordDaoFactory INSTANCE;
	static {
		INSTANCE = new InQueryByKeyWordDaoFactory();
	}

	private InQueryByKeyWordDaoFactory() {
		super();
	}


	public String  QueryByKeyWord(String data) {
		return new InQueryByKeyWordDao().QueryByKeyWord(data,"");
	}

}
