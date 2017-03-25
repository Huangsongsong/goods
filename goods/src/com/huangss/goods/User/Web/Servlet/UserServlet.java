package com.huangss.goods.User.Web.Servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

import com.huangss.goods.User.Service.UserService;
import com.huangss.goods.User.Web.Servlet.Exception.MyException;
import com.huangss.goods.User.domain.User;

public class UserServlet extends BaseServlet {
	private UserService service = new UserService();
	
	//用户登录
	public String loginUser(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获取表单数据,封住到Userz中
		User userform = CommonUtils.toBean(req.getParameterMap(), User.class);
		//登录数据校验
		Map<String, String> maperror = validateLogin(userform, req.getSession());
		if(maperror.size() == 0){
			//数据无误，调用service中的方法
			try {
				User user = service.loginUser(userform);
				req.getSession().setAttribute("user", user);
				//将用户名出入cookie中，方便下次登录显示用户名
				String loginname =userform.getLoginname();
				loginname =  URLEncoder.encode(loginname, "utf-8");//防止中文出现乱码
				Cookie cookie = new Cookie("loginname",loginname);
				//设置保存时间
				cookie.setMaxAge(60*60*24*7);
				resp.addCookie(cookie);
				return "r:/index.jsp";
			} catch (MyException e) {
				req.setAttribute("msg", e.getMessage());
				req.setAttribute("user", userform);
				return "f:/jsps/user/login.jsp";
			}
		}
		//数据出错，转发到登录页面，带回错误信息，和用户填的数据
		req.setAttribute("msg1", maperror);
		req.setAttribute("user", userform);
		return "f:/jsps/user/login.jsp";
	}
	
	//校验登录数据
	private Map<String, String> validateLogin(User user, HttpSession session) {
		Map<String, String> errors = new HashMap<String, String>();
		/*
		 * 校验登录名
		 */
		String loginname = user.getLoginname();
		if(loginname == null || loginname.trim().isEmpty()) {
			errors.put("loginname", "用户名不能为空！");
		} else if(loginname.length() < 3 || loginname.length() > 20) {
			errors.put("loginname", "用户名长度必须在3~20之间！");
		} 
		
		/*
		 *  校验登录密码
		 */
		String loginpass = user.getLoginpass();
		if(loginpass == null || loginpass.trim().isEmpty()) {
			errors.put("loginpass", "密码不能为空！");
		} else if(loginpass.length() < 3 || loginpass.length() > 20) {
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}
		/*
		 *  验证码校验
		 */
		String verifyCode = user.getVerifyCode();
		String vcode = (String) session.getAttribute("vCode");
		if(verifyCode == null || verifyCode.trim().isEmpty()) {
			errors.put("verifyCode", "验证码不能为空！");
		} else if(!verifyCode.equalsIgnoreCase(vcode)) {
			errors.put("verifyCode", "验证码错误！");
		}
		return errors;
	}
	
	//修改密码
	public String updatePass(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//封装数据
		User userform = CommonUtils.toBean(req.getParameterMap(), User.class);
		User user = (User) req.getSession().getAttribute("user");
		// 如果用户没有登录，返回到登录页面，显示错误信息
		if(user == null) {
			req.setAttribute("msg", "您还没有登录！");
			return "f:/jsps/user/login.jsp";
		}
		//校验数据的正确性
		Map<String,String> errors = checkPass(userform);
		if(errors.size() >0){
			req.setAttribute("msg1", errors);
			req.setAttribute("user",userform);
			return "f:/jsps/user/pwd.jsp";
		}
		//调用service中的房法
		try {
			service.updatePass(user.getUid(),userform);
			req.setAttribute("msg", "修改密码成功");
			req.setAttribute("code", "success");
			return "f:/jsps/msg.jsp";
		} catch (MyException e) {
			req.setAttribute("msg", e.getMessage());//保存异常信息到request
			req.setAttribute("user", userform);//为了回显
			return "f:/jsps/user/pwd.jsp";
		}
	}
	
	private Map<String, String> checkPass(User userform) {
		Map<String,String> errors = new HashMap<String,String>();
		/*
		 *  校验登录密码
		 */
		String oldpass = userform.getLoginpass();
		if(oldpass == null || oldpass.trim().isEmpty()) {
			errors.put("loginpass", "密码不能为空！");
		} else if(oldpass.length() < 3 || oldpass.length() > 20) {
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}
		
		String newpass = userform.getNewpass();
		if(newpass == null || newpass.trim().isEmpty()) {
			errors.put("newpass", "密码不能为空！");
		} else if(newpass.length() < 3 || newpass.length() > 20) {
			errors.put("newpass", "密码长度必须在3~20之间！");
		}
		
		String renewpass = userform.getReloginpass();
		if(renewpass == null || renewpass.trim().isEmpty()) {
			errors.put("renewpass", "密码不能为空！");
		} else if(renewpass.length() < 3 || renewpass.length() > 20) {
			errors.put("renewpass", "密码长度必须在3~20之间！");
		}else if(!renewpass.equals(newpass)){
			errors.put("renewpass", "和新密码不一致！");
		}
		return errors;
	}

	//用户注册
	public String registUser(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获取参数
		Map<String, String[]> map = req.getParameterMap();
		//将参数封装
		User user = CommonUtils.toBean(map, User.class);
		//校验数据
		Map<String,String> errormap = checkPamarates(user, req.getSession());
		
		if(errormap.size() > 0){
			req.setAttribute("user", user);//将用户填的信息返回，提高用户体验
			req.setAttribute("errors", errormap);//回现错误信息
			return "f:/jsps/user/regist.jsp";
		}
		//调用service方法
		try {
			service.registUser(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		/*
		 *  保存成功信息，转发到msg.jsp显示！
		 */
		req.setAttribute("code", "success");
		req.setAttribute("msg", "注册功能，请马上到邮箱激活！");
		return "f:/jsps/msg.jsp";
	}
	
	//服务器端进行数据校验
	private Map<String, String> checkPamarates(User user, HttpSession session) {
		Map<String,String> errors = new HashMap<String,String>();
		/*
		 * 校验登录名
		 */
		String loginname = user.getLoginname();
		if(loginname == null || loginname.trim().isEmpty()) {
			errors.put("loginname", "用户名不能为空！");
		} else if(loginname.length() < 3 || loginname.length() > 20) {
			errors.put("loginname", "用户名长度必须在3~20之间！");
		} else
			try {
				if(!service.validateLoginname(loginname)) {
					errors.put("loginname", "用户名已被注册！");
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		
		/*
		 *  校验登录密码
		 */
		String loginpass = user.getLoginpass();
		if(loginpass == null || loginpass.trim().isEmpty()) {
			errors.put("loginpass", "密码不能为空！");
		} else if(loginpass.length() < 3 || loginpass.length() > 20) {
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}
		
		/*
		 *  确认密码校验
		 */
		String reloginpass = user.getReloginpass();
		if(reloginpass == null || reloginpass.trim().isEmpty()) {
			errors.put("reloginpass", "确认密码不能为空！");
		} else if(!reloginpass.equals(loginpass)) {
			errors.put("reloginpass", "两次输入不一致！");
		}
		
		/*
		 *  校验email
		 */
		String email = user.getEmail();
		if(email == null || email.trim().isEmpty()) {
			errors.put("email", "Email不能为空！");
		} else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
			errors.put("email", "Email格式错误！");
		} else
			try {
				if(!service.validateEmail(email)) {
					errors.put("email", "Email已被注册！");
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		
		/*
		 *  验证码校验
		 */
		String verifyCode = user.getVerifyCode();
		String vcode = (String) session.getAttribute("vCode");
		if(verifyCode == null || verifyCode.trim().isEmpty()) {
			errors.put("verifyCode", "验证码不能为空！");
		} else if(!verifyCode.equalsIgnoreCase(vcode)) {
			errors.put("verifyCode", "验证码错误！");
		}
		return errors;
	}

	//判断用户名是否被注册
	public String validateLoginname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//得到参数
		String loginname = req.getParameter("loginname");
			
		boolean result =false;
		try {
			result =service.validateLoginname(loginname);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		resp.getWriter().print(result);
		return null;
	}
	
	//判断邮箱是否被注册
	public String validateEmail(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{
		//得到参数
		String email = req.getParameter("email");
			
		boolean result =false;
		try{
			result =service.validateEmail(email);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		resp.getWriter().print(result);
		return null;
	}
	//校验验证码
	public String validateVerifyCode(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获取参数
		String vCode = req.getParameter("vCode");
		String sCode = (String) req.getSession().getAttribute("vCode");
		
		boolean result =false;
		if(!vCode.equalsIgnoreCase(sCode)){
			result =false;
		}
		else{
			result =true;
		}
		resp.getWriter().print(result);
		
		return null;
	}
	
	//激活用户
	public String activation(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/**
		 * 获取参数，如果是表单，直接封装到实体类中
		 */
		//获取参数
		String activationCode = req.getParameter("activationCode");
		
		//调用service方法，完成 用户激活
		try {
			service.activation(activationCode);
			req.setAttribute("code", "success");//通知msg.jsp显示对号
			req.setAttribute("msg", "恭喜，激活成功，请马上登录！");
		} catch (Exception e) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", e.getMessage());
		}	
		return "f:/jsps/msg.jsp";
	}
	//退出登录
	public String exit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getSession().invalidate();
		return "r:/jsps/user/login.jsp";
	}
}
