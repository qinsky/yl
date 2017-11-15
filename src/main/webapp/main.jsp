<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html>
	<head>
		<%@include file="./module/bsf/comm.jsp"%>
		<style>
			.level2-selected-menu{
				font-size:16px;
				text-decoration:underline; 
				color:blue;
				cursor:pointer;
				/**border-bottom:1px solid blue;*/
				
			}
			.level2-menu{
				font-size:15px;
				text-decoration:none;
				color:black;
				cursor:pointer;
				margin-bottom:4px;
			}
		.menustyle{
			column-width: 160px;
			word-wrap:none;
			-moz-column-gap:normal;
			-webkit-column-gap:normal;
			column-rule:1px solid gray;
			column-gap:normal;
			position:absolute; 
			height:98%;
			text-align:left;
			padding-left:10px;
		 }
		 .detail-menu-ul{
		 	padding-left:0px;
		 	margin-left:6px;
		 	font-size:16px;
		 	font-weight:500;
		 	color:rgb(69,131,192);
		 }
		 .detail-menu-label{ 
		 	margin-left:8px; 
		 	font-size:14px;
		 	color:black;
		 	font-weight:normal;
		 	text-align:center; 
		 }
		 .detail-menu-label-selected{
		 	font-size:15px;
		 	color:blue;
		 	cursor:pointer;
		 	margin-left:9px; 
		 	text-decoration:underline; 
		 }
		 .img{
		 	vertical-align:sub;
		 }
		</style>
		 <title></title>
	</head>
	<body>
		<div  class="easyui-layout" data-options="fit:true"> 
			<div data-options="region:'north'" style="height:60px"></div>
			<div data-options="region:'center'">  
				<div class="easyui-tabs" id="mainTabs" data-options="fit:true,narrow:true,tabHeight:30" style="padding:0px;">
					<div title="功能导航"  data-options="fit:true" style="padding:10px;margin:0px;padding:0px;">
				 		<div id="menu" class="easyui-layout" data-options="fit:true"> 
							<div data-options="region:'west',split:true"  style="width:25%;border:0px;margin:0px;padding:0px;">
							 
								 <div id="menuTab" class="easyui-tabs" data-options="fit:true,narrow:false,tabPosition:'left',tabHeight:30" style="margin:0px;padding:0px;">
									<div title="常用功能" data-options="" style="margin-top:4px;align-text:center">
									 
									</div>
								 </div>
							 
							</div>
							<div  data-options="region:'center',split:true">
								<div id="detailMenu"  class="menustyle" >
									
								</div>
							</div>
						</div>  	   
					</div>
					<div title="消息中心"  style="padding:10px">
						Hello everybody! this is Messager Center. 
					</div> 
				</div>
			</div>
		 </div> 
		
	</body>
	
	<script >
		var cxtPath = "${ctxPath}/facade.do"; 
		ul.appctx.initial(cxtPath,"auth");
		
		var menu;
		ul.appctx.initial();
		var queryParam = {qq9214_bt:"auth",qq9214_at:"menu"};
		ul.appctx.sendRequest(queryParam,{success:function(rspData){
			ul.appctx.validRsp(rspData);
			menu = rspData.busiData;
			console.log(menu);
		}});
		//var treeData = ul.view.tree.generalTreeData(obj.menu,"id","title","parentobject","id");
		var treeData = ul.view.tree.generalTreeData(menu,"id","name","parent_id","id");
		console.log(treeData);
		
		function generalMenu(){
			var menuTab = $("#menuTab"); 
			var options = null;
			for(var i=0;i<treeData.length;i++)
			{
				//生成一级以及二级菜单
				options = {title:treeData[i].text};  
				if(treeData[i].children)
				{
					options.content = "<ul>"
					for(var j=0;j<treeData[i].children.length;j++)
					{
						options.content += "<li class='level2-menu' id='"+treeData[i].children[j].id+"'>"+treeData[i].children[j].text+"</li>"
					}
					options.content += "</ul>" 
				}
				menuTab.tabs('add',options); 
				
				//每个菜单需要绑定跟自己相关的记录
				if(treeData[i].children)
				{
					$.each(treeData[i].children,function(index,obj){
						var menu = $("#"+obj.id); 
						menu.data("bean",obj); 
						menu.bind("mouseover",function(){$(this).removeClass("level2-menu"),$(this).addClass("level2-selected-menu")});
						menu.bind("mouseout",function(){$(this).addClass("level2-menu"),$(this).removeClass("level2-selected-menu")});
						menu.bind("click",showDetailMenu);
					});
				} 
			} 
			
			menuTab.tabs({onSelect:function(){
				$("#detailMenu").empty();
			}});
		}
		
		var curMenuId = null;
		function showDetailMenu()
		{
			var bean = $(this).data("bean");
			if(curMenuId==bean.id){
				return;
			}
			var detailMenu = $("#detailMenu");
			detailMenu.empty();
			
			var strHtml = generalDetailMenu(bean);
		 
			detailMenu.append(strHtml);
			
			var menu = $(".detail-menu-label");
			menu.bind("mouseover",function(){$(this).removeClass("detail-menu-label"),$(this).addClass("detail-menu-label-selected")});
			menu.bind("mouseout",function(){$(this).addClass("detail-menu-label"),$(this).removeClass("detail-menu-label-selected")});
			menu.bind("dblclick",openNode); 
		}
		
		function generalDetailMenu(bean)
		{
			if(bean.children){
				var strHtml = "<ul class='detail-menu-ul'>"+bean.text;
				for(var i=0;i<bean.children.length;i++){
					if(bean.children[i].children)
					{
						strHtml += generalDetailMenu(bean.children[i]);
					}else
					{
						strHtml += "<br><label class='detail-menu-label' id='"+bean.children[i].id+"'><image class='img' src='bnexec.gif' />"+bean.children[i].text+"</label>";
					}
				}
				strHtml += "</ul>"
				return strHtml;
			} 
			return "";
		}
		
		function openNode()
		{
			var mainTabs = $("#mainTabs");
			var tabs = mainTabs.tabs('tabs');
			
			//判断是否已经打开过，如果已经打开过则直接显示已经打开的tab
			var isOpened = false;
			var curId = this.id;
			$.each(tabs,function(index,obj){ 
				var options = $(obj).panel('options');
				if(options.id==curId)
				{
					isOpened = true;
					mainTabs.tabs('select',index);
				}
			});
			if(isOpened)
			{
				return;
			} 
			var path = cxtPath+"?qq9214_bt=auth&qq9214_at=openMenu&menu_id="+this.id;
			var content = "<div class='easyui-panel' data-options='fit:true' style='border:0px;overflow:hidden;padding:0px;margin:0px;'>";
				content += '<iframe contxt="aaa" style="height:100%;width:100%;margin:0px;padding:0px;frameborder:no;border:0px" src="'+path+'"></iframe>';
				content += "</div>";
			var options = {title:$(this).text(),content:content,id:this.id,closable:true,style:{overflow:'hidden',padding:'0px',margin:'0px'}};
			console.log($("#mainTabs"));
			console.log(options);
			mainTabs.tabs("add",options);
			
			
		}
		
		$(generalMenu);
	</script>
</html>		