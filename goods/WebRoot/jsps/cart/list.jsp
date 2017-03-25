<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>cartlist.jsp</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
	<script src="<c:url value='/js/round.js'/>"></script>
	
	<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/cart/list.css'/>">
<script type="text/javascript">
$(function(){
	//加载总计
	sumTotal();
	//给全选复选框添加click事件
	$("#selectAll").click(function(){
		var bool = $(this).attr("checked");
		//实现全选或全不选
		subCheckBox(bool);
		//同步修改结算按钮的状态
		setJieSuan(bool);
		//重新计算总计
		sumTotal();
	});
	
	//给所有的条目复选框添加click
	$(":checkbox[name=checkboxBtn]").click(function() {
		var all = $(":checkbox[name=checkboxBtn]").length;//所有条目的个数
		var select = $(":checkbox[name=checkboxBtn][checked=true]").length;//获取所有被选择条目的个数
		if(all == select){
			//全选复选框设为true
			$("#selectAll").attr("checked",true);
			//同步修改结算按钮的状态
			setJieSuan(true);
		}else if(select ==0){
			//全选复选框设为false
			$("#selectAll").attr("checked",false);
			//同步修改结算按钮的状态
			setJieSuan(false);
		}else{
			//全选复选框设为false
			$("#selectAll").attr("checked",false);
			//同步修改结算按钮的状态
			setJieSuan(true);
		}
		//重新计算总计
		sumTotal();
	});
	//给减号添加click事件
	$(".jian").click(function(){
		//得到当前图书的id
		var id = $(this).attr("id").substring(0,32);
		//得到当前的数量
		var quantity = $(".quantity").val();
		if(quantity == 1){
			if(confirm("是否删除？")) {
				location="/goods/cartItemServlet?method=delBookItems&cartItemIds=" + id;
			}
		}else{
			sendAjaxRequest(id,Number(quantity)-1);
		}
	});
	//给加号添加click事件
	$(".jia").click(function(){
		//得到当前图书的id
		var id = $(this).attr("id").substring(0,32);
		//得到当前的数量
		var quantity = $(".quantity").val();
		//发送一步请求
		sendAjaxRequest(id,Number(quantity)+1);
	});
});
//发送一步请求
var sendAjaxRequest = function(id,quantity){
	$.ajax({
		async:false,
		cache:false,
		url:"/goods/cartItemServlet",
		data:{method:"updateQuantity",cartItemId:id,quantity:quantity},
		type:"POST",
		dataType:"json",
		success:function(result){
			$(".quantity").val(result.quantity);
			$("#" + id + "Subtotal").text(result.subtotal);
			//重新计算总计
			sumTotal();
		}
	});
}
//设置结算按钮的状态
var setJieSuan = function(bool){
	if(bool) {
		$("#jiesuan").removeClass("kill").addClass("jiesuan");
		$("#jiesuan").unbind("click");//撤消当前元素止所有click事件
	} else {
		$("#jiesuan").removeClass("jiesuan").addClass("kill");
		$("#jiesuan").click(function() {return false;});
	}
};
//子复选框全选
var subCheckBox = function(bool){
	$(":checkbox[name=checkboxBtn]").attr("checked",bool);
};
//计算总计
var sumTotal = function(){
	var total =0;
	$(":checkbox[name=checkboxBtn][checked=true]").each(function(){//得到所有被勾选的复选框
		var id = $(this).val();
		var subTotalId = id + "Subtotal";
		var sub = $("#"+subTotalId).text();
		total+=Number(sub);//将字符串sub转换成数字，累加
	});
	
	$("#total").text(round(total, 2));//显示总计
};
//批量删除
var delBookItems = function(){
	var arrays = new Array();
	//得到所有被勾选购物条目的id
	$(":checkbox[name=checkboxBtn][checked=true]").each(function(){
		arrays.push($(this).val());//将勾选的购物条目所有id存到数组中，
	});
	location="/goods/cartItemServlet?method=delBookItems&cartItemIds=" + arrays;
};
//提交订单
var loadCartItems = function(){
	var arrays = new Array();
	//得到所有被图书的id
	$(":checkbox[name=checkboxBtn][checked=true]").each(function(){
		//将id装到数组中
		arrays.push($(this).val());
	});
	//给表单的隐藏控件赋值
	$("#cartItemIds").val(arrays + "");
	// 把总计的值，也保存到表单中
	$("#hiddenTotal").val($("#total").text());
	$("#form1").submit();
}
</script>
  </head>
  <body>

<c:choose>
	<c:when test="${empty requestScope.listItem}">
		<table width="95%" align="center" cellpadding="0" cellspacing="0">
		<tr>
			<td align="right">
				<img align="top" src="<c:url value='/images/icon_empty.png'/>"/>
			</td>
			<td>
				<span class="spanEmpty">您的购物车中暂时没有商品</span>
			</td>
		</tr>
	</table>  
	</c:when>
	
	<c:otherwise>
		<table width="95%" align="center" cellpadding="0" cellspacing="0">
			<tr align="center" bgcolor="#efeae5">
				<td align="left" width="50px">
					<input type="checkbox" id="selectAll" checked="checked"/><label for="selectAll">全选</label>
				</td>
				<td colspan="2">商品名称</td>
				<td>单价</td>
				<td>数量</td>
				<td>小计</td>
				<td>操作</td>
			</tr>
			<c:forEach items="${listItem }" var="item">
				<tr align="center">
					<td align="left">
						<input value="${item.cartItemId }" type="checkbox" name="checkboxBtn" checked="checked"/>
					</td>
					<td align="left" width="70px">
						<a class="linkImage" href="<c:url value='/bookServlet?method=load&bid=${item.book.bid }'/>"><img border="0" width="54" align="top" src="<c:url value='${item.book.image_b }'/>"/></a>
					</td>
					<td align="left" width="400px">
					    <a href="<c:url value='/bookServlet?method=load&bid=${item.book.bid }'/>"><span>${item.book.bname }</span></a>
					</td>
					<td><span>&yen;<span class="currPrice" id="12345CurrPrice">${item.book.currPrice }</span></span></td>
					<td>
						<a class="jian" id="${item.cartItemId }Jian"></a><input class="quantity" readonly="readonly" id="${item.cartItemId }Quantity" type="text" value="${item.quantity }"/><a class="jia" id="${item.cartItemId }Jia"></a>
					</td>
					<td width="100px">
						<span class="price_n">&yen;<span class="subTotal" id="${item.cartItemId }Subtotal">${item.subTotal }</span></span>
					</td>
					<td>
						<a href="<c:url value='/cartItemServlet?method=delBookItems&cartItemIds=${item.cartItemId }'/>">删除</a>
					</td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="4" class="tdBatchDelete">
					<a href="javascript:delBookItems();">批量删除</a>
				</td>
				<td colspan="3" align="right" class="tdTotal">
					<span>总计：</span><span class="price_t">&yen;<span id="total"></span></span>
				</td>
			</tr>
			<tr>
				<td colspan="7" align="right">
					<a href="javascript:loadCartItems();" id="jiesuan" class="jiesuan"></a>
				</td>
			</tr>
		</table>
		<form id="form1" action="<c:url value='/cartItemServlet'/>" method="post">
			<input type="hidden" name="cartItemIds" id="cartItemIds" />
			<input type="hidden" name="total" id="hiddenTotal"/>
			<input type="hidden" name="method" value="loadCartItems"/>
		</form>
	</c:otherwise>
</c:choose>
  </body>
</html>
