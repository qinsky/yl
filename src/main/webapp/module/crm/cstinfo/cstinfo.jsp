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
		 <script type="text/javascript" src="${ctxPath}/module/crm/cstinfo/cstinfo_billTemplate.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/crm/cstinfo/cstinfo_billTemplate.js")%>"></script> 
		  <script type="text/javascript" src="${ctxPath}/module/crm/cstinfo/cstinfo.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/crm/cstinfo/cstinfo.js")%>"></script>  
		 <title></title>
	</head>
	<body> 
		 <div id="cc" class="easyui-layout" data-options="fit:true">
			<div id="btns_panel" data-options="region:'north',split:false" style="text-align:center;height:40px;border-top:0px;background:rgb(253,253,255);">
				<!-- 按钮面板--> 
			</div>

			<div data-options="region:'west',split:true" title="查询面板" style="width:350px;">
				<div id="queryPanel" class="easyui-panel" data-options="fit:true" style="padding-top:400;text-align:center;padding:0px;border:none;background:rgb(253,253,255)">
					 
				</div>
			</div>
			<div id="billContainer" data-options="region:'center'" style="contentEditable:false,margin:0 0 0 0;padding:0px">
				<div  id="cardPanel"  class="easyui-panel" style="margin:0px;padding:0px;border:0px;width:100%;height:100%;" data-options="fit:true,width:'100%',height:'100%'"></div> 
				<div  id="listPanel"  class="easyui-panel" style="margin:0px;border:0px;width:100%;height:100%" data-options="fit:true"></div> 
			</div>
		 </div>
 
		 <div id="traceDialog" style="text-align:center;padding-top:20px"	class="easyui-dialog" data-options="modal:true,closed:true,title:'添加跟进记录',width:460,height:360" >
		 	<form id="traceForm">
		 		<!-- 
				<div>
			 		<input class="easyui-datetimebox" id="traceTime" data-options="validateOnCreate:false,label:'跟进时间:',width:380,labelPosition:'left',labelWidth:80,required:true" />
			 	</div> -->
			 	<div>
			 		<input class="easyui-textbox" id="traceContent" data-options="validateOnCreate:false,label:'跟进内容:',width:380,height:220,multiline:true,labelPosition:'left',labelWidth:80,required:true" />
			 	</div>
			 	<a class="easyui-linkbutton" id="btnSaveTrace" data-options="onClick:saveTrace" style="margin-top:20px;width:80px" >添  加</a>
		 	</form>
		 </div> 
			<div id="contentMM" class="easyui-menu" style="width:120px;">
	        </div> 
	          
	</body>
	<script> 
		var cxtPath = "${ctxPath}/facade.do"; 
	</script>
</html>
