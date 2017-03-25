package com.huangss.goods.admin.order.web.servlet;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;

import com.huangss.goods.Order.Service.OrderService;
import com.huangss.goods.Order.domain.Order;
import com.huangss.goods.User.domain.User;
import com.huangss.goods.pager.PageBean;

public class AdminOrderServlet extends BaseServlet {
	private OrderService service = new OrderService();
	
	
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
	 * 加载所有订单
	 * @param req
	 * @param resp
	 * @return
	 */
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//得到当前页码
		int pc = getPageCode(req);
		//得到请求的url
		String url = getURL(req);
		//根据当前用户的id查询订单
		PageBean<Order> pb = service.findAll(pc);
		//设置到PageBean中
		pb.setPc(pc);
		pb.setUrl(url);
		//将pb保存到request中
		req.setAttribute("pb", pb);
		//跳转到、order/list.jsp
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/**
	 * 加载订单详细信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		String btn = req.getParameter("btn");
		
		Order order = service.load(oid);
		req.setAttribute("Order", order);
		req.setAttribute("btn", btn);
		return "f:/adminjsps/admin/order/desc.jsp";
	}
	
	/**
	 * 按状态查询订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByStatus(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String status = req.getParameter("status");
		//得到当前页码
		int pc = getPageCode(req);
		//得到请求的url
		String url = getURL(req);
		//根据当前用户的id查询订单
		PageBean<Order> pb = service.findByStatus(status, pc);
		//设置到PageBean中
		pb.setPc(pc);
		pb.setUrl(url);
		//将pb保存到request中
		req.setAttribute("pb", pb);
		//跳转到、order/list.jsp
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/**
	 * 发货
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deliver(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {	
		String oid = req.getParameter("oid");
		
		//发货
		//验证该订单是否付款
		Order order = service.load(oid);
		if(order.getStatus() != 2){
			req.setAttribute("msg", "该订单状态不对");
			return "f:/adminjsps/msg.jsp";
		}
		service.updateStatus(oid, 3);
		req.setAttribute("msg", "发货成功");
		return "f:/adminjsps/msg.jsp";
	}
	
	/**
	 * 取消订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String cancel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {	
		String oid = req.getParameter("oid");
		
		//发货
		//验证该订单是否付款
		Order order = service.load(oid);
		if(order.getStatus() != 1){
			req.setAttribute("msg", "该订单状态不对");
			return "f:/adminjsps/msg.jsp";
		}
		service.updateStatus(oid, 5);
		req.setAttribute("msg", "取消成功");
		return "f:/adminjsps/msg.jsp";
	}
}
