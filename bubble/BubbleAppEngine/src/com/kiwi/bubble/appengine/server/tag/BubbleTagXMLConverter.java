package com.kiwi.bubble.appengine.server.tag;

import java.util.List;


public class BubbleTagXMLConverter {
	public static String convertTagIdToXml(Long data) {
		String content = "";		
		
		content += addTag("tag", Long.toString(data));
		
		return content;
	}
	
	public static String convertTagIdListToXml(List<Long> data) {
		String content = "";
		for (Long tag:data) {
			content += convertTagIdToXml(tag);
		}
		String ret = addTag("tags", content);
		return ret;
	}
	
	public static String convertTagToXml(BubbleTag data) {
		String content = "";		
		
		switch (data.getType()) {
		case BubbleTag.TAG_TYPE_TEXT:
			content += addTag("type", "0");
			content += addTag("data", data.getText());
			break;
		case BubbleTag.TAG_TYPE_USER:
			content += addTag("type", "1");
			content += addTag("data", Long.toString(data.getUser()));
			break;
		case BubbleTag.TAG_TYPE_LOCATION:
			content += addTag("type", "2");
			content += addTag("data", data.getLocation().toString());
			break;
		}
		
		String ret = addTag("tag", content);
		return ret;
	}
	
	public static String convertTagListToXml(List<BubbleTag> data) {
		String content = "";
		for (BubbleTag tag:data) {
			content += convertTagToXml(tag);
		}
		String ret = addTag("tags", content);
		return ret;
	}
	
	public static String addTag(String tag, String value) {
		return ("<" + tag + ">" + value + "</" + tag + ">");
	}
}
