package com.kiwi.bubble.android.common;

import java.util.Date;


public class BubbleComment extends ModelBase {
	private Long bubbleId;
	
	private String email;
	
	private String text;
	
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
