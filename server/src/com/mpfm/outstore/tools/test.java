package com.mpfm.outstore.tools;

import com.mpfm.outstore.dao.GetOutStoreDao;


public class test {
	public static void main(String[] args) {

		String a = new GetOutStoreDao().createCkid("99900W0406B-000");
		System.out.println(a);
	}
}
