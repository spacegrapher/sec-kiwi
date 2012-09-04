package com.kiwi.bubble.appengine.server.comment;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class BubbleCommentServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("id");
		
		List<BubbleComment> bubbleComment = BubbleCommentJDOWrapper
				.getBubbleByBubbleId(Long.valueOf(id));

		String ret = BubbleCommentXMLConverter
				.convertDataListToXml(bubbleComment);

		resp.setContentType("text/xml");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setCharacterEncoding("utf-8");

		resp.getWriter().write(ret);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long bubbleId = Long.valueOf(req.getParameter("bubbleid"));
		Long authorId = Long.valueOf(req.getParameter("authorid"));
		String comment = req.getParameter("comment");

		BubbleComment bubbleComment = new BubbleComment(bubbleId, authorId,
				comment);
		bubbleComment.setPostTime(new Date());

		BubbleCommentJDOWrapper.insertComment(bubbleComment);
	}

}
