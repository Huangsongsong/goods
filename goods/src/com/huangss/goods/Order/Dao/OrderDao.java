package com.huangss.goods.Order.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

import com.huangss.goods.Order.domain.Order;
import com.huangss.goods.Order.domain.OrderItem;
import com.huangss.goods.book.domain.Book;
import com.huangss.goods.pager.Expression;
import com.huangss.goods.pager.PageBean;
import com.huangss.goods.pager.PageConstants;

public class OrderDao {
	private QueryRunner runner = new TxQueryRunner();

	
	/**
	 * 通用的查询方法
	 * @param exprList
	 * @param pc
	 * @return
	 * @throws SQLException 
	 */
	private PageBean<Order> findByCriteria(List<Expression> exprList, int pc) throws SQLException {
		/*
		 * 1. 得到ps
		 * 2. 得到tr
		 * 3. 得到beanList
		 * 4. 创建PageBean，返回
		 */
		/*
		 * 1. 得到ps
		 */
		int ps = PageConstants.ORDER_PAGE_SIZE;//每页记录数
		/*
		 * 2. 通过exprList来生成where子句
		 */
		StringBuilder whereSql = new StringBuilder(" where 1=1"); 
		List<Object> params = new ArrayList<Object>();//SQL中有问号，它是对应问号的值
		for(Expression expr : exprList) {
			/*
			 * 添加一个条件上，
			 * 1) 以and开头
			 * 2) 条件的名称
			 * 3) 条件的运算符，可以是=、!=、>、< ... is null，is null没有值
			 * 4) 如果条件不是is null，再追加问号，然后再向params中添加一与问号对应的值
			 */
			whereSql.append(" and ").append(expr.getName())
				.append(" ").append(expr.getOperator()).append(" ");
			// where 1=1 and bid = ?
			if(!expr.getOperator().equals("is null")) {
				whereSql.append("?");
				params.add(expr.getValue());
			}
		}

		/*
		 * 3. 总记录数 
		 */
		String sql = "select count(*) from t_order" + whereSql;
		Number number = (Number)runner.query(sql, new ScalarHandler(), params.toArray());
		int tr = number.intValue();//得到了总记录数
		/*
		 * 4. 得到beanList，即当前页记录
		 */
		sql = "select * from t_order" + whereSql + " limit ?,?";
		params.add((pc-1) * ps);//当前页首行记录的下标
		params.add(ps);//一共查询几行，就是每页记录数
		
		List<Order> beanList = runner.query(sql, new BeanListHandler<Order>(Order.class), 
				params.toArray());
		  
		//遍历每一个order，得到其订单条目List<OrderItem>
		for(Order order : beanList){
			List<OrderItem> orderItem = getOrderItem(order);
			order.setOrderItem(orderItem);
		}
		
		/*
		 * 5. 创建PageBean，设置参数
		 */
		PageBean<Order> pb = new PageBean<Order>();
		/*
		 * 其中PageBean没有url，这个任务由Servlet完成
		 */
		pb.setBeanList(beanList);
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		
		return pb;
	}
	
	/**
	 * 查询当前用户的订单
	 * @param uid
	 * @param pc
	 * @return
	 * @throws SQLException 
	 */
	public PageBean<Order> findByUid(String uid, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("uid", "=", uid));
		return findByCriteria(exprList, pc);
	}

	/**
	 * 获取订单的详细订单条目
	 * @param order
	 * @return
	 * @throws SQLException
	 */
	private List<OrderItem> getOrderItem(Order order) throws SQLException {
		String sql = "select * from t_orderitem where oid =?";
		List<Map<String, Object>> listMap = runner.query(sql, 
				new MapListHandler(),order.getOid());
		return toBeanList(listMap);
	}

	/**
	 * 将多个map转化为list
	 * @param listMap
	 * @return
	 */
	private List<OrderItem> toBeanList(List<Map<String, Object>> listMap) {
		List<OrderItem> beanList = new ArrayList<OrderItem>();
		for(Map<String, Object> map : listMap){
			OrderItem orderItem = toBean(map);
			beanList.add(orderItem);
		}
		return beanList;
	}

	/**
	 * 将一个Map转化为Bean
	 * @param map
	 * @return
	 */
	private OrderItem toBean(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		Order order = CommonUtils.toBean(map, Order.class);
		orderItem.setBook(book);
		orderItem.setOrder(order);
		return orderItem;
	}

	/**
	 * 添加订单
	 * @param order
	 * @throws SQLException 
	 */
	public void add(Order order) throws SQLException {
		//完成订单的添加
		String sql = "insert into t_order values(?,?,?,?,?,?)";
		Object[] obj = {order.getOid(), order.getOrdertime(), order.getTotal(),
							order.getStatus(), order.getAddress(), order.getUser().getUid()};
		runner.update(sql,obj);
		//完成订单条目的添加
		List<OrderItem> orderListItem = order.getOrderItem();
		sql = "insert into t_orderitem values(?,?,?,?,?,?,?,?)";
		/*
		 *使用batct()完成批量添加，其参数为二维数组 
		 */
		int len = orderListItem.size();
		Object[][] objs = new Object[len][];
		//给二位数组赋值
		for(int i =0; i <len; i++){
			objs[i] = new Object[]{
					orderListItem.get(i).getOrderItemId(), 
						orderListItem.get(i).getQuantity(),
							orderListItem.get(i).getSubtotal(), 
								orderListItem.get(i).getBook().getBid(), 
									orderListItem.get(i).getBook().getBname(),
										orderListItem.get(i).getBook().getCurrPrice(),
											orderListItem.get(i).getBook().getImage_b(),
													orderListItem.get(i).getOrder().getOid()};
			}
		runner.batch(sql, objs);
		}

	/**
	 * 加载订单
	 * @param oid
	 * @return
	 * @throws SQLException 
	 */
	public Order load(String oid) throws SQLException {
		String sql = "select * from t_order where oid = ?";
		Order order = runner.query(sql, new BeanHandler<Order>(Order.class), oid);
		List<OrderItem> orderItem = getOrderItem(order);
		order.setOrderItem(orderItem);
		return order;
	}

	/**
	 * 修改订单状态
	 * @param oid
	 * @param status
	 * @throws SQLException 
	 */
	public void updateStatus(String oid, int status) throws SQLException {
		String sql = "update t_order set status = ? where oid = ?";
		runner.update(sql, status, oid);
	}

	/**
	 * 加载所有订单
	 * @param pc
	 * @return
	 * @throws SQLException 
	 */
	public PageBean<Order> findAll(int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		return findByCriteria(exprList, pc);
	}

	/**
	 * 按状态查找订单
	 * @param status
	 * @param pc
	 * @return
	 * @throws SQLException 
	 */
	public PageBean<Order> findByStatus(String status, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("status", "=", status));
		return findByCriteria(exprList, pc);
	}
}
