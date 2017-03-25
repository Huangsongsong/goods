package com.huangss.goods.User.Dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.jdbc.TxQueryRunner;

import com.huangss.goods.User.domain.User;

public class UserDao {
	private QueryRunner runner = new TxQueryRunner();

	public boolean validateLoginname(String loginname) throws SQLException {
		String sql ="select count(1) from t_user where loginname =?";
		
		//new ScalarHandle()用來封装查询结果为数字的情况
		Number num = (Number)runner.query(sql, new ScalarHandler(), loginname);
		
		return num.intValue() == 0;
	}

	//判断邮箱是否被注册
	public boolean validateEmail(String email) throws SQLException {
		String sql ="select count(1) from t_user where email =?";
		
		//new ScalarHandle()用來封装查询结果为数字的情况
		Number num = (Number)runner.query(sql, new ScalarHandler(), email);
		
		return num.intValue() == 0;
	}

	//注册用户
	public void registUser(User user) throws SQLException {
		String sql = "insert into t_user values(?,?,?,?,?,?)";
		Object[] param ={user.getUid(),user.getLoginname(),user.getLoginpass(),user.getEmail(),
				user.isStatus(), user.getActivationCode()};
		runner.update(sql, param);
	}
	
	//验证激活码是否有效
	public User checkActivation(String activationCode) throws SQLException {
		String sql ="select * from t_user where activationCode=?";
		return runner.query(sql, new BeanHandler<User>(User.class), activationCode);
	}

	public void updateStatus(String uid, boolean status) throws SQLException {
		String sql ="update t_user set status=? where uid =?";
		runner.update(sql, status, uid);
	}

	//用户登录
	public User loginUser(User userform) throws SQLException {
		String sql ="select * from t_user where loginname=? and loginpass=?";
		return runner.query(sql, new BeanHandler<User>(User.class), userform.getLoginname(),userform.getLoginpass());
	}

	//检查旧密码的正确性
	public boolean checkOldPass(String uid, String oldpass) throws SQLException {
		String sql = "select count(*) from t_user where uid =? and loginpass=?";
		Number num = (Number) runner.query(sql, new ScalarHandler(),uid,oldpass);
		return num.intValue() >0;
	}

	public void updataPass(String uid, String newpass) throws SQLException {
		String sql = "update t_user set loginpass=? where uid =?";
		runner.update(sql, newpass,uid);
	}
}
