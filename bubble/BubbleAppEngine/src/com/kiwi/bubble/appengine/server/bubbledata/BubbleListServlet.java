package com.kiwi.bubble.appengine.server.bubbledata;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BubbleListServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String email = req.getParameter("email");
		System.out.println("email: " + email);
		
		List<BubbleData> bubbleData = null;
		
		if(email == null) {
			bubbleData = BubbleDataJDOWrapper.getAllBubbles();
		}
		else
			bubbleData = BubbleDataJDOWrapper.getBubbleByEmail(email);
		
		String ret = BubbleDataXMLConverter.convertDataListToXml(bubbleData);
		
		resp.setContentType("text/xml");
		resp.setHeader("Cache-Control", "no-cache");
		
		System.out.println("[BubbleListServlet] " + ret);
		resp.getWriter().write(ret);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

}
