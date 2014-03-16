package com.listerly;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Hello")
public class HelloWorldServlet extends HttpServlet {
    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
		throws ServletException, IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello AppEngine!");
	}
}
