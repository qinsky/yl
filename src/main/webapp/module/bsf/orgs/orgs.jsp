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
				margin-left:0px;
				border:thin soild green; 
			 }
		 </style>
		 <%@include file="/module/bsf/bsf/comm.jsp"%>
		 <script type="text/javascript" src="${ctxPath}/module/bsf/orgs/orgs_billTemplate.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/orgs/orgs_billTemplate.js")%>"></script> 
		 <script type="text/javascript" src="${ctxPath}/module/bsf/orgs/orgs.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/orgs/orgs.js")%>"></script>
		 <title></title>
	</head>
	<body>
		 <div id="cc" class="easyui-layout" data-options="fit:true">
			<div id="btns_panel" data-options="region:'north',split:false" style="height:40px;border-top:0px;background:rgb(253,253,255);">
				<!-- 按钮面板-->
			</div>

			<div data-options="region:'west',split:true" title="<span style='font-size:15px'>组织结构</span>" style="width:300px;">
				<div class="easyui-panel" data-options="fit:true" style="padding:5px;border:none;background:rgb(253,253,255)">
					<ul class="easyui-tree" id="tt" ></ul>
				</div>
			</div>
			<div id="billContainer" data-options="region:'center'" style="contentEditable:false">

			</div>
		 </div>
		 
		<div id='alloOrgs' data-options='modal:true,title:"分配组织",closed:true' style='display:none;padding:0px;margin:0px' >
			 <ul id='alloOrgTree' class='easyui-tree' data-options='lines:true'></ul>
		</div>   
		 
	</body>
	<script> 
		var cxtPath = "${ctxPath}/facade.do"; 
	  
	</script>
</html>
