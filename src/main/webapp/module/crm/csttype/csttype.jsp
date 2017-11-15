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
			 html {font-size: 100%;}
			<!-- body {font-size:1em;} --> 
			 .field{
				padding-top: 10px;padding-bottom: 5px;border-bottom:thin solid #CAFF70;list-style:none;
			 }

			 div[id^="fdiv_"]{
				float:left;
				margin-top:4px;
				margin-right:20px;
				border:thin soild green;
				width:260px;
			 }
		 </style>
		 <%@include file="/module/bsf/bsf/comm.jsp"%>
		 <script type="text/javascript" src="${ctxPath}/module/crm/csttype/csttype_billTemplate.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/crm/csttype/csttype_billTemplate.js")%>"></script> 
		 <script type="text/javascript" src="${ctxPath}/module/crm/csttype/csttype.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/crm/csttype/csttype.js")%>"></script>
		 <title></title>
	</head>
	<body>
		 <div id="cc" class="easyui-layout" data-options="fit:true">
			<div id="btns_panel" data-options="region:'north',split:false" style="height:40px;border-top:0px;background:rgb(253,253,255);">
				<!-- 按钮面板--> 
			</div>

			<div data-options="region:'west',split:true" title="查询面板" style="width:350px;">
				<div id="queryPanel" class="easyui-panel" data-options="fit:true" style="padding-top:400;text-align:center;padding:0px;border:none;background:rgb(253,253,255)">
					 
				</div>
			</div>
			<div id="billContainer" data-options="region:'center'" style="contentEditable:false,margin:0 0 0 0;">
				<div  id="cardPanel"  class="easyui-panel" style="margin:0px;border:0px;width:100%;height:100%" data-options="fit:true,width:'100%',height:'100%'"></div> 
				<div  id="listPanel"  class="easyui-panel" style="margin:0px;border:0px;width:100%;height:100%" data-options="fit:true"></div> 
			</div>
		 </div>
	</body>
	<script> 
		var cxtPath = "${ctxPath}/facade.do";  
	</script>
</html>
