package com.kiwi.bubble.appengine.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.User;

@SuppressWarnings("serial")
public class BubbleServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user != null) {
			UserInfoBean userInfo = new UserInfoBean(user.getNickname(), user.getEmail());
			req.setAttribute("user_info", userInfo);
		}
		
		SettingsBean settings = new SettingsBean();
		req.setAttribute("settings", settings);
		
		String url = "/index.jsp";
		try {
			req.getRequestDispatcher(url).forward(req, resp);
		} catch (ServletException e) {
			e.printStackTrace();
		}
		return;
	}

}
