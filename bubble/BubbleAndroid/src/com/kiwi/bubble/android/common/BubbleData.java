package com.kiwi.bubble.android.common;

import java.util.Date;

public class BubbleData {
	private String authorEmail;
	
	private String title;
	
	private String text;
	
	//private Blob photo;
	
	private Date postTime;
	
	private String geopoint;

	public BubbleData(String authorEmail, String title, String text) {
		super();
		this.authorEmail = authorEmail;
		this.title = title;
		this.text = text;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public String getGeopoint() {
		return geopoint;
	}

	public void setGeopoint(String geopoint) {
		this.geopoint = geopoint;
	}
}
