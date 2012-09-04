package com.kiwi.bubble.appengine.server.userinfo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Bubble");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String email = req.getParameter("email");
		String password = req.getParameter("password");

		// List<UserInfo> userInfo =
		// UserInfoJDOWrapper.getUserByEmailAndPassword(email, password);
		Long userId = UserInfoJDOWrapper.getUserIdByEmailAndPassword(email,
				password);

		String msg = new String();
		if (userId < 0) {
			msg = "";
		} else {
			req.getSession().setAttribute("id", userId);
			msg = "" + userId;
		}

		// System.out.println("[LoginServlet] " + msg);
		resp.getWriter().print(msg);
	}

}
