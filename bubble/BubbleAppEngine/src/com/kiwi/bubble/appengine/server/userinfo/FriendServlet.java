package com.kiwi.bubble.appengine.server.userinfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kiwi.bubble.appengine.server.bubbledata.BubbleData;
import com.kiwi.bubble.appengine.server.bubbledata.BubbleDataJDOWrapper;
import com.kiwi.bubble.appengine.server.bubbledata.BubbleDataXMLConverter;

public class FriendServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long id = Long.valueOf(req.getParameter("id"));
		Long friendId = Long.valueOf(req.getParameter("friendid"));
		
		UserInfo userInfo = UserInfoJDOWrapper.getUserById(id).get(0);		
		boolean isFriend = userInfo.isFriend(friendId);
		byte[] retValue = isFriend?"OK".getBytes():"NO".getBytes();
		
		resp.setContentType("text/plain");		
		resp.getOutputStream().write(retValue);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long id = Long.valueOf(req.getParameter("id"));
		Long friendId = Long.valueOf(req.getParameter("friendid"));
		
		UserInfoJDOWrapper.addUserFriend(id, friendId);
		
		resp.setContentType("text/plain");
        resp.getOutputStream().write("OK".getBytes());
	}

}
