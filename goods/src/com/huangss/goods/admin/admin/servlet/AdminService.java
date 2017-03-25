package com.huangss.goods.admin.admin.servlet;

import java.sql.SQLException;

import com.huangss.goods.admin.admin.Dao.AdminDao;
import com.huangss.goods.admin.admin.domain.Admin;

public class AdminService {
	private AdminDao dao = new AdminDao();

	/**
	 * 管理员登录
	 * @param form
	 * @return
	 */
	public Admin login(Admin form) {
		try {
			return dao.login(form.getAdminname(), form.getAdminpwd());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
