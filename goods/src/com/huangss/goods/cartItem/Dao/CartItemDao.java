package com.huangss.goods.cartItem.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.Test;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

import com.huangss.goods.User.domain.User;
import com.huangss.goods.book.domain.Book;
import com.huangss.goods.cartItem.domain.CartItem;

public class CartItemDao {
	private QueryRunner runner = new TxQueryRunner();
	
	/**
	 * 将Map封装成Bean
	 */
	private CartItem toBean(Map<String,Object> map){
		/**
		 * 缺少这步，返回的数据不会为空，book并不为空
		 */
		if(map == null || map.size() == 0){
			return null;
		}
		//将CartItemId 和 quantity封装到CartItem
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		//将Uid封装到User
		User user = CommonUtils.toBean(map, User.class);
		//将图书相关信息封装到Book中
		Book book = CommonUtils.toBean(map, Book.class);
		//将book和user与cartItem关联
		cartItem.setBook(book);
		cartItem.setUser(user);
		return cartItem;
	}
	
	/**
	 * 将List<Map>封装成List<CartItem>
	 */
	private List<CartItem> toBeanList(List<Map<String,Object>> mapList){
		List<CartItem> list = new ArrayList<CartItem>();
		//遍历List集合
		for(Map<String,Object> map : mapList){
			CartItem cartItem = toBean(map);
			list.add(cartItem);
		}
		return list;
	}
	
	/**
	 * 根据用户名id查询该用户的所有购物车条目
	 * @throws SQLException 
	 */
	public List<CartItem> findByUser(String uid) throws SQLException{
		/**
		 * 购物车条目里包含了图书的信息，所以需要建立多表查询
		 */
		String sql ="select * from t_book b, t_cartitem c " +
						"where c.bid = b.bid and uid =?" + 
							"order by c.orderBy";
		List<Map<String,Object>> listMap = runner.query(sql, new MapListHandler(),uid);
		return toBeanList(listMap);
	}
	
	public static void main(String[] args) {
		CartItemDao dao = new CartItemDao();
		try {
			System.out.println(dao.findByUser("xxx").toArray());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询购物车是否已经存在该书
	 * @param bid
	 * @param uid
	 * @return
	 * @throws SQLException 
	 */
	public CartItem findByBidAndUid(String bid, String uid) throws SQLException {
		String sql ="select * from t_cartitem where bid=? and uid =?";
		Map<String,Object> map = runner.query(sql, new MapHandler(),
				bid,uid);
		return toBean(map);
	}

	/**
	 * 修改图书数量
	 * @param _quantity
	 * @param cartItem
	 * @throws SQLException 
	 */
	public void updateQuantity(int quantity, CartItem cartItem) throws SQLException {
		String bid = cartItem.getBook().getBid();
		String uid = cartItem.getUser().getUid();
		
		String sql ="update t_cartitem set quantity=? where bid=? and uid=?";
		runner.update(sql, quantity,bid,uid);
	}

	/**
	 * 添加图书
	 * @param cartItemId 
	 * @param quantity
	 * @param bid
	 * @param uid
	 * @throws SQLException 
	 */
	public void addBookItem(String cartItemId, int quantity, String bid, String uid) throws SQLException {
		String sql = "insert into t_cartitem(cartItemId,quantity,bid,uid)" +
						"values(?,?,?,?)";
		runner.update(sql,cartItemId,quantity,bid,uid);
	}
	
	//拼写where子句
	private String toWhereSql(int len){
		StringBuffer sb = new StringBuffer("cartItemId in (");
		for(int i =0; i <len; i++){
			if(i == len-1){
				sb.append("?");
			}
			else{
				sb.append("?,");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	//删除勾选的图书条目
	public void delBookItems(String cartItemId) throws SQLException {
		//将cartItemId按逗号分割成数组
		Object[] obj = cartItemId.split(",");
		//
		//拼写sql语句
		String whereSql = toWhereSql(obj.length);
		//delete * from t_cartitem where cartItemId in (?,?);
		String sql = "delete from t_cartitem where " + whereSql;
		runner.update(sql, obj);
	}
	/**
	 * 修改图书数量
	 * @throws SQLException
	 */
	public void updateQuantity(int quantity,String cartItemId) throws SQLException{
		String sql ="update t_cartitem set quantity = ? where cartItemId = ?";
		runner.update(sql,quantity,cartItemId);
	}
	/**
	 * 查询修改后的图书
	 * @throws SQLException
	 */
	public CartItem findByCartItemId(String cartItemId) throws SQLException{
		String sql = "select * from t_cartitem c, t_book b where c.bid = b.bid and cartItemId = ?";
		Map<String,Object> map = runner.query(sql, new MapHandler(),cartItemId);
		return toBean(map);
	}
	@Test
	public void test() throws SQLException{
		System.out.println(toWhereSql(5));
		CartItemDao dao = new CartItemDao();
		dao.delBookItems("1,2,2,3,4");
	}

	/**
	 * 加载图书
	 * @param cartItemIds
	 * @return
	 * @throws SQLException 
	 */
	public List<CartItem> loadCartItems(String cartItemIds) throws SQLException {
		//分割id
		Object[] obj = cartItemIds.split(",");
		//拼接where子句
		String whereSql = toWhereSql(obj.length);
		//查询
		String sql = "select * from t_cartitem c , t_book b where c.bid = b.bid and " + whereSql;
		List<Map<String,Object>> mapList = runner.query(sql, new MapListHandler(),obj);
		return toBeanList(mapList);
	}
}
