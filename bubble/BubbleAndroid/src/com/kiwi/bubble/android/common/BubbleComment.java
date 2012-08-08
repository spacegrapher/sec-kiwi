package com.kiwi.bubble.android.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;


public class BubbleComment extends ModelBase {
	private Long bubbleId;
	
	private Long authorId;
	private UserInfo authorInfo;
	
	private String text;
	
	private Date postTime;
	
	

	public BubbleComment(Long bubbleId, Long authorId, String text) {
		super();
		this.bubbleId = bubbleId;
		this.authorId = authorId;
		this.text = text;
	}

	public Long getBubbleId() {
		return bubbleId;
	}

	public void setBubbleId(Long bubbleId) {
		this.bubbleId = bubbleId;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
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

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}
	
	public static List<BubbleComment> getCommentData(long id) {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/comment";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + id, client);
		
		List<BubbleComment> comments = ObjectParsers.parseBubbleComment(response);
		
		return comments;
	}	
}
