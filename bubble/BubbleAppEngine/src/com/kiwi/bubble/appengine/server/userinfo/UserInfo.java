package com.kiwi.bubble.appengine.server.userinfo;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.kiwi.bubble.appengine.server.ModelBase;

@PersistenceCapable
public class UserInfo extends ModelBase {
	@Persistent
	private String email;
	
	@Persistent
	private String name;
	
	@Persistent
	private String password;
	
	@Persistent
	private Date joinDate;
	
	@Persistent
	private List<Long> favorites;

	public UserInfo(String email, String name, String password) {
		super();
		this.email = email;
		this.name = name;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
