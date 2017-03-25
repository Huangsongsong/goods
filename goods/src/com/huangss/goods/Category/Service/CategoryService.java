package com.huangss.goods.Category.Service;

import java.sql.SQLException;
import java.util.List;

import com.huangss.goods.Category.Dao.CategoryDao;
import com.huangss.goods.Category.domain.Category;

public class CategoryService {
	private CategoryDao dao = new CategoryDao();

	/**
	 * 加载所有分类
	 * @return
	 */
	public List<Category> findAllCategory() {
		try {
			return dao.findAllCategory();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 添加分类
	 * @param category
	 */
	public void add(Category category){
		try {
			dao.add(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Category> loadParent(){
		try {
			return dao.loadParent();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查找所有二级分类
	 * @param cid
	 * @return
	 */
	public int loadChildren(String cid) {
		try {
			return dao.loadChildren(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 删除指定分类
	 * @param cid
	 */
	public void delCategory(String cid) {
		try {
			dao.delCategory(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查找指定分一级分类
	 * @param cid
	 * @return
	 */
	public Category findParent(String cid) {
		try {
			return dao.findCategory(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 修改分类
	 * @param category
	 */
	public void updateCategory(Category category) {
		try {
			dao.updateCategory(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查找指定分二级分类
	 * @param cid
	 * @return
	 */
	public Category findChild(String cid) {
		try {
			return dao.findCategory(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 查找指定的二级分类
	 * @param pid
	 * @return
	 */
	public List<Category> findChildren(String pid) {
		try {
			return dao.findCategoryByPid(pid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
