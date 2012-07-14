package com.kiwi.bubble.appengine.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
		resp.getWriter().println("[doGet] Hello Bubble!");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		
		List<UserInfo> userInfo = UserInfoJDOWrapper.getUserByEmailAndPassword(email, password);
		
		String msg = new String();
		if(userInfo.isEmpty()) {
			msg = "";
		} else {
			req.getSession().setAttribute("email", userInfo.get(0).getEmail());
			msg = userInfo.get(0).getEmail();
		}
		
		System.out.println("[LoginServlet] " + msg);
		resp.getWriter().print(msg);
	}

}
