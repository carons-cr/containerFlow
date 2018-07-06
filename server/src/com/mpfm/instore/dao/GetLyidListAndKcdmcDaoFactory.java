package com.mpfm.instore.dao;

public class GetLyidListAndKcdmcDaoFactory {
     public static GetLyidListAndKcdmcDaoFactory INSTANCE;
     static {
    	 INSTANCE=new GetLyidListAndKcdmcDaoFactory();
     }
     private GetLyidListAndKcdmcDaoFactory() {
    	 super();
     }
     public String getLyidListAndKcdmc(String data) {
    	 return new GetLyidListAndKcdmcDao().getLyidListAndKcdmc(data);
     }
}
