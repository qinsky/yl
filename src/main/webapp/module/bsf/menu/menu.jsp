<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html>
	<head>
		 <meta http-equiv="expires" content="0">  
		 <meta http-equiv="pragma" content="no-cache">  
		 <meta http-equiv="cache-control" content="no-cache"> 
		 <link rel="icon" href="favicon.ico" /> 
		 <style type="text/css" media="all">
			 
			 .field{
				padding-top: 10px;padding-bottom: 5px;border-bottom:thin solid #CAFF70;list-style:none;
			 }

			 div[id^="fdiv_"]{
				float:left;
				margin-top:4px;
				margin-left:0px;
				border:thin soild green;
				width:260px;
			 }
		 </style>
		 <%@include file="/module/bsf/bsf/comm.jsp"%>
		 <script type="text/javascript" src="${ctxPath}/module/bsf/menu/menu_billTemplate.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/menu/menu_billTemplate.js")%>"></script> 
		 <script type="text/javascript" src="${ctxPath}/module/bsf/menu/menu.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/menu/menu.js")%>"></script>
		 <title></title>
	</head>
	<body>
		 <div id="cc" class="easyui-layout" data-options="fit:true">
			<div id="btns_panel" data-options="region:'north',split:false" style="height:40px;border-top:0px;background:rgb(253,253,255);">
				<!-- 按钮面板-->
			</div>

			<div data-options="region:'west',split:true" title="功能菜单" style="width:300px;">
				<div class="easyui-panel" data-options="fit:true" style="padding:5px;border:none;background:rgb(253,253,255)">
					<ul class="easyui-tree" id="tt" ></ul>
				</div>
			</div>
			<div id="billContainer" data-options="region:'center'" style="contentEditable:false">

			</div>
		 </div>
	</body>
	<script> 
		var cxtPath = "${ctxPath}/facade.do"; 
	</script>
</html>
