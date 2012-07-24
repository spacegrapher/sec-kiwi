package com.kiwi.bubble.android.common;

import java.util.Date;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;

public class BubbleData extends ModelBase {
	private String authorEmail;
	
	private String title;
	
	private String text;
	
	//private Blob photo;
	
	private Date postTime;
	
	private String geopoint;
	
	private List<Long> tag;

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

	public List<Long> getTag() {
		return tag;
	}

	public void setTag(List<Long> tag) {
		this.tag = tag;
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
