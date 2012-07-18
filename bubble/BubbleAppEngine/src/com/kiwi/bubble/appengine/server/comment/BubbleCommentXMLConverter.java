package com.kiwi.bubble.appengine.server.comment;

import java.util.List;

public class BubbleCommentXMLConverter {
	public static String convertDataToXml(BubbleComment data) {
		String content = "";
		content += addTag("id", data.getId().toString());
		content += addTag("email", data.getEmail());
		content += addTag("text", data.getText());
		String ret = addTag("comment", content);
		return ret;
	}
	
	public static String convertDataListToXml(List<BubbleComment> data) {
		String content = "";
		for (BubbleComment bd:data) {
			content += convertDataToXml(bd);
		}
		String ret = addTag("comments", content);
		return ret;
	}
	
	public static String addTag(String tag, String value) {
		return ("<" + tag + ">" + value + "</" + tag + ">");
	}
}