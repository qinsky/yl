<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE>
<html>
  <head>
    <title>首页</title>
    <meta charset=utf-8">
	 <%@include file="../bsf/comm.jsp"%>
    <style type="text/css">
    	#header{
    		position:absolute;
    		left:0px;
    		top:0px;
			width: 100%;
    		min-width: 800px;
    		height: 40px;
    		background-color: #8DB6CD;
    	}
		#tabs{
			position:absolute;
    		left:0px;
    		top:40px;
			right:0px;
			width: 100%;
    		min-width: 800px;
			height:90%;
    		min-height:500px;
			
		}
		.divbg{
    		background-color: rgb(224,236,255);
    	}
		.menustyle{
			column-width: 200px;
			word-wrap:none;
			-moz-column-gap:normal;
			-webkit-column-gap:normal;
			column-rule:1px solid gray;
			column-gap:normal;
			position:absolute;
			left:320px;
			height:95%;
			text-align:left;
		}
		.menufont{
			/**padding:3px;*/
			font-size: 16;
		}
		.menudiv{
			position:absolute;
				/**left:150px;
			top:30px;*/
			width:160px;
			height:100%;
			min-height:500px;
			overflow:auto;
			text-align:left;
			border-right:2px double RGB(128,128,128);
			/**background-color:#E0FFFF;
			border-color:#E0FFFF;*/
			margin-left:0px;
			margin-top:5px;
			font-size:16px;
		}
		.menu-div-div{
		 	padding:3px; 
		}
		
    </style>

	<script type="text/javascript">
			var obj={"menu":[{"title":"财务系统系统","id":"ccs","level":"1","parentobject":"","isfunction":"Y"},{"title":"单据类型","id":"jcsj-djlx","level":"3","parentobject":"jcsj","isfunction":"Y"},{"title":"基础数据","id":"jcsj","level":"2","parentobject":"ccs","isfunction":"Y"},{"title":"费用报销","id":"rbsm","level":"2","parentobject":"ccs","isfunction":"Y"},{"title":"合同管理","id":"htgl","level":"2","parentobject":"ccs","isfunction":"Y"},{"title":"合同结算","id":"htjs","level":"3","parentobject":"htgl","isfunction":"Y"},{"title":"合同登记","id":"htdj","level":"3","parentobject":"htgl","isfunction":"Y"},{"title":"维修费报销","id":"wxfbx","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx1","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx2","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx3","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx4","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx5","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx6","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx7","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx8","level":"3","parentobject":"rbsm","isfunction":"N"},{"title":"接待费报销","id":"jdfbx8-1","level":"4","parentobject":"jdfbx8","isfunction":"N"},{"title":"接待费报销","id":"jdfbx8-1-1","level":"5","parentobject":"jdfbx8-1","isfunction":"N"},{"title":"接待费报销","id":"jdfbx8-1-1-1","level":"6","parentobject":"jdfbx8-1-1","isfunction":"N"},{"title":"接待费报销","id":"jdfbx8-1-1-1-1","level":"7","parentobject":"jdfbx8-1-1-1","isfunction":"N"},{"title":"接待费报销","id":"jdfbx8-1-1-1-1-1","level":"8","parentobject":"jdfbx8-1-1-1-1","isfunction":"N"},{"title":"接待费报销","id":"jdfbx8-1-1-1-1-1-1","level":"9","parentobject":"jdfbx8-1-1-1-1-1","isfunction":"N"},{"title":"接待费报销","id":"jdfbx8-1-1-1-1-1-1-1","level":"10","parentobject":"jdfbx8-1-1-1-1-1-1","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx9","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx10","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx11","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx12","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx13","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx14","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx15","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx16","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx17","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx18","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx19","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx20","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx21","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx22","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx23","level":"3","parentobject":"rbsm","isfunction":"N"},{"title":"接待费报销","id":"jdfbx23-1","level":"4","parentobject":"jdfbx23","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx23-2","level":"4","parentobject":"jdfbx23","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx23-3","level":"4","parentobject":"jdfbx23","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx23-4","level":"4","parentobject":"jdfbx23","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx24","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx25","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx26","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx27","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx28","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx29","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx30","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx31","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx32","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx33","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx34","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx35","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx36","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx37","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx38","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx39","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx40","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx41","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx42","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx43","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx44","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx45","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx46","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx47","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx48","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx49","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx50","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"接待费报销","id":"jdfbx51","level":"3","parentobject":"rbsm","isfunction":"Y"},{"title":"差旅报销","id":"clbx","level":"3","parentobject":"rbsm","isfunction":"Y"}]};
			
var tree = generalTreeData(obj.menu,"id","title","parentobject","id");
function load(){	//页面加载
	$("#sidebar_left").tabs({narrow:true});		
	
	for(j = 0; j < tree.length; j++) {
		var id = tree[j].id;
		var title = tree[j].text;
		$("#sidebar_left").tabs("add",{title:title,content:"<div id="+id+" class='menudiv'></div>"}); /**<div id="+id+"content class='menustyle'></div>*/
		
		if(tree[j].children){
			var children = tree[j].children;
			for(i = 0; i < children.length;i++){
				$("#"+id).append("<div id="+children[i].id+" class='menu-div-div' onclick='JavaScript:ChangeDiv(this);' >"+children[i].text+"</div>");
				if(i==0){loadNode(children[i])};
			}
		}
	}
}

function loadItem(item){
	for(j = 0; j < tree.length; j++) {		
		var dparentid = $("#"+item.id).parent().attr("id");
		if (dparentid == tree[j].id){
			if(tree[j].children){
				var children = tree[j].children;
				for(i = 0; i < children.length;i++){
					if(item.id==children[i].id){loadNode(children[i])};
				}
			}
		}
	}
}

var level = 0;
function traverse(tree,dparentid) {//加载功能节点
	var nodes = tree.children;
	if(nodes != null) {
		for(var  i = 0;  i < nodes.length;  i++) {
			if(level==0){
				$("#"+dparentid).append("<div id="+nodes[i].id+">"+nodes[i].text+"</div>");
				if(nodes[i].attributes.isfunction=="Y"){$("#"+nodes[i].id).css({"font-size":"18px","padding":"0 5px"})}
				else{$("#"+nodes[i].id).css({"font-size":"18px","padding":"0 5px","font-weight":"bold"})}
			}else{
				$("#"+nodes[i].attributes.parentobject).append("<div id="+nodes[i].id+">"+nodes[i].text+"</div>");
				var pleft = level*5+5; var fontsize = 16;//25-(level*2);
				if(nodes[i].attributes.isfunction=="Y"){$("#"+nodes[i].id).css({"font-size":fontsize+"px","padding":"0 5px 0 "+pleft+"px"})}
				else{$("#"+nodes[i].id).css({"font-size":fontsize+"px","padding":"0 5px 0 "+pleft+"px","font-weight":"bold"})}
			}
			if(nodes[i].children) {
				level++;
				traverse(nodes[i]);
			}
		}
		level = 0;
	}
}
function generalTreeData(datas,strId,strName,parentId,childId,curObj){		//生成菜单树
	var tmpObj = null;
	if(curObj)
	{
		for(var i=0;i<datas.length;i++)
		{
			var parentValue = datas[i][parentId];
			if(parentValue==curObj[childId])
			{ 
				tmpObj = {id:datas[i][strId],text:datas[i][strName],attributes:datas[i]};
				if(!curObj["children"])
				{
					curObj["children"] = [];
				}
				curObj["children"].push(tmpObj);
				generalTreeData(datas,strId,strName,parentId,childId,tmpObj);
			}
		}
	}else{
		var tree = [];
		for(var i=0;i<datas.length;i++)
		{
			if(datas[i][parentId]==""||datas[i][parentId]==null)
			{
				tmpObj = {id:datas[i][strId],text:datas[i][strName],attributes:datas[i]};
				tree.push(tmpObj); 
				generalTreeData(datas,strId,strName,parentId,childId,tmpObj);
			}	
		}
		return tree;
	}
};
function loadNode(data)
{
	var c = "content";
	var dparentid = $("#"+data.id).parent().attr("id");
	$("#"+dparentid).children("div").each(function(i,item){
		$("#"+item.id).removeClass("divbg");
	});
	$("#"+data.id).addClass("divbg");
	$("#"+dparentid+c).empty();
	traverse(data,dparentid+c);
}
function ChangeDiv(t)
{
	loadItem(t);
}
$(document).ready(load);
	</script>

  </head>
  
  <body">
    <div id="header">
    </div>
	<div id="tabs" class="easyui-tabs">
		<div title="菜单导航" id="navigation">
			<div id="sidebar_left" class="easyui-tabs" tabPosition="left">
				<div title="常用功能">
				</div>
			</div>
		</div>
		<div title="帮助">
							<label>电话号码</label></p>
		</div>
	</div>	
  </body>
</html>
