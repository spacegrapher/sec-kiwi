package com.kiwi.bubble.appengine.server.comment;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.kiwi.bubble.appengine.server.ModelBase;

@PersistenceCapable
public class BubbleComment extends ModelBase {
	@Persistent
	private Long bubbleId;
	
	@Persistent
	private String email;
	
	@Persistent
	private String text;
	
	@Persistent
	private Date postTime;
	
	

	public BubbleComment(Long bubbleId, String email, String text) {
		super();
		this.bubbleId = bubbleId;
		this.email = email;
		this.text = text;
	}

	public Long getBubbleId() {
		return bubbleId;
	}

	public void setBubbleId(Long bubbleId) {
		this.bubbleId = bubbleId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
	
	
	
}
