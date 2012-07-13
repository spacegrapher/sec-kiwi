package com.kiwi.bubble.appengine.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SignupServlet extends HttpServlet {

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
		String name = req.getParameter("name");
		String password = req.getParameter("password");
		
		UserInfo userInfo = new UserInfo(email, name, password);
		UserInfoJDOWrapper.insertUser(userInfo);
		
		String msg = "Your email: " + userInfo.getEmail() + "\nYour name: " + userInfo.getName();
		System.out.println("[SignupServlet] " + msg);
		resp.getWriter().print(msg);
	}

}
