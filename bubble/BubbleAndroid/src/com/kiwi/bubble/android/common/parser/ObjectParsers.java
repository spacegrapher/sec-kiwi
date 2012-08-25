package com.kiwi.bubble.android.common.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwi.bubble.android.common.BubbleComment;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.BubbleTag;
import com.kiwi.bubble.android.common.UserInfo;

public class ObjectParsers {
	public static UserInfo parseUserInfo(String response) {
		String content = ObjectParsers.regex("<user>(.*)</user>", response);
		
		String email = null;
		String name = null;
		Pattern pattern = Pattern.compile("<email>([^>]*)</email><name>([^>]*)</name>");
		Matcher matcher = pattern.matcher(content);
		if(matcher.matches()) {
			email = matcher.group(1);
			name = matcher.group(2);
		}
		
		UserInfo user = new UserInfo(email, name);
		
		return user;
	}
	
	public static List<BubbleComment> parseBubbleComment(String response) {
		List<BubbleComment> data = new ArrayList<BubbleComment>();
		String insideContents = ObjectParsers.regex("<comments>(.*)</comments>", response);
		
		String[] contents = insideContents.split("<comment>");
		
		for (String content : contents) {
			if (content.equals("")) {
				continue;
			}
			
			Long id = null;
			Long authorId = null;
			Long date = null;
			String text = null;
			
			Pattern pattern = Pattern.compile("<id>([^>]*)</id><author>([^>]*)</author><date>([^>]*)</date><text>((?:.|\\s)*)</text>.*", Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(content);
			if(matcher.matches()) {
				id = Long.valueOf(matcher.group(1));
				authorId = Long.valueOf(matcher.group(2));
				date = Long.valueOf(matcher.group(3));
				text = matcher.group(4);
			}
						
			Date dateData = new Date(date);
			
			BubbleComment bc = new BubbleComment(id, authorId, text);
			bc.setPostTime(dateData);
			data.add(bc);
		}
		return data;	
	}
	
	public static List<BubbleData> parseBubbleData(String response) {
		List<BubbleData> data = new ArrayList<BubbleData>();
		String insideContents = ObjectParsers.regex("<bubbles>((?:.|\\s)*)</bubbles>", response);
		
		String[] contents = insideContents.split("<bubble>");
		for (String content : contents) {
			if (content.equals("")) {
				continue;
			}
			
			Long id = null;
			Long authorId = null;
			Long date = null;
			String text = null;
			int commentCount = 0;
			Pattern pattern = Pattern.compile("<id>([^>]*)</id><author>([^>]*)</author><date>([^>]*)</date><text>((?:.|\\s)*)</text><comment>([^>]*)</comment>.*", Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(content);
			if(matcher.matches()) {
				id = Long.valueOf(matcher.group(1));
				authorId = Long.valueOf(matcher.group(2));
				date = Long.valueOf(matcher.group(3));
				text = matcher.group(4);
				commentCount = Integer.parseInt(matcher.group(5));
			}
			
			List<Long> tags = parseBubbleTagId(content);
						
			BubbleData bd = new BubbleData(authorId, text);
			bd.setId(id);
			Date dateData = new Date(date);
			
			bd.setPostTime(dateData);
			bd.setTag(tags);
			bd.setCommentCount(commentCount);
			data.add(bd);
		}
		return data;		
	}
	
	public static List<Long> parseBubbleTagId(String response) {
		List<Long> data = new ArrayList<Long>();
		String insideContents = ObjectParsers.regex("(?:.|\\s)*<tags>(.*)</tags>.*", response);
		
		String[] tagContents = insideContents.split("<tag>");
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
