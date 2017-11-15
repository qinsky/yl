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
				/**width:360px;*/
			 }
		 </style>
		 <%@include file="/module/bsf/bsf/comm.jsp"%>
		 <script type="text/javascript" src="${ctxPath}/module/bsf/meta/meta_billTemplate.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/meta/meta_billTemplate.js")%>"></script> 
		 <script type="text/javascript" src="${ctxPath}/module/bsf/meta/meta.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/meta/meta.js")%>"></script>
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
		 
	 
		<div id="editMetaDialog" class="easyui-dialog" data-options="modal:true,width:'50%',height:'80%',title:'编辑元数据',closed:true,buttons:[{text:'确定',iconCls:'icon-ok',handler:mtOpe.ok},{text:'取消',iconCls:'icon-cancel',handler:mtOpe.cancel}]" >
			<ul id="metaTree"></ul> 
		</div>
		
		<div id="ctxMenu" class="easyui-menu" style="width:120px;">
			<div onclick="mtOpe.appendMetaEle()" data-options="iconCls:'icon-add'">增加</div>
			<div onclick="mtOpe.removeMetaEle()" data-options="iconCls:'icon-remove'">删除</div> 
			<!-- 
			<div class="menu-sep"></div>
			<div onclick="expand()">Expand</div>  
			 -->
		</div> 
		<div id="dialog_add_meta_ele" data-options="modal:true,width:340,height:220,title:'增加元素',closed:true" style="display:none;padding-top:16px;text-align:center;" > 
			<div> 
				<select class="easyui-combobox" id="meta_ele_type" data-options="label:'元素类型',width:260,height:30,labelPosition:'left',labelWidth:80" > 
				<option value="object">object</option>  
				<option value="array">array</option> 
				<option value="string">string</option> 
				<option value="boolean">boolean</option> 
				<option value="number">number</option> 
				</select> 
			</div> 
			<div> 
				<input  class="easyui-textbox" id="meta_ele_name" data-options="label:'元素名称',width:260,height:30,labelPosition:'left',labelWidth:80" /> 
			</div> 
			<div> 
				<input class="easyui-textbox" id="meta_ele_value" data-options="label:'元素值',width:260,height:30,labelPosition:'left',labelWidth:80" /> 
			</div>  
			<a class="easyui-linkbutton" id="btn_add_ele" data-options="onClick:mtOpe.addMetaEle" style="margin-top:20px;width:80px" >确定</a> 
			<a class="easyui-linkbutton" id="btn_add_cancel" data-options="onClick:mtOpe.addMetaCancel" style="margin-top:20px;width:80px" >取消</a> 
		</div>  
	 
	</body>
	<script> 
		var cxtPath = "${ctxPath}/facade.do"; 	  
	</script>
</html>
