package com.kiwi.bubble.android.common;

import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;


public class BubbleTag extends ModelBase {
	public static final int TAG_TYPE_TEXT = 0;
	public static final int TAG_TYPE_USER = 1;
	public static final int TAG_TYPE_LOCATION = 2;
	
	private int type;
	
	private String text;
	
	private long user;
	
	private String location;
	
	//private long id;

	public BubbleTag(int type) {
		super();
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		setType(TAG_TYPE_TEXT);
		this.text = text;
	}

	public long getUser() {
		return user;
	}

	public void setUser(long user) {
		setType(TAG_TYPE_USER);
		this.user = user;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		setType(TAG_TYPE_LOCATION);
		this.location = location;
	}
	
	public static List<BubbleTag> getAllBubbleTags() {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/tag";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl, client);
		final List<BubbleTag> tags = ObjectParsers.parseBubbleTag(response);
		
		return tags;
	}
	
	public static BubbleTag getBubbleTag(Long id) {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/tag";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + String.valueOf(id), client);
		final List<BubbleTag> tags = ObjectParsers.parseBubbleTag(response);
		
		return tags.size()==0?null:tags.get(0);
	}
	
	public static List<BubbleTag> getBubbleTagMultiple(String id) {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/tag";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + id, client);
		final List<BubbleTag> tags = ObjectParsers.parseBubbleTag(response);
		
		return tags;
	}
}
