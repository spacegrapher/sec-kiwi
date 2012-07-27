package com.kiwi.bubble.appengine.server.bubbledata;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.Blob;
import com.kiwi.bubble.appengine.server.ModelBase;
import com.kiwi.bubble.appengine.server.tag.BubbleTag;

@PersistenceCapable
public class BubbleData extends ModelBase {
	@Persistent
	//private String authorEmail;
	private Long authorId;
	
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
	
	//@Persistent
	//private List<String> tag;
	
	@Persistent
	private List<Long> tag;

	public BubbleData(Long id, String title, String text) {
		super();
		this.authorId = id;
		this.title = title;
		this.text = text;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long id) {
		this.authorId = id;
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

	public List<Long> getTag() {
		return tag;
	}

	public void setTag(List<Long> tag) {
		this.tag = tag;
	}
	
	
}
