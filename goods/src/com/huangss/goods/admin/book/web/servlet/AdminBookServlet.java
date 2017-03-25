package com.huangss.goods.admin.book.web.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

import com.huangss.goods.Category.Service.CategoryService;
import com.huangss.goods.Category.domain.Category;
import com.huangss.goods.book.Service.BookService;
import com.huangss.goods.book.domain.Book;
import com.huangss.goods.pager.PageBean;

/**
 * 添加图书设计到图片的上传，
 * 表单的encType必须为multipart/form-data
 * 所有获取标签元素不能使用req.getParamaeter("")方法
 * 所有必须另写一个AdminAddBookServlet来完成表单的封装
 * 
 * @author 黄松松
 *
 */
public class AdminBookServlet extends BaseServlet {
	private BookService service = new BookService();
	private CategoryService cservice = new CategoryService();

	/**
	 * 添加图书前的准备，
	 * 加载所有一级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addBookPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//通过service获取所有一级分类了
		List<Category> parents = cservice.loadParent();
		//保存到request中，转发到add.jsp
		req.setAttribute("parents", parents);
		return "f:/adminjsps/admin/book/add.jsp";
	}
	/**
	 * 删除图书
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String bid = req.getParameter("bid");
		
		/*
		 * 删除图片
		 */
		Book book = service.load(bid);
		String savepath = this.getServletContext().getRealPath("/");//获取真实的路径
		new File(savepath, book.getImage_w()).delete();//删除文件
		new File(savepath, book.getImage_b()).delete();//删除文件
		
		service.delete(bid);//删除数据库的记录
		
		req.setAttribute("msg", "删除图书成功！");
		return "f:/adminjsps/msg.jsp";
	}
	
	/**
	 * 修改图书
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String edit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 把表单数据封装到Book对象中
		 * 2. 封装cid到Category中
		 * 3. 把Category赋给Book
		 * 4. 调用service完成工作
		 * 5. 保存成功信息，转发到msg.jsp
		 */
		Map map = req.getParameterMap();
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		book.setCategory(category);
		
		service.edit(book);
		req.setAttribute("msg", "修改图书成功！");
		return "f:/adminjsps/msg.jsp";
	}
	
	/**
	 * 得到当前页码
	 * @return
	 */
	private int getPageCode(HttpServletRequest req){
		int pc = 1;
		
		String _pc = req.getParameter("pc");
		if(_pc != null){
			try{
				pc = Integer.parseInt(_pc);
			}catch(Exception e){}
		}
		return pc;
	}
	
	/**
	 * 获取请求链接
	 * @param req
	 * @return
	 */
	private String getURL(HttpServletRequest req){
		String uri = req.getRequestURI();
		String param = req.getQueryString();
		String url =null;
		int index = param.lastIndexOf("&pc");
		if(index == -1){
			url = uri + "?" + param;
		}else{
			url = uri + "?" + param.substring(0, index);
		}
		return url;
	}
	
	/**
	 * 加载所有分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String loadCategory(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//加载所有图书分类
		List<Category> parents = cservice.findAllCategory();
		req.setAttribute("parents", parents);
		return "f:/adminjsps/admin/book/left.jsp";
	}
	
	/**
	 * 记载相应分类下所有图书
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByCategory(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获取分类id
		String cid = req.getParameter("cid");
		//获取当前页
		int pc = getPageCode(req);
		//获取URL(翻页时，查询条件不会丢失)
		String url = getURL(req);
		//通过service方法获取分页bean
		PageBean<Book> pb = service.findByCategory(cid, pc);
		//设置url
		pb.setUrl(url);
		//存到request中，转发
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}

	/**
	 * 按bid查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String bid = req.getParameter("bid");//获取链接的参数bid
		Book book = service.load(bid);//通过bid得到book对象
		req.setAttribute("book", book);//保存到req中
		
		//加载所有的一级分类
		List<Category> parents = cservice.loadParent();
		req.setAttribute("parents", parents);
		
		//加载一级分类下的所有二级分类
		String pid = book.getCategory().getParent().getCid();
		List<Category> children = cservice.findChildren(pid);
		req.setAttribute("children", children);
		
		return "f:/adminjsps/admin/book/desc.jsp";//转发到desc.jsp
	}
	
	/**
	 * 查找指定一级分类下的所有二级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxFindChildren(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String pid =req.getParameter("pid");
		List<Category> children = cservice.findChildren(pid);
		resp.getWriter().print(toJson(children));
		return null;
	}
	
	private String toJson(Category category){
		StringBuffer sb = new StringBuffer("{");
		sb.append("\"cid\"").append(":").append("\"").append(category.getCid()).append("\"");
		sb.append(",");
		sb.append("\"cname\"").append(":").append("\"").append(category.getCname()).append("\"");
		sb.append("}");
		return sb.toString();
	}
	private String toJson(List<Category> children){
		StringBuffer sb = new StringBuffer("[");
		for(int i =0; i <children.size(); i ++){
			if(i == (children.size() -1)){
				sb.append(toJson(children.get(i)));
			}else{
				sb.append(toJson(children.get(i))).append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	/**
	 * 按作者查
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByAuthor(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 得到pc：如果页面传递，使用页面的，如果没传，pc=1
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 得到url：...
		 */
		String url = getURL(req);
		/*
		 * 3. 获取查询条件，本方法就是cid，即分类的id
		 */
		String author = req.getParameter("author");
		/*
		 * 4. 使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Book> pb = service.findByAuthor(author, pc);
		/*
		 * 5. 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/**
	 * 按出版社查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByPress(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 得到pc：如果页面传递，使用页面的，如果没传，pc=1
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 得到url：...
		 */
		String url = getURL(req);
		/*
		 * 3. 获取查询条件，本方法就是cid，即分类的id
		 */
		String press = req.getParameter("press");
		/*
		 * 4. 使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Book> pb = service.findByPress(press, pc);
		/*
		 * 5. 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/**
	 * 按书名查
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByBname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 得到pc：如果页面传递，使用页面的，如果没传，pc=1
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 得到url：...
		 */
		String url = getURL(req);
		/*
		 * 3. 获取查询条件，本方法就是cid，即分类的id
		 */
		String bname = req.getParameter("bname");
		/*
		 * 4. 使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Book> pb = service.findByBname(bname, pc);
		/*
		 * 5. 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/**
	 * 多条件组合查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByCombination(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 得到pc：如果页面传递，使用页面的，如果没传，pc=1
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 得到url：...
		 */
		String url = getURL(req);
		/*
		 * 3. 获取查询条件，本方法就是cid，即分类的id
		 */
		Book criteria = CommonUtils.toBean(req.getParameterMap(), Book.class);
		/*
		 * 4. 使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Book> pb = service.findByCombination(criteria, pc);
		/*
		 * 5. 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
}
