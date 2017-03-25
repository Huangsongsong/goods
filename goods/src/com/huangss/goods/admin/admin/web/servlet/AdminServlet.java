package com.huangss.goods.admin.admin.web.servlet;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

import com.huangss.goods.admin.admin.domain.Admin;
import com.huangss.goods.admin.admin.servlet.AdminService;

public class AdminServlet extends BaseServlet {
	private AdminService service = new AdminService();
	
	/**
	 * 管理员登录
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String login(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 获取表单数据，封装
		 */
		Admin form = CommonUtils.toBean(req.getParameterMap(), Admin.class);
		//调用service完成登录
		Admin admin= service.login(form);
		if(admin == null){
			req.setAttribute("msg", "用户名或密码错误...");
			return "f:/adminjsps/msg.jsp";
		}
		req.getSession().setAttribute("admin", admin);
		return "r:/adminjsps/admin/index.jsp";
	}
	
	/**
	 * 退出登录
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String exit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getSession().invalidate();
		return "r:/adminjsps/login.jsp";
	}
}
