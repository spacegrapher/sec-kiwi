package com.kiwi.bubble.appengine.server.userinfo;

public class UserInfoXMLConverter {
	public static String convertUserToXml(UserInfo info) {
		String content = "";
		content += addTag("email", info.getEmail());
		content += addTag("name", info.getName());
		String ret = addTag("user", content);
		return ret;
	}

	public static String addTag(String tag, String value) {
		return ("<" + tag + ">" + value + "</" + tag + ">");
	}
}
