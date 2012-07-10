package com.appirio.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharsetEncodingFilter implements Filter {

    private String encoding;

    @Override
    public void init(FilterConfig config) throws ServletException {
	encoding = config.getInitParameter("requestEncoding");
	if (encoding == null)
	    encoding = "UTF-8";
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
	    FilterChain next) throws IOException, ServletException {

	System.out.println("encoding="+encoding);
	
	if(null == request.getCharacterEncoding())
	    request.setCharacterEncoding(encoding);
	
	next.doFilter(request, response);
    }
}
