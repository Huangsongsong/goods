/**
 * 页面加载结束时，执行相应的js
 */
$(function(){
	/**
	 * 隐藏没有内容的label,显示有错误信息的label
	 */
	$(".labelError").each(function(){
		showError($(this));//调用相应函数
	});
	
	/**
	 * 对input框的数据进行校验
	 */
	/**
	 * 当输入框得到焦点时，相应的label的错误信息应该隐藏
	 */
	$(".input").focus(function(){
		var labelId = $(this).attr("id") + "Error";
		$("#"+ labelId).text("");//将错误信息清空
		showError($("#"+ labelId));
	});
	/**
	 * 当输入框失去焦点时，调用函数进行数据校验，如果数据不符合，设置相应的错误的网信息，显示label
	 */
	$(".input").blur(function(){
		var id = $(this).attr("id");
		var funName = "check" + id.substring(0,1).toUpperCase() + id.substring(1) + "()";
		eval(funName);
	});
});

/**
 * 对用户名进行校验,如果数据不符合，设置相应的错误的网信息，显示label
 */
var checkLoginname = function(){
	var id = "loginname";
	var value = $("#" + id).attr("value");
	//vaule不能为空
	if(!value){
		$("#" + id + "Error").text("用户名不能为空...");
		showError($("#" + id + "Error"));
		return false;
	}
	//value的长度必须在3~20位之间
	if(value.length<3 || value.length >20){
		$("#" + id + "Error").text("用户名必须在3~20位之间...");
		showError($("#" + id + "Error"));
		return false;
	}
	//客户端校验用户名是否被注册
	$.ajax({
		cache: false,//是否启用缓存
		async: false,//是否一步请求
		type: "POST",//发送方式
		dataType: "json",//相应文本格式
		data: {method: "validateLoginname", loginname : value},//参数
		url: "/goods/userServlet",
		success: function(result) {
			if(!result){
				$("#" + id + "Error").text("用户名已经被注册...");
				showError($("#" + id + "Error"));
				return false;
			}
		}
	});		
}
/**
 * 对密码进行校验
 */
var checkLoginpass = function(){
	var id = "loginpass";
	var value = $("#" + id).attr("value");
	//vaule不能为空
	if(!value){
		$("#" + id + "Error").text("密码不能为空...");
		showError($("#" + id + "Error"));
		return false;
	}
	//value的长度必须在3~20位之间
	if(value.length<3 || value.length >20){
		$("#" + id + "Error").text("密码必须在3~20位之间...");
		showError($("#" + id + "Error"));
		return false;
	}
}
/**
 * 对确认密码进行校验
 */
var checkReloginpass = function(){
	var id = "reloginpass";
	var value = $("#" + id).attr("value");
	//vaule不能为空
	if(!value){
		$("#" + id + "Error").text("确认密码不能为空...");
		showError($("#" + id + "Error"));
		return false;
	}
	//确认密码必须与密码一致
	if(value != $("#loginpass").attr("value")){
		$("#" + id + "Error").text("两次密码不一致...");
		showError($("#" + id + "Error"));
		return false;
	}
}
/**
 * 对Email进行校验
 */
var checkEmail = function(){
	var id = "email";
	var value = $("#" + id).attr("value");
	if(!/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(value)){
		$("#" + id + "Error").text("邮箱格式不正确...");
		showError($("#" + id + "Error"));
		return false;
	}
	//客户端校验邮箱是否被注册
	$.ajax({
		cache: false,//是否启用缓存
		async: false,//是否一步请求
		type: "POST",//发送方式
		dataType: "json",//相应文本格式
		data: {method: "validateEmail", email : value},//参数
		url: "/goods/userServlet",
		success: function(result) {
			if(!result){
				$("#" + id + "Error").text("邮箱已经被注册...");
				showError($("#" + id + "Error"));
				return false;
			}
		}
	});	
}
/**
 * 对验证码进行校验
 */
var checkVerifyCode = function(){
	var id = "verifyCode";
	var value = $("#" + id).attr("value");
	if(!value){
		$("#" + id + "Error").text("验证码不能为空...");
		showError($("#" + id + "Error"));
		return false;
	}
	//验证码的长度必须为4
	if(value.length != 4 ){
		$("#" + id + "Error").text("验证码的长度必须为4...");
		showError($("#" + id + "Error"));
		return false;
	}
	//校验验证码是否正确
	$.ajax({
		cache: false,//是否启用缓存
		async: false,//是否一步请求
		type: "POST",//发送方式
		dataType: "json",//相应文本格式
		data: {method: "validateVerifyCode", vCode : value},//参数
		url: "/goods/userServlet",
		success: function(result) {
			if(!result){
				$("#" + id + "Error").text("验证码错误...");
				showError($("#" + id + "Error"));
				return false;
			}
		}
	});	
}

/**
 * 实现label的隐藏和出现
 */
var showError = function(label){
	var text = label.text();
	if(!text){
		label.css("display","none");
	}else{
		label.css("display","");
	}
}

/**
 * 切换验证码
 * 设置new Date().getTime()参数防止浏览器读取缓存
 * @returns
 */
var _hyz = function(){
	$("#vCode").attr("src","/goods/VerifyCodeServlet?time=" + new Date().getTime());
}

//提交表单时，再进行数据校验，防止出错
var subform = function(){
	var bool = true;//表示校验通过
	if(!checkLoginname()) {
		bool = false;
	}
	if(!checkLoginpass()) {
		bool = false;
	}
	if(!checkReloginpass()) {
		bool = false;
	}
	if(!checkEmail()) {
		bool = false;
	}
	if(!checkVerifyCode()) {
		bool = false;
	}
	
	if(bool == true){
		$("#form").submit();
	}
}