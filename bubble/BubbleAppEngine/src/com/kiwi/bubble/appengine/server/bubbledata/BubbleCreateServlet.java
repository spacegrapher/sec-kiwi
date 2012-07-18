package com.kiwi.bubble.appengine.server.bubbledata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BubbleCreateServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String email = req.getParameter("email");
		String title = req.getParameter("title");
		String text = req.getParameter("text");
		String tag = req.getParameter("tag");
		
		BubbleData bubbleData = new BubbleData(email, title, text);
		
		if(!tag.isEmpty()) {
			String[] tags = tag.split(",");
			List<String> tagList = new ArrayList<String>();
			for(int i=0; i<tags.length; i++) {
				tagList.add(tags[i]);
			}
			bubbleData.setTag(tagList);
		}
		
		BubbleDataJDOWrapper.insertBubble(bubbleData);
	}

}
