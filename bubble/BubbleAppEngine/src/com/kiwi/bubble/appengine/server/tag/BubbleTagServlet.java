package com.kiwi.bubble.appengine.server.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BubbleTagServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String idStr = req.getParameter("id");
				
		List<BubbleTag> bubbleTag = new ArrayList<BubbleTag>();
		
		if (idStr == null) {
			bubbleTag = BubbleTagJDOWrapper.getAllTags();
		} else {
			String[] idArray = idStr.split(",");
			for(int i=0; i<idArray.length; i++) {
				long id = Long.valueOf(idArray[i]);		
				BubbleTag currentTag = BubbleTagJDOWrapper.getTagById(id).get(0);
				bubbleTag.add(currentTag);
			}
		}
		
		String ret = BubbleTagXMLConverter.convertTagListToXml(bubbleTag);
		
		resp.setContentType("text/xml");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setCharacterEncoding("utf-8");
		
		resp.getWriter().write(ret);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
	}

}
