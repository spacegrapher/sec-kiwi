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
	
	public static List<BubbleComment> getCommentData(long id) {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/comment";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + id, client);
		
		List<BubbleComment> comments = ObjectParsers.parseBubbleComment(response);
		
		return comments;
	}	
}
