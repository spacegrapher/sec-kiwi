package com.kiwi.bubble.appengine.server.bubbledata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kiwi.bubble.appengine.server.tag.BubbleTag;
import com.kiwi.bubble.appengine.server.tag.BubbleTagJDOWrapper;

@SuppressWarnings("serial")
public class BubbleCreateServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long id = Long.valueOf(req.getParameter("id"));
		String strText = req.getParameter("text");
		String strTag = req.getParameter("tag");

		BubbleData bubbleData = new BubbleData(id, strText);

		List<Long> tagList = new ArrayList<Long>();
		if (!strTag.isEmpty()) {
			String[] tags = strTag.split(",");

			for (int i = 0; i < tags.length; i++) {
				List<BubbleTag> existingTag = BubbleTagJDOWrapper
						.getTagByText(tags[i]);
				if (existingTag.isEmpty()) {
					BubbleTag tag = new BubbleTag(BubbleTag.TAG_TYPE_TEXT);
					System.out.println("Tag: " + tags[i]);
					tag.setText(tags[i]);
					BubbleTagJDOWrapper.insertTag(tag);
					tagList.add(tag.getId());
				} else {
					tagList.add(existingTag.get(0).getId());
				}
			}
			bubbleData.setTag(tagList);
		}

		bubbleData.setPostTime(new Date());
		BubbleDataJDOWrapper.insertBubble(bubbleData);

		resp.setContentType("text/plain");
		resp.getWriter().write(bubbleData.getId().toString());
	}

}
