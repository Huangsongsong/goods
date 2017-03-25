package com.
huangss.goods.Order.Service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.jdbc.JdbcUtils;

import com.huangss.goods.Order.Dao.OrderDao;
import com.huangss.goods.Order.domain.Order;
import com.huangss.goods.pager.PageBean;

public class OrderService {
	private OrderDao dao = new OrderDao();

	/**
	 * 查询当前用户的订单
	 * @param uid
	 * @param pc
	 * @return
	 */
	public PageBean<Order> findByUid(String uid, int pc) {
		try{
			JdbcUtils.beginTransaction();
			PageBean<Order> pb = dao.findByUid(uid ,pc);
			JdbcUtils.commitTransaction();
			return pb;
		}catch(Exception e){
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
				throw new RuntimeException(e);
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加订单
	 * @param order
	 */
	public void add(Order order) {
		try {
			JdbcUtils.beginTransaction();
			dao.add(order);
			JdbcUtils.commitTransaction();
		} catch (Exception e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * 加载订单
	 * @param oid
	 * @return
	 */
	public Order load(String oid) {
		try {
			return dao.load(oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 更改订单状态
	 * @param oid
	 * @param status
	 */
	public void updateStatus(String oid, int status) {
		try {
			dao.updateStatus(oid, status);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查询所有订单
	 * @param pc
	 * @return
	 */
	public PageBean<Order> findAll(int pc) {
		try {
			return dao.findAll(pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 按状态查找订单
	 * @param status
	 * @param pc
	 * @return
	 */
	public PageBean<Order> findByStatus(String status, int pc) {
		try {
			return dao.findByStatus(status, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}