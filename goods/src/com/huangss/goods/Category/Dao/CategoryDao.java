package com.huangss.goods.Category.Dao;

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

import com.huangss.goods.Category.domain.Category;

public class CategoryDao {
	private QueryRunner runner = new TxQueryRunner();
	
	/**
	 * 将map中的数据封装到category中
	 * @param listMap
	 * @return
	 */
	private Category toCategory(Map<String,Object> map){
		Category category = CommonUtils.toBean(map, Category.class);
		/**
		 * 手动封装pid
		 */
		String pid = (String)map.get("pid");
		/**
		 * pid如果为空，表示为一级分类，否则为二级分类，
		 * pid就表示为一级分类的主键
		 */
		if(pid != null){
			Category parent = new Category();
			parent.setCid(pid);
			category.setParent(parent);
		}
		return category;
	}
	
	/**
	 * 将List<map>中的数据封装到List<category>中
	 * @param listMap
	 * @return
	 */
	private List<Category> toCategoryList(List<Map<String,Object>> listMap){
		List<Category> list = new ArrayList<Category>();
		
		for(Map<String,Object> map : listMap){
			Category category = toCategory(map);
			list.add(category);
		}
		return list;
	}
	//查询所有的分类
	public List<Category> findAllCategory() throws SQLException {
		/**
		 * 得到所有的一级分类
		 */
		String sql = "select * from t_category where pid is null order by orderBy";
		/**
		 * 如果直接封装到Category中，pid数据将会丢失，所以先封装到Map中，
		 * 在用Beanutils工具封装到Category中，再将pid手动封装到Category中
		 */
		List<Map<String,Object>> listMap = runner.query(sql, new MapListHandler());
		
		List<Category> parents = toCategoryList(listMap);//所有的一级分类集合，并没有二级分类数据
		/**
		 * 通过一级分类的主键找到旗下的所有二级分类
		 */
		for(Category parent : parents){
			List<Category> children = findCategoryByPid(parent.getCid());
			parent.setChildren(children);
		}
		return parents;
	}

	/**
	 * 查找指定分类下的所有二级分类
	 * @param pid
	 * @return
	 * @throws SQLException
	 */
	public List<Category> findCategoryByPid(String pid) throws SQLException {
		String sql = "select * from t_category where pid =?";
		List<Map<String,Object>> listMap = runner.query(sql, new MapListHandler(),pid);
		return toCategoryList(listMap);
	}

	/**
	 * 添加一级分类和二级分类
	 * @throws SQLException 
	 */
	public void add(Category category) throws SQLException{
		String pid =null;
		//如果category.getParent() != null表示为二级分类
		if(category.getParent() != null){
			pid = category.getParent().getCid();
		}
		
		String sql ="insert into t_category(cid, cname, pid, `desc`) values(?,?,?,?)";
		Object[] objs ={category.getCid(), category.getCname(), pid, category.getDesc()};
		
		runner.update(sql, objs);
	}
	
	/**
	 * 加载所有一级分类
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> loadParent() throws SQLException{
		String sql ="select * from t_category where pid is null";
		return runner.query(sql, new BeanListHandler<Category>(Category.class));
	}

	/**
	 * 查询指定父分类下子分类的个数
	 * @param cid
	 * @return
	 * @throws SQLException 
	 */
	public int loadChildren(String cid) throws SQLException {
		String sql ="select count(*) from t_category where pid = ?";
		Number num = (Number)runner.query(sql, new ScalarHandler(), cid);
		return num == null ? 0 : num.intValue();
	}
	
	/**
	 * 删除指定的分类
	 * @param cid
	 * @throws SQLException 
	 */
	public void delCategory(String cid) throws SQLException {
		String sql = "delete from t_category where cid = ?";
		runner.update(sql, cid);
	}

	/**
	 * 查找指定的分类
	 * @param cid
	 * @return
	 * @throws SQLException 
	 */
	public Category findCategory(String cid) throws SQLException {
		String sql ="select * from t_category where cid = ?";
		return runner.query(sql, new BeanHandler<Category>(Category.class), cid);
	}

	/**
	 * 修改分类
	 * @param category
	 * @throws SQLException
	 */
	public void updateCategory(Category category) throws SQLException {
		String pid =null;
		if(category.getParent() != null){//表示为二级分类
			pid =category.getParent().getCid();
		}
		
		String sql = "update t_category set cname =?, `desc` =?, pid =? where cid =?";
		Object[] objs ={category.getCname(), category.getDesc(), pid, category.getCid()};
		runner.update(sql, objs);
	}
}
