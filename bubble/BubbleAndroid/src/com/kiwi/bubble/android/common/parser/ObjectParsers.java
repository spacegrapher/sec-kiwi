package com.kiwi.bubble.android.common.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.kiwi.bubble.android.common.BubbleData;

public class ObjectParsers {
	public static List<BubbleData> parseBubbleData(String response) {
		List<BubbleData> data = new ArrayList<BubbleData>();
		String insideContents = ObjectParsers.regex("<bubbles>(.*)</bubbles>", response);
		
		String[] contents = insideContents.split("<bubble>");
		
		for (String content : contents) {
			if (content.equals("")) {
				continue;
			}
			
			Long id = Long.valueOf(ObjectParsers.regex(".*>([^>]*)</id>.*", content));
			String email = ObjectParsers.regex(".*>([^>]*)</email>.*", content);
			String title = ObjectParsers.regex(".*>([^>]*)</title>.*", content);
			String text = ObjectParsers.regex(".*>([^>]*)</text>.*", content);
						
			String insideTags = ObjectParsers.regex(".*<tags>(.*)</tags>.*", content);
			String[] tagContents = insideTags.split("<tag>");
			List<String> tags = new ArrayList<String>();
			for (String tagContent : tagContents) {
				if (tagContent.equals("")) {
					continue;
				}
				String tag = ObjectParsers.regex("([^>]*)</tag>", tagContent);
				tags.add(tag);
			}
			
			Log.i("PARSER", "email: " + email + ", title: " + title + ", text: " + text + ", tag: " + tags.toString());
			BubbleData bd = new BubbleData(email, title, text);
			bd.setId(id);
			bd.setTag(tags);
			data.add(bd);
		}
		return data;		
	}
	
	public static String regex(String ex, String input) {
		Pattern pattern = Pattern.compile(ex);
		Matcher matcher = pattern.matcher(input);
		if(matcher.matches()) {
		//Log.i("PARSER", matcher.group(1));
			return matcher.group(1);
		} else return "";
	}
}
