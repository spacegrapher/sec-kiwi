package com.kiwi.bubble.appengine.server;

public class UserInfoBean {
	private String nickname;
	private String email;
	public UserInfoBean(String nickname, String email) {
		this.nickname = nickname;
		this.email = email;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
