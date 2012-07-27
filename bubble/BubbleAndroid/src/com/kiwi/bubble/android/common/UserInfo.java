package com.kiwi.bubble.android.common;

import java.util.Date;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;

public class UserInfo extends ModelBase {
	private String email;
	
	private String name;
	
	private Date joinDate;
	
	private List<Long> favorites;

	public UserInfo(String email, String name) {
		super();
		this.email = email;
		this.name = name;
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

	
	public static UserInfo getUserInfo(long id) {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/user";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + id, client);
		UserInfo user = ObjectParsers.parseUserInfo(response);
		
		return user;
	}	
}
