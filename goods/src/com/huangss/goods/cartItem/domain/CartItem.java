package com.huangss.goods.cartItem.domain;

import java.math.BigDecimal;

import com.huangss.goods.User.domain.User;
import com.huangss.goods.book.domain.Book;

public class CartItem {
	private String cartItemId;
	private int quantity;
	private Book book;//外键
	private User user;//外键
	
	//计算小计
	public double getSubTotal(){
		BigDecimal b1= new BigDecimal(quantity + "");
		BigDecimal b2= new BigDecimal(book.getCurrPrice() + "");
		BigDecimal b3 = b1.multiply(b2);
		return b3.doubleValue();
	}
	public String getCartItemId() {
		return cartItemId;
	}
	public void setCartItemId(String cartItemId) {
		this.cartItemId = cartItemId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "CartItem [cartItemId=" + cartItemId + ", quantity=" + quantity
				+ ", book=" + book + ", user=" + user + "]";
	}
	
}
