package com.kiwi.bubble.appengine.server.bubbledata;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kiwi.bubble.appengine.server.userinfo.UserInfo;
import com.kiwi.bubble.appengine.server.userinfo.UserInfoJDOWrapper;

@SuppressWarnings("serial")
public class FavoriteServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long id = Long.valueOf(req.getParameter("id"));
		Long bubbleId = Long.valueOf(req.getParameter("bubbleid"));

		UserInfo userInfo = UserInfoJDOWrapper.getUserById(id).get(0);
		boolean isFavorite = userInfo.isFavorite(bubbleId);
		byte[] retValue = isFavorite ? "OK".getBytes() : "NO".getBytes();

		resp.setContentType("text/plain");
		resp.getOutputStream().write(retValue);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long id = Long.valueOf(req.getParameter("id"));
		Long bubbleId = Long.valueOf(req.getParameter("bubbleid"));

		UserInfoJDOWrapper.addFavorite(id, bubbleId);

		resp.setContentType("text/plain");
		resp.getOutputStream().write("OK".getBytes());
	}

}
