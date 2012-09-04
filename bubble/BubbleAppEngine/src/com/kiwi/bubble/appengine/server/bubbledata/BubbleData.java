package com.kiwi.bubble.appengine.server.bubbledata;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.kiwi.bubble.appengine.server.ModelBase;

@PersistenceCapable
public class BubbleData extends ModelBase {
	@Persistent
	private Long authorId;

	@Persistent
	private String text;

	@Persistent
	private Date postTime;

	@Persistent
	private String geopoint;

	@Persistent
	private List<Long> tag;

	private int commentCount;

	public BubbleData(Long id, String text) {
		super();
		this.authorId = id;
		this.text = text;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long id) {
		this.authorId = id;
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

	public List<Long> getTag() {
		return tag;
	}

	public void setTag(List<Long> tag) {
		this.tag = tag;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

}
