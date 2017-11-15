<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>

<%
/**
	try
	{
		test.TransTest.test();
	}finally
	{
		System.gc();
	}
*/
%>

<html>
<head>
<title>聚讯客户管理系统</title>
<link rel="stylesheet" type="text/css" href="${ctxPath}/easyui/themes/default/easyui.css?12388553">
<link rel="stylesheet" type="text/css" href="${ctxPath}/easyui/themes/icon.css">
<script type="text/javascript" src="${ctxPath}/easyui/jquery.min.js"></script>
<script type="text/javascript" src="${ctxPath}/easyui/jquery.easyui.min.js?241322"></script>
<script type="text/javascript" src="${ctxPath}/easyui/locale/easyui-lang-zh_CN.js"></script> 
<script type="text/javascript" src="${ctxPath}/easyui/jquery.blockUI.js?41555243"></script>
<style>
	.loginPanel{
		/**background-color:rgb(112,178,227);*/
		background-color:rgb(224,236,255);
		/**filter:alpha(Opacity=0);*/
		/**opacity:0.1; */
		padding-left:50px;
		padding-top:6%;
		padding-right:50px;
		text-align:center; 
  		display:table-cell;
		border:0px; 
	}
</style>
<script type="text/javascript">
		 function initial()
		 { 
			 
			 var curw = getParent(window);
			 if(!curw==window)
			 {
			 	curw.locaiton.href = window.location.href;
			 }
			 function getParent(obj)
			 {
				 if(obj.parent && (obj.parent!=obj))
				 {
				 	return getParent(obj.parent);
				 }
				 return obj;
			 }
			 $(".easyui-passwordbox").textbox("textbox").keydown(function(event){
				 if(event.key=="Enter")
				 {
					 login();
				 } 
			 });
		 }
		 $(initial);
		 
		 var ctxPath = "${ctxPath}";
		 
		 
		 function login()
		 {
			var result = $("form").form("validate");
			if(!result)
			{
				return;
			}
			var c = $("#c").textbox("getValue");
			var p = $("#p").textbox("getValue");
			var sendParam = {
					url:ctxPath+"/facade.do"
					,type:"POST"
					,data:"qq9214_bt=login&qq9214_at=login&field1="+c+"&field2="+p 
					,processData:false 
					,dataType:"json" 
					,beforeSend:$.blockUI
					,complete:$.unblockUI
					,success:function(rspData){
					    
						if(rspData.code!="success")
						{
							$.messager.alert("错误",rspData.msg,"error");
							return;
						}
						window.location = ctxPath+"/facade.do?qq9214_bt=auth&qq9214_at=main";
					}
				    ,error:function(rspData){
				    	console.log(rspData);
				    	$.messager.alert("错误",rspData.statusText,"error");
				    } 
				}; 
			$.ajax(sendParam);
		 }
		 
		 function reset()
		 {
			 $("form").form("reset");
		 }
		 
		 
		 
</script>
</head>
<body style="padding:0px;"> 
   
	    <div id="loginPanel" class="loginPanel easyui-panel" data-options="fit:true" style="width:100%;" >
	    	<form name="loginForm">
		    	<div style="margin-top:100px"><strong style="font-size:24px;">聚讯客户管理系统</strong></div>
		    	<br/> 
		        <div style="margin-bottom:20px">
		            <input class="easyui-textbox" id="c" prompt="登录名" data-options="iconCls:'icon-man',required:true,missingMessage:'登录名不能为空',validateOnCreate:false" iconWidth="28" style="width:320px;height:42px;padding:10px;">
		        </div>
		        <div style="margin-bottom:20px">
		            <input class="easyui-passwordbox" id="p" prompt="密码" iconWidth="28"  data-options="required:true,missingMessage:'密码不能为空',validateOnCreate:false"  style="width:320px;height:42px;padding:10px">
		        </div>
	        </form>
	         <div style="margin-bottom:20px">
	            <a class="easyui-linkbutton"  data-options="onClick:login"  style="width:80px;height:34px;" >登录</a>&nbsp;&nbsp;
	            <a class="easyui-linkbutton" data-options="onClick:reset"    style="width:80px;height:34px;" >重置</a>
	        </div>
	    </div>
     
</body>
</html>
