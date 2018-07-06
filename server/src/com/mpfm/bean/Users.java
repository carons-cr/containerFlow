package com.mpfm.bean;

/**
 * Created by liust on 17-9-18.
 */

public class Users {
	private int id;
	private String userid;
	private String username;
	private String role;
	private String menus;
	private String password;
	private String kcdid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getMenus() {
		return menus;
	}

	public void setMenus(String menus) {
		this.menus = menus;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getKcdid() {
		return kcdid;
	}

	public void setKcdid(String kcdid) {
		this.kcdid = kcdid;
	}

}
