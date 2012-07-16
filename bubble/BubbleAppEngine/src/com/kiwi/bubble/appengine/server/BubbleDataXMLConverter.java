package com.kiwi.bubble.appengine.server;

import java.util.List;

public class BubbleDataXMLConverter {
	public static String convertDataToXml(BubbleData data) {
		String content = "";
		content += addTag("email", data.getAuthorEmail());
		content += addTag("title", data.getTitle());
		content += addTag("text", data.getText());
		String ret = addTag("bubble", content);
		return ret;
	}
	
	public static String convertDataListToXml(List<BubbleData> data) {
		String content = "";
		for (BubbleData bd:data) {
			content += convertDataToXml(bd);
		}
		String ret = addTag("bubbles", content);
		return ret;
	}
	
	public static String addTag(String tag, String value) {
		return ("<" + tag + ">" + value + "</" + tag + ">");
	}
}
