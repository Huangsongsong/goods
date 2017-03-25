package com.huangss.goods.User.Filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserFilter implements Filter {
	public void destroy() {
	}
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
			throws IOException, ServletException {
		/*
		 * 读取session中的数据，如果User为空，保存错误信息，转发到msg.sjp否则放行
		 */
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		
		Object user = request.getSession().getAttribute("user");
		if(user == null){
			request.setAttribute("code", "error");
			request.setAttribute("msg", "您还没有登录...");
			request.getRequestDispatcher("/jsps/msg.jsp").forward(request, response);
		}
		chain.doFilter(request, response);
	} 

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
