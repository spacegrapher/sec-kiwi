package com.kiwi.bubble.appengine.server.tag;

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

public class BubbleTagServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		long id = Long.valueOf(req.getParameter("id"));
		
		List<BubbleTag> bubbleTag = BubbleTagJDOWrapper.getTagById(id);
		
		String ret = BubbleTagXMLConverter.convertTagListToXml(bubbleTag);
		
		resp.setContentType("text/xml");
		resp.setHeader("Cache-Control", "no-cache");
		
		System.out.println("[BubbleTagServlet] " + ret);
		resp.getWriter().write(ret);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
	}

}
