package com.kiwi.bubble.appengine.server.userinfo;

import java.util.ArrayList;
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
	
	@Persistent
	private List<Long> friends;

	public UserInfo(String email, String name, String password) {
		super();
		this.email = email;
		this.name = name;
		this.password = password;
		this.joinDate = null;
		this.favorites = new ArrayList<Long>();
		this.friends = new ArrayList<Long>();
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

	public List<Long> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Long> favorites) {
		this.favorites = favorites;
	}

	public List<Long> getFriends() {
		return friends;
	}

	public void setFriends(List<Long> friends) {
		this.friends = friends;
	}
	
	public void addFriend(Long friend) {
		friends.add(friend);
	}
	
	public void removeFriend(Long friend) {
		friends.remove(friend);
	}
	
	public boolean isFriend(Long friend) {
		return friends.contains(friend);
	}
}
