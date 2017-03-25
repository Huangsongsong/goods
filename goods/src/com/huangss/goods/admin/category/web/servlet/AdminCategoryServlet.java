package com.huangss.goods.admin.category.web.servlet;


import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

import com.huangss.goods.Category.Service.CategoryService;
import com.huangss.goods.Category.domain.Category;
import com.huangss.goods.book.Service.BookService;

/**
 * 这个模块的
 * 	domain service dao 都使用前台的Category模块的
 * @author 黄松松
 *
 */
public class AdminCategoryServlet extends BaseServlet {
	CategoryService service = new CategoryService();
	
	/**
	 * 加载所有一级目录和二级目录
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//加载所有目录
		List<Category> listCategory = service.findAllCategory();
		//存到request中
		req.setAttribute("listCategory", listCategory);
		//转发到list.jsp
		return "f:/adminjsps/admin/category/list.jsp";
	}
	
	/**
	 * 添加一级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addParent(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获取表单参数封装
		Category parent = CommonUtils.toBean(req.getParameterMap(), Category.class);
		//设置cid
		parent.setCid(CommonUtils.uuid());
		//调用service方法
		service.add(parent);
		//返回到list.jsp下
		return load(req, resp);
	}
	
	/**
	 * 添加二级分类前准备，
	 * 加载所有一级分类，
	 * 并保存在request中
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addChildPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//得到一级分类的cid
		String cid = req.getParameter("pid");
		//得到所有一级分类
		List<Category> parents = service.loadParent();
		req.setAttribute("parents", parents);
		req.setAttribute("pid", cid);
		return "f:/adminjsps/admin/category/add2.jsp";
	}
	
	/**
	 * 添加二级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addChild(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获取表单参数封装
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		//设置pid
		String pid = req.getParameter("pid");
		Category parent = new Category();
		parent.setCid(pid);
		child.setParent(parent);
		//设置cid
		child.setCid(CommonUtils.uuid());
		//调用service方法
		service.add(child);
		//返回到list.jsp下
		return load(req, resp);
	}
	
	/**
	 * 删除一级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delParent(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获取参数
		String cid =req.getParameter("cid");
		/*
		 * 查找该cid下是否有二级分类
		 * 如果有，保存错误信息，转发到msg.jsp下
		 * 如果没有，删除，跳转到list.jsp
		 */
		int num = service.loadChildren(cid);
		if(num > 0){//该一级分类下有二级分类
			req.setAttribute("msg", "该分类下有二级分类，拒绝删除");
			return "f:/adminjsps/msg.jsp";
		}
		service.delCategory(cid);
		return load(req, resp);
	}
	
	/**
	 * 删除二级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delChild(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获取参数
		String cid =req.getParameter("cid");
		/*
		 * 查找该cid下是否有图书
		 * 如果有，保存错误信息，转发到msg.jsp下
		 * 如果没有，删除，跳转到list.jsp
		 */
		BookService bservice = new BookService();
		int num = bservice.findBook(cid);
		if(num > 0){//该一级分类下有二级分类
			req.setAttribute("msg", "该分类下有图书，拒绝删除");
			return "f:/adminjsps/msg.jsp";
		}
		service.delCategory(cid);
		return load(req, resp);
	}
	
	/**
	 * 修改一级分类前准备
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updateParentPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cid = req.getParameter("pid");
		Category parent = service.findParent(cid);
		req.setAttribute("parent", parent);
		
		return "f:/adminjsps/admin/category/edit.jsp";
	}
	
	/**
	 * 修改一级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updateParent(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cid = req.getParameter("cid");
		//封装表单参数
		Category parent = CommonUtils.toBean(req.getParameterMap(), Category.class);
		parent.setCid(cid);
		service.updateCategory(parent);
		
		return load(req, resp);
	}
	
	/**
	 * 修改二级分类前准备
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updateChildPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String pid = req.getParameter("pid");
		req.setAttribute("pid", pid);
		
		String cid = req.getParameter("cid");
		Category child = service.findChild(cid);
		req.setAttribute("child", child);
		
		//得到所有一级分类
		List<Category> parents = service.loadParent();
		req.setAttribute("parents", parents);
		
		return "f:/adminjsps/admin/category/edit2.jsp";
	}
	
	/**
	 * 修改二级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updateChild(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cid = req.getParameter("cid");
		//封装表单参数
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		child.setCid(cid);
		
		//获取一级分类的cid
		Category parent = new Category();
		String pid = req.getParameter("pid");
		parent.setCid(pid);
		child.setParent(parent);
		
		service.updateCategory(child);
		
		return load(req, resp);
	}
}
