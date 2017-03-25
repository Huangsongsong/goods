package com.huangss.goods.cartItem.Service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.commons.CommonUtils;

import com.huangss.goods.User.domain.User;
import com.huangss.goods.book.domain.Book;
import com.huangss.goods.cartItem.Dao.CartItemDao;
import com.huangss.goods.cartItem.domain.CartItem;

public class CartItemService {
	private CartItemDao dao = new CartItemDao();
	/**
	 * 通过用户的id号查询该用户的购物车条目
	 *
	 */
	public List<CartItem> findByUser(User user){
		try {
			return dao.findByUser(user.getUid());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 添加购物条目
	 * @param quantity 
	 * @param book
	 * @param user
	 */
	public void addBookItem(CartItem cartItem) {
		/**
		 * 通过uid和bid查询该用户下是否有相同的数据，
		 * 如果有更改该本书的quantity
		 * 如果没有，添加
		 */
		try{
			//向数据库查询是否该书存在
			CartItem _cartItem = dao.findByBidAndUid(cartItem.getBook().getBid(),
					cartItem.getUser().getUid());
			if(_cartItem == null){
				String cartItemId = CommonUtils.uuid();
				dao.addBookItem(cartItemId,
						cartItem.getQuantity(),
						cartItem.getBook().getBid(),
						cartItem.getUser().getUid());
			}else{
				//修改该书的数量
				int _quantity = cartItem.getQuantity() + _cartItem.getQuantity();
				dao.updateQuantity(_quantity,cartItem);
			}
			
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	//删除勾选的图书条目
	public void delBookItems(String cartItemId) {
		try {
			dao.delBookItems(cartItemId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 修改数量
	 * @param cartItemId
	 * @param quantity
	 * @return 
	 * @throws SQLException 
	 */
	public CartItem updateQuantity(String cartItemId, int quantity) {
		//修改图书数量
		try {
			dao.updateQuantity(quantity,cartItemId);
			//查询修改后的图书
			CartItem cartItem = dao.findByCartItemId(cartItemId);
			return cartItem;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 加载购买的图书
	 * @param cartItemIds
	 * @return
	 */
	public List<CartItem> loadCartItems(String cartItemIds) {
		try {
			return dao.loadCartItems(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
