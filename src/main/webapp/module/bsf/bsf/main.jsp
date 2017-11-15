<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html>
	<head>
		<%@include file="comm.jsp"%>
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
			<div data-options="region:'north'" style="padding:16px;height:60px;background-color:rgb(232,242,254);text-align:right;"  >
				当前用户:${loginInfo.userName}&nbsp;&nbsp;<a href="javascript:openChangePs();">修改密码</a>&nbsp;&nbsp;<a href="javascript:loginOut();">注销</a>
			</div>
			<div data-options="region:'center'">  
				<div class="easyui-tabs" id="mainTabs" data-options="fit:true,narrow:true,tabHeight:30" style="padding:0px;margin:0px;">
					<div title="功能导航"  data-options="fit:true" style="padding:10px;margin:0px;padding:0px;">
				 		<div id="menu" class="easyui-layout" data-options="fit:true"> 
							<div data-options="region:'west',split:true"  style="width:25%;border:0px;margin:0px;padding:0px;">
							 
								 <div id="menuTab" class="easyui-tabs" data-options="fit:true,narrow:false,tabPosition:'left',tabHeight:30" style="margin:0px;padding:0px;">
									<!-- 
									<div title="常用功能" data-options="" style="margin-top:4px;align-text:center"> 
									</div> 
									-->
								 </div>
							 
							</div>
							<div  data-options="region:'center',split:true">
								<div id="detailMenu"  class="menustyle" >
									
								</div>
							</div>
						</div>  	   
					</div>
					<div title="工作中心"  style="padding:0px;margin:0px">
						<table id="tblUserTask" class="easyui-datagrid" title="代办任务"  style="width:50%;height:100%" data-options="singleSelect:true,collapsible:true">
           					 <thead>
					            <tr>
					            	<th data-options="field:'menuName',">单据名称</th>
					                <th data-options="field:'name',">任务名称</th>
					                <th data-options="field:'create_time'">创建时间</th>
					                <th data-options="field:'options',align:'center'">操&nbsp;&nbsp;作</th> 
					            </tr>
					        </thead>
           				</table>
					</div> 
				</div>
			</div>
		 </div> 
		  
		<div id='chgPsD' data-options='modal:true,width:340,height:220,title:"修改密码",closed:true' style='display:none;padding-top:16px;text-align:center;' >
			<div>
		 		<input class="easyui-passwordbox" id="ops" data-options="validateOnCreate:false,label:'旧密码',width:260,height:30,labelPosition:'left',labelWidth:80,required:true" />
		 	</div>
		 	<div>
		 		<input class="easyui-passwordbox" id="nps" data-options="validateOnCreate:false,label:'新密码',width:260,height:30,labelPosition:'left',labelWidth:80,required:true" />
		 	</div>
		 	<div>
		 		<input class="easyui-passwordbox" id="nps2" data-options="validateOnCreate:false,label:'确认新密码',width:260,height:30,labelPosition:'left',labelWidth:80,required:true" />
		 	</div>
		 	
		 	<a class="easyui-linkbutton" id="btnChangePs" data-options="onClick:changeps" style="margin-top:20px;width:80px" >确定</a>
		</div>   
	</body>
	
	<script >
		var root = "${ctxPath}";
		var cxtPath = root+"/facade.do"; 
		ul.appctx.initial(cxtPath,"auth");
		
		var menu; 
		var queryParam = {qq9214_bt:"auth",qq9214_at:"menu"};
		var treeData = null;
		function getMenu()
		{
			ul.appctx.sendRequest(queryParam,{success:function(rspData){
				ul.appctx.validRsp(rspData);
				menu = rspData.busiData;
				treeData = ul.view.tree.generalTreeData(menu,"id","name","parent_id","id");
				generalMenu();
			}}); 
		}
		 
	 
		function loginOut()
		{
			$.messager.confirm("确认","确认注销吗？",function(r){
				if(r)
				{
					var queryParam = {qq9214_bt:"login",qq9214_at:"loginOut"};
					ul.appctx.sendRequest(queryParam,{success:function(rspData){
						
						ul.appctx.validRsp(rspData); 
						
						window.location = root+"/index.jsp";
					}}); 
				}
			}); 
		};
		
		function openChangePs()
		{ 
			$("#chgPsD").dialog({closed:false}); 
		};
		
		function changeps(){
			
			var ops = ul.utils.trim($("#ops").textbox("getValue"));
			var nps = ul.utils.trim($("#nps").textbox("getValue"));
			var nps2 = ul.utils.trim($("#nps2").textbox("getValue"));
			if(ops==""){
				$.messager.alert("错误","就密码不能为空!","error");
				return;
			}
			if(nps==""){
				$.messager.alert("错误","新密码不能为空!","error");
				return;
			}
			
			if(nps.length<6){
				$.messager.alert("错误","新密码长度不能小于六位!","error");
				return;
			}
			
			if(nps2==""){
				$.messager.alert("错误","确认新密码不能为空!","error");
				return;
			}
			
			if(nps2!=nps)
			{
				$.messager.alert("错误","新密码与确认新密码不一致!","error");
				return;
			}
			
			var queryParam = {qq9214_bt:"auth",qq9214_at:"modifyPs",field1:ops,field2:nps};
			ul.appctx.sendRequest(queryParam,{success:function(rspData){
				
				ul.appctx.validRsp(rspData);  
				
				$("#chgPsD").dialog({closed:true});
				
				$.messager.alert("提示",rspData.busiData,"info");
			}}); 
			
		};
		
		
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
						if(obj.type=="2")
						{
							menu.bind("click",openNode);
						}else
						{
							menu.bind("click",showDetailMenu);
						}
						
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
			
			var strHtml = generalDetailMenu(bean,true);
		 
			detailMenu.append(strHtml);
			
			var menu = $(".detail-menu-label");
			menu.bind("mouseover",function(){$(this).removeClass("detail-menu-label"),$(this).addClass("detail-menu-label-selected")});
			menu.bind("mouseout",function(){$(this).addClass("detail-menu-label"),$(this).removeClass("detail-menu-label-selected")});
			menu.bind("click",openNode); 
		}
		
		function generalDetailMenu(bean,flag)
		{
			if(bean.children){
				var strHtml = flag?"":"<ul class='detail-menu-ul'>"+bean.text;
				for(var i=0;i<bean.children.length;i++){
					if(bean.children[i].children)
					{
						strHtml += generalDetailMenu(bean.children[i]);
					}else
					{
						strHtml += "<br><label class='detail-menu-label' id='"+bean.children[i].id+"'><image class='img' src='bnexec.gif' />"+bean.children[i].text+"</label>";
					}
				}
				strHtml += flag?"":"</ul>"
				return strHtml;
			} 
			return "";
		}
		
		function openNode(param)
		{
			param = param?param:{};
			
			var mainTabs = $("#mainTabs");
			var tabs = mainTabs.tabs('tabs');
			
			//判断是否已经打开过，如果已经打开过则直接显示已经打开的tab
			var isOpened = false;
			var curId = param.menuId?param.menuId:this.id;
			
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
				if(param.menuId)
				{
					//自动加载要处理的单据
					var ifw = document.getElementById("menu_"+curId).contentWindow;
					ifw.ul.model.fireEvent("link_query",{ids:[param.billId]});
					//ifw.ul.model.initIds([param.billId]);
				}
				return;
			} 
			
			var title = param.menuName?param.menuName:$(this).text();
			var path = cxtPath+"?qq9214_bt=auth&qq9214_at=openMenu&menu_id="+curId;
			if(param.billId)
			{
				path += "&iniBillId="+param.billId;
			}
			
			var content = "<div class='easyui-panel' data-options='fit:true' style='border:0px;overflow:hidden;padding:0px;margin:0px;'>";
				content += '<iframe id="menu_'+curId+'" style="height:100%;width:100%;margin:0px;padding:0px;frameborder:no;border:0px;" src="'+path+' "/>';
				content += "</div>";
			var options = {title:title,content:content,id:curId,closable:true,style:{overflow:'hidden',padding:'0px',margin:'0px'}};
			mainTabs.tabs("add",options); 
			
			var panel = mainTabs.tabs("getSelected"); 
		}
		//初始化工作中心
		function iniCenter(){
			//加载任务消息
			var sendParam = {qq9214_bt:"process",qq9214_at:"qusertasks"};
			ul.appctx.sendRequest(sendParam,{success:function(rspData){
				ul.appctx.validRsp(rspData);
				var beans = rspData.busiData; 
				for(var i=0;i<beans.length;i++)
				{ 
					beans[i].options = "<a href='javascript:;' onclick='doTask(\""+beans[i].id+"\")'>处&nbsp;&nbsp;理</a>";
				}
			    $("#tblUserTask").datagrid("loadData",rspData.busiData); 
			}});
		};
		
		function doTask(id)
		{
			var rows = $("#tblUserTask").datagrid("getData").rows;
			var row = null;
			for(var i=0;i<rows.length;i++)
			{
				if(rows[i].id==id)
				{
					row = rows[i];
					break;
				}
			}
			openNode(row);
		};
		
		$(getMenu);
		$(iniCenter);
	</script>
</html>		