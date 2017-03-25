package com.huangss.goods.User.domain;

/**
 * 用户模块的实体类
 * @author 黄松松
 *
 */
public class User {
	//数据库表属性
	private String uid;
	private String loginname;
	private String loginpass;
	private String email;
	private boolean status;
	private String activationCode;
	
	//注册表单属性
	private String reloginpass;
	private String verifyCode;
	
	//登录表单属性
	//修改密码表单属性
	private String newpass;
	
	public String getNewpass() {
		return newpass;
	}
	public void setNewpass(String newpass) {
		this.newpass = newpass;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getLoginname() {
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
	public String getLoginpass() {
		return loginpass;
	}
	public void setLoginpass(String loginpass) {
		this.loginpass = loginpass;
	}
	public String getReloginpass() {
		return reloginpass;
	}
	public void setReloginpass(String reloginpass) {
		this.reloginpass = reloginpass;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getActivationCode() {
		return activationCode;
	}
	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}
	
	@Override
	public String toString() {
		return "User [uid=" + uid + ", loginname=" + loginname + ", loginpass="
				+ loginpass + ", email=" + email + ", status=" + status
				+ ", activationCode=" + activationCode + ", reloginpass="
				+ reloginpass + ", verifyCode=" + verifyCode + ", newpass="
				+ newpass + "]";
	}
}
