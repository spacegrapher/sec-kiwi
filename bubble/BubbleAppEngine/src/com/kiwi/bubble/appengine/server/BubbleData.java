package com.kiwi.bubble.appengine.server;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.Blob;

@PersistenceCapable
public class BubbleData extends ModelBase {
	@Persistent
	private String authorEmail;
	
	@Persistent
	private String title;
	
	@Persistent
	private String text;
	
	@Persistent
	private Blob photo;
	
	@Persistent
	private Date postTime;
	
	@Persistent
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

	public Blob getPhoto() {
		return photo;
	}

	public void setPhoto(Blob photo) {
		this.photo = photo;
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
