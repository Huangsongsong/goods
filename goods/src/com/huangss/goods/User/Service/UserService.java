package com.huangss.goods.User.Service;

import java.sql.SQLException;

import cn.itcast.commons.CommonUtils;

import com.huangss.goods.User.Dao.UserDao;
import com.huangss.goods.User.Web.Servlet.Exception.MyException;
import com.huangss.goods.User.domain.User;

public class UserService {
	private UserDao dao = new UserDao();

	//判断用户名是否被注册
	public boolean validateLoginname(String loginname) throws SQLException {
		return dao.validateLoginname(loginname);
	}

	//判断邮箱是否被注册
	public boolean validateEmail(String email) throws SQLException {
		return dao.validateEmail(email);
	}

	public void registUser(User user) throws SQLException {
		//补充参数
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid() + CommonUtils.uuid());
		//调用dao方法
		dao.registUser(user);
		//发送邮件
		/*
		 * 把配置文件内容加载到prop中
		 */
		/*Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		
		 * 登录邮件服务器，得到session
		 
		String host = prop.getProperty("host");//服务器主机名
		String name = prop.getProperty("username");//登录名
		String pass = prop.getProperty("password");//登录密码
		Session session = MailUtils.createSession(host, name, pass);
		
		
		 * 创建Mail对象
		 
		String from = prop.getProperty("from");
		String to = user.getEmail();
		String subject = prop.getProperty("subject");
		// MessageForm.format方法会把第一个参数中的{0},使用第二个参数来替换。
		// 例如MessageFormat.format("你好{0}, 你{1}!", "张三", "去死吧"); 返回“你好张三，你去死吧！”
		String content = MessageFormat.format(prop.getProperty("content"), user.getActivationCode());
		Mail mail = new Mail(from, to, subject, content);
		
		 * 发送邮件
		 
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}*/
	}

	//用户激活
	public void activation(String activationCode) throws MyException{
		//判断激活码是否有效
		try {
			User user = dao.checkActivation(activationCode);
			if(user == null){
				//激活码无效
				throw new MyException("激活码无效，请重新注册...");
			}
			if(user.isStatus()){
				throw new MyException("你已经被激活，请勿再次激活...");
			}
			dao.updateStatus(user.getUid(),true);
		} catch (SQLException e){
			throw new RuntimeException(e);
		}
	}

	//用户登陆
	public User loginUser(User userform) throws MyException{
		try{
			User user = dao.loginUser(userform);
			if(user == null){
				throw new MyException("用户名或密码错误...");
			}
			if(!user.isStatus()){
				throw new MyException("您还没有激活...");
			}
			return user;
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	//修改密码
	public void updatePass(String uid, User userform) throws MyException {		
		try {
			//先判断oldpass是否正确
			boolean result = dao.checkOldPass(uid, userform.getLoginpass());
			if(!result){
				throw new MyException("原密码错误...");
			}
			dao.updataPass(uid, userform.getNewpass());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
