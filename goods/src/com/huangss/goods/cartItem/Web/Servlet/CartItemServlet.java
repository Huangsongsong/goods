package com.huangss.goods.cartItem.Web.Servlet;



import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

import com.huangss.goods.User.domain.User;
import com.huangss.goods.book.domain.Book;
import com.huangss.goods.cartItem.Service.CartItemService;
import com.huangss.goods.cartItem.domain.CartItem;

public class CartItemServlet extends BaseServlet {
	private CartItemService service = new CartItemService();
	
	/**
	 * 加载购买的图书条目
	 * 
	 */
	public String loadCartItems(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获取参数
		String cartItemIds = req.getParameter("cartItemIds");
		String total = req.getParameter("total");
		//调用service方法，得到购买的图书条目
		List<CartItem> listItem = service.loadCartItems(cartItemIds);
		//将结果保存到request中，转发到/jsps/cart/showitem.jsp
		req.setAttribute("listItem", listItem);
		req.setAttribute("cartItemIds", cartItemIds);
		req.setAttribute("total", total);
		return "f:/jsps/cart/showitem.jsp";
	}
	
	/**
	 * 根据cartItemid修改图书数量
	 */
	public String updateQuantity(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获取参数
		int quantity = Integer.parseInt(req.getParameter("quantity"));
		String cartItemId = req.getParameter("cartItemId");
		//调用service方法，修改数量,得到修改后的图书条目情况
		CartItem cartItem = service.updateQuantity(cartItemId, quantity);
		//作出相应，并将响应转换成json格式
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"quantity\"").append(":").append(cartItem.getQuantity());
		sb.append(",");
		sb.append("\"subtotal\"").append(":").append(cartItem.getSubTotal());
		sb.append("}");
		resp.getWriter().print(sb);
		return null;
	}
	/**
	 * 按cartItemId删除购物条目
	 *
	 */
	public String delBookItems(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//得到参数
		String cartItemId = req.getParameter("cartItemIds");
		//调用service方法完成删除
		service.delBookItems(cartItemId);
		return findByUser(req,resp);
	}
	/**
	 * 查询当前用户的购物车条目
	 */
	public String findByUser(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//从session中获取当前用户的信息
		User user = (User)req.getSession().getAttribute("user");
		//调用service方法，得到List<CartItem>，存到request中
		List<CartItem> listItem = service.findByUser(user);
		req.setAttribute("listItem", listItem);
		//转发到/jsps/cart/list.jsp下
		return "f:/jsps/cart/list.jsp";
	}
	/**
	 * 向购物车添加购物条条目
	 */
	public String addBookItem(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//封装表单参数,quantity和bid
		Map<String, String[]> map = req.getParameterMap();
		//将quantity封装到CartItem中
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		//将bid封装到Book中
		Book book = CommonUtils.toBean(map, Book.class);
		//从session中获取当前用户的信息
		User user = (User)req.getSession().getAttribute("user");
		//关联cartItem
		cartItem.setBook(book);
		cartItem.setUser(user);
		//调用service方法，完成添加
		service.addBookItem(cartItem);
		//转发到我的购物车页面显示
		return findByUser(req,resp);
	}
}

