/**
 * 
 */
package com.kiwi.bubble.appengine.server.bubbledata;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;

@SuppressWarnings("serial")
public class BubbleDataImageServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long bubbleId = Long.valueOf(req.getParameter("bubbleid"));
		String strImage = req.getParameter("image");

		Blob imageBlob = new Blob(strImage.getBytes());
		BubbleDataImage imageObject = new BubbleDataImage(bubbleId, imageBlob);

		// persist image
		BubbleDataImageJDOWrapper.insertImage(imageObject);

		// respond to query
		resp.setContentType("text/plain");
		resp.getOutputStream().write("OK!".getBytes());
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Long bubbleId = Long.valueOf(req.getParameter("bubbleid"));

		List<BubbleDataImage> imageObject = BubbleDataImageJDOWrapper
				.getImageByBubbleId(bubbleId);

		resp.setContentType("text/plain");

		if (imageObject.size() > 0)
			resp.getOutputStream().write(
					imageObject.get(0).getContent().getBytes());
		else
			resp.getOutputStream().write("".getBytes());
	}
}
