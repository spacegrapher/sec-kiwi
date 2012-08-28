package com.kiwi.bubble.appengine.server.bubbledata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kiwi.bubble.appengine.server.comment.BubbleComment;
import com.kiwi.bubble.appengine.server.comment.BubbleCommentJDOWrapper;
import com.kiwi.bubble.appengine.server.tag.BubbleTag;
import com.kiwi.bubble.appengine.server.tag.BubbleTagJDOWrapper;
import com.kiwi.bubble.appengine.server.userinfo.UserInfo;
import com.kiwi.bubble.appengine.server.userinfo.UserInfoJDOWrapper;


public class BubbleListServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("id");
				
		List<BubbleData> bubbleData = null;
		
		if(id == null) {			
			bubbleData = BubbleDataJDOWrapper.getAllBubbles();
		}
		else {
			UserInfo userInfo = UserInfoJDOWrapper.getUserById(Long.valueOf(id)).get(0);
			bubbleData = BubbleDataJDOWrapper.getFriendBubbles(Long.valueOf(id), userInfo.getFriends());
			//bubbleData = BubbleDataJDOWrapper.getBubbleByAuthorId(Long.valueOf(id));
		}
		
		for(int i=0; i<bubbleData.size(); i++) {			
			List<BubbleComment> comment = BubbleCommentJDOWrapper.getBubbleByBubbleId(Long.valueOf(bubbleData.get(i).getId()));
			bubbleData.get(i).setCommentCount(comment.size());
		}
		
		String ret = BubbleDataXMLConverter.convertDataListToXml(bubbleData);
		
		resp.setContentType("text/xml");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().write(ret);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

}
