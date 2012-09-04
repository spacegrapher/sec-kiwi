package com.kiwi.bubble.appengine.server.bubbledata;

import java.util.List;

import com.kiwi.bubble.appengine.server.tag.BubbleTagXMLConverter;

public class BubbleDataXMLConverter {
	public static String convertDataToXml(BubbleData data) {
		String content = "";
		content += addTag("id", data.getId().toString());
		content += addTag("author", data.getAuthorId().toString());
		content += addTag("date", String.valueOf(data.getPostTime().getTime()));
		content += addTag("text", data.getText());
		content += addTag("comment", String.valueOf(data.getCommentCount()));
		content += BubbleTagXMLConverter.convertTagIdListToXml(data.getTag());

		String ret = addTag("bubble", content);
		return ret;
	}

	public static String convertDataListToXml(List<BubbleData> data) {
		String content = "";
		for (BubbleData bd : data) {
			content += convertDataToXml(bd);
		}
		String ret = addTag("bubbles", content);
		return ret;
	}

	public static String addTag(String tag, String value) {
		return ("<" + tag + ">" + value + "</" + tag + ">");
	}
}
