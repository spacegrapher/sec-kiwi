package com.kiwi.bubble.android.common;

import java.util.Date;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;

import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;

public class BubbleData extends ModelBase {
	private Long authorId;
	private UserInfo authorInfo;
	
	private String text;
	
	private Bitmap photo;
	
	private Date postTime;
	
	private String geopoint;
	
	private List<Long> tag;
	private List<BubbleTag> realTag;
	
	private List<BubbleComment> comments;
	private int commentCount;
	
	private boolean isFavorite;

	public BubbleData(Long id, String text) {
		super();
		this.authorId = id;
		this.text = text;
		this.photo = null;
		this.postTime = null;
		this.geopoint = null;
		this.tag = null;
		this.realTag = null;
		this.comments = null;
		this.commentCount = 0;
		this.isFavorite = false;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long id) {
		this.authorId = id;
	}

	public UserInfo getAuthorInfo() {
		return authorInfo;
	}

	public void setAuthorInfo(UserInfo authorInfo) {
		this.authorInfo = authorInfo;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Bitmap getPhoto() {
		return photo;
	}

	public void setPhoto(Bitmap photo) {
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
	
	public List<BubbleTag> getRealTag() {
		return realTag;
	}

	public void setRealTag(List<BubbleTag> realTag) {
		this.realTag = realTag;
	}

	public List<BubbleComment> getComments() {
		return comments;
	}

	public void setComments(List<BubbleComment> comments) {
		this.comments = comments;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public static BubbleData getBubbleData(long id) {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/detail";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + id, client);
		List<BubbleData> bubbles = ObjectParsers.parseBubbleData(response);
		
		assert(bubbles.size() == 1);
		
		return bubbles.get(0);
	}
}
