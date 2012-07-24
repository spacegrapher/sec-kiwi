package com.kiwi.bubble.appengine.server.tag;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.GeoPt;
import com.kiwi.bubble.appengine.server.ModelBase;

@PersistenceCapable
public class BubbleTag extends ModelBase {
	public static final int TAG_TYPE_TEXT = 0;
	public static final int TAG_TYPE_USER = 1;
	public static final int TAG_TYPE_LOCATION = 2;
	
	@Persistent
	private int type;
	
	@Persistent
	private String text;
	
	@Persistent
	private long user;
	
	@Persistent
	private GeoPt location;
	
	@Persistent
	private int count;

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

	public GeoPt getLocation() {
		return location;
	}

	public void setLocation(GeoPt location) {
		setType(TAG_TYPE_LOCATION);
		this.location = location;
	}
	
	
}
