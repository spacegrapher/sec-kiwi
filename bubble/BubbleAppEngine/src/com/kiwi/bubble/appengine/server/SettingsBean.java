package com.kiwi.bubble.appengine.server;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class SettingsBean {
	private String cssPath;
	private String lang;
	private String logoutPath;
	
	public SettingsBean() {
		setCssPath(System.getProperty("css_path"));
		setLang("ko");
		UserService userService = UserServiceFactory.getUserService();
		setLogoutPath(userService.createLogoutURL("/"));
	}

	public String getCssPath() {
		return cssPath;
	}

	public void setCssPath(String cssPath) {
		this.cssPath = cssPath;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getLogoutPath() {
		return logoutPath;
	}

	public void setLogoutPath(String logoutPath) {
		this.logoutPath = logoutPath;
	}
	
	
}
