package com.huangss.goods.admin.admin.Dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import cn.itcast.jdbc.TxQueryRunner;

import com.huangss.goods.admin.admin.domain.Admin;

public class AdminDao {
	private QueryRunner runner = new TxQueryRunner();

	/**
	 * 用户登录
	 * @param adminname
	 * @param adminpwd
	 * @return
	 * @throws SQLException 
	 */
	public Admin login(String adminname, String adminpwd) throws SQLException {
		String sql = "select * from t_admin where adminname =? and adminpwd =?";
		return runner.query(sql, new BeanHandler<Admin>(Admin.class), adminname, adminpwd);
	}
}
