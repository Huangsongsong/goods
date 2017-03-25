package com.huangss.goods.Category.Web.Servlet;



import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;

import com.huangss.goods.Category.Service.CategoryService;
import com.huangss.goods.Category.domain.Category;
public class CategoryServlet extends BaseServlet {
	private CategoryService service = new CategoryService();
	
	//查找所有的菜单分类
	public String findAllCategory(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/**
		 * 1.调用service方法，得到分类
		 * 2.存入request域中，转发到left.jsp中
		 */
		List<Category> parents = service.findAllCategory();
		req.setAttribute("parents", parents);
		return "f:/jsps/left.jsp";
	}
}
