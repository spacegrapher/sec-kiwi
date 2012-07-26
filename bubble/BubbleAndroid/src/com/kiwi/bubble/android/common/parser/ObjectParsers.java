package com.kiwi.bubble.android.common.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.kiwi.bubble.android.common.BubbleComment;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.BubbleTag;

public class ObjectParsers {
	public static List<BubbleComment> parseBubbleComment(String response) {
		List<BubbleComment> data = new ArrayList<BubbleComment>();
		String insideContents = ObjectParsers.regex("<comments>(.*)</comments>", response);
		
		String[] contents = insideContents.split("<comment>");
		
		for (String content : contents) {
			if (content.equals("")) {
				continue;
			}
			
			Long id = Long.valueOf(ObjectParsers.regex(".*>([^>]*)</id>.*", content));
			String email = ObjectParsers.regex(".*>([^>]*)</email>.*", content);
			String text = ObjectParsers.regex(".*>([^>]*)</text>.*", content);
						
			
			//Log.i("PARSER", "email: " + email + ", title: " + title + ", text: " + text + ", tag: " + tags.toString());
			BubbleComment bc = new BubbleComment(id, email, text);
			data.add(bc);
		}
		return data;	
	}
	
	public static List<BubbleData> parseBubbleData(String response) {
		List<BubbleData> data = new ArrayList<BubbleData>();
		String insideContents = ObjectParsers.regex("<bubbles>((?:.|\\s)*)</bubbles>", response);
		
		String[] contents = insideContents.split("<bubble>");
		//Log.i("PARSER", "insideContents: " + insideContents + ", contents: " + contents.toString());
		for (String content : contents) {
			if (content.equals("")) {
				continue;
			}
			//Log.i("PARSER", "content: " + content);
			Long id = Long.valueOf(ObjectParsers.regex(".*>([^>]*)</id>(?:.|\\s)*", content));
			String email = ObjectParsers.regex(".*>([^>]*)</email>(?:.|\\s)*", content);
			String title = ObjectParsers.regex(".*>([^>]*)</title>(?:.|\\s)*", content);
			String text = ObjectParsers.regex(".*>((?:.|\\s)*)</text>(?:.|\\s)*", content);
			
			List<Long> tags = parseBubbleTagId(content);
			Log.i("PARSER", "email: " + email + ", title: " + title + ", text: " + text + ", tag: " + tags.toString());
			
			BubbleData bd = new BubbleData(email, title, text);
			bd.setId(id);
			bd.setTag(tags);
			data.add(bd);
		}
		return data;		
	}
	
	public static List<Long> parseBubbleTagId(String response) {
		List<Long> data = new ArrayList<Long>();
		String insideContents = ObjectParsers.regex("(?:.|\\s)*<tags>(.*)</tags>.*", response);
		
		String[] tagContents = insideContents.split("<tag>");
		//Log.i("PARSER", "response: " + response + ", insideContents: " + insideContents + ", tagContents: " + tagContents.toString());
		for (String tagContent : tagContents) {
			if (tagContent.equals("")) {
				continue;
			}
			
			Long id = Long.valueOf(ObjectParsers.regex("([^>]*)</tag>.*", tagContent));
			
			data.add(id);
		}
		return data;
	}
	
	public static List<BubbleTag> parseBubbleTag(String response) {
		List<BubbleTag> data = new ArrayList<BubbleTag>();
		String insideContents = ObjectParsers.regex("<tags>(.*)</tags>", response);
		
		String[] tagContents = insideContents.split("<tag>");
		//Log.i("PARSER", "response: " + response + ", insideContents: " + insideContents + ", tagContents: " + tagContents.toString());
		for (String tagContent : tagContents) {
			if (tagContent.equals("")) {
				continue;
			}
			
			int type = Integer.parseInt(ObjectParsers.regex(".*>([^>]*)</type>.*", tagContent));
			BubbleTag bubbleTag = new BubbleTag(type);
			
			switch(type) {
			case BubbleTag.TAG_TYPE_TEXT:
				String text = ObjectParsers.regex(".*>([^>]*)</data>.*", tagContent);
				bubbleTag.setText(text);
				break;
			case BubbleTag.TAG_TYPE_USER:
				long user = Long.valueOf(ObjectParsers.regex(".*>([^>]*)</data>.*", tagContent));
				bubbleTag.setUser(user);
				break;
			case BubbleTag.TAG_TYPE_LOCATION:
				String location = ObjectParsers.regex(".*>([^>]*)</data>.*", tagContent);
				bubbleTag.setLocation(location);
				break;
			}			
			
			data.add(bubbleTag);
		}
		return data;
	}
	
	public static String regex(String ex, String input) {
		Pattern pattern = Pattern.compile(ex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(input);
		if(matcher.matches()) {
			return matcher.group(1);
		} else return "";
	}
}
