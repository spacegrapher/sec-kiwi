/**
 * 
 */
package com.kiwi.bubble.appengine.server.userinfo;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;

@SuppressWarnings("serial")
public class UserInfoImageServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long id = Long.valueOf(req.getParameter("id"));
		String strImage = req.getParameter("image");

		Blob imageBlob = new Blob(strImage.getBytes());
		UserInfoImage imageObject = new UserInfoImage(id, imageBlob);

		// persist image
		if (UserInfoImageJDOWrapper.getImageByUserId(id).size() == 0)
			UserInfoImageJDOWrapper.insertImage(imageObject);
		else
			UserInfoImageJDOWrapper.updateImage(id, imageBlob);

		// respond to query
		resp.setContentType("text/plain");
		resp.getOutputStream().write("OK!".getBytes());
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Long id = Long.valueOf(req.getParameter("id"));

		List<UserInfoImage> imageObject = UserInfoImageJDOWrapper
				.getImageByUserId(id);

		resp.setContentType("text/plain");

		if (imageObject.size() > 0)
			resp.getOutputStream().write(
					imageObject.get(0).getContent().getBytes());
		else
			resp.getOutputStream().write("".getBytes());
	}
}
