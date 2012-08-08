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

public class UserInfoServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long id = Long.valueOf(req.getParameter("id"));
		
		List<UserInfo> userInfo = UserInfoJDOWrapper.getUserById(id);
		
		assert(userInfo.size() == 1);
		
		String ret = UserInfoXMLConverter.convertUserToXml(userInfo.get(0));
		
		resp.setContentType("text/xml");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setCharacterEncoding("utf-8");
		
		//System.out.println("[UserInfoServlet] " + ret);
		resp.getWriter().write(ret);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
	}

}
