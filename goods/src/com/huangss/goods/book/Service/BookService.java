package com.huangss.goods.book.Service;

import java.sql.SQLException;

import com.huangss.goods.book.Dao.BookDao;
import com.huangss.goods.book.domain.Book;
import com.huangss.goods.pager.PageBean;


public class BookService {
	private BookDao dao = new BookDao();
	
	/**
	 * 加载图书
	 * @param bid
	 * @return
	 */
	public Book load(String bid) {
		try {
			return dao.findByBid(bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按分类查
	 * @param cid
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByCategory(String cid, int pc) {
		try {
			return dao.findByCategory(cid, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按书名查
	 * @param bname
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByBname(String bname, int pc) {
		try {
			return dao.findByBname(bname, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按作者查
	 * @param author
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByAuthor(String author, int pc) {
		try {
			return dao.findByAuthor(author, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按出版社查
	 * @param press
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByPress(String press, int pc) {
		try {
			return dao.findByPress(press, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 多条件组合查询
	 * @param criteria
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByCombination(Book criteria, int pc) {
		try {
			return dao.findByCombination(criteria, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 找指定二级分类下的图书
	 * @param cid
	 * @return
	 */
	public int findBook(String cid) {
		try {
			return dao.findBook(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 删除图书
	 * @param bid
	 */
	public void delete(String bid) {
		try {
			dao.delete(bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 修改图书
	 * @param book
	 */
	public void edit(Book book) {
		try {
			dao.edit(book);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加图书
	 * @param book
	 */
	public void addBook(Book book) {
		try {
			dao.addBook(book);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
