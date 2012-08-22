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

public class TagSearchServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String str = req.getParameter("tag");
		List<BubbleTag> bubbleTag = null;
		
		String[] tags = null;
		List<Long> tagIdList = new ArrayList<Long>();
		
		if (str == null) {
			bubbleTag = BubbleTagJDOWrapper.getAllTags();
			tagIdList.add(bubbleTag.get(0).getId());
		} else {
			tags = str.split(",");
			for(int i=0; i<tags.length; i++) {
				bubbleTag = BubbleTagJDOWrapper.getTagByText(tags[i]);
				tagIdList.add(bubbleTag.get(0).getId());
			}			
		}
		
		//String ret = BubbleTagXMLConverter.convertTagListToXml(bubbleTag);
		
		//List<BubbleData> bubbleData = BubbleDataJDOWrapper.getBubbleByTag(bubbleTag.get(0).getId());
		List<BubbleData> bubbleData = BubbleDataJDOWrapper.getBubbleByTags(tagIdList);
		String ret = BubbleDataXMLConverter.convertDataListToXml(bubbleData);
		
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
