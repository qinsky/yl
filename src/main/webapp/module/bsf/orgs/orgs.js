function initial()
{
	initialFields();
	initialBtn(); 
	initialListener(); 
	initialData();
	
};

function initialFields()
{ 
	ul.appctx.initial(cxtPath,"orgs");
	ul.appctx.attrs["meta"]=billTemp;
	ul.view.generalFields("billContainer",billTemp);
	ul.model.disableBill(true);
	
	var menu = $("#ul_children_menu");  
	var options = menu.datagrid("options");  
	menu.treegrid({idField:"id",treeField:"text",rownumbers:true,fit:true,lines:true,toolbar:options["toolbar"]});
	
	$("#ul_card_children_tabs").hide(); 
	$("#billContainer").find(".easyui-layout").layout("unsplit","south"); 
}; 

function initialData()
{
	var busiType = ul.appctx.attrs["qq9214_bt"];
	var queryParams = {qq9214_bt:busiType,qq9214_at:"query"};
	 
	ul.appctx.sendRequest(queryParams,{dataType:"json",success:function(rspData){
	 
		ul.appctx.validRsp(rspData);
		
		var orgsData = rspData.busiData;
		ul.model.initData(orgsData); 
		
		var menu = ul.view.tree.generalTreeData(orgsData,"id","name","parent_id","id");
		
		$('#tt').tree({
		data:menu ,
		lines:true,
		onClick:function(item){ 
			if(ul.view.status!="default")
			{
				return;
			}
			ul.model.select.call(this,item["attributes"]);
		}
		});  
	}}); 
	ul.view.setStatus("default"); 
};

function initialListener()
{
		ul.model.addListener("data_selected",function(eventType,data){
		  
		if(!data["bean"])
		{
			ul.model.setBillValue(data["bean"]);
			return;
		}
		
		if(data["bean"].type=="5")
		{
			$("#billContainer").find(".easyui-layout").layout("split","south"); 
			$("#ul_card_children_tabs").show();
		}else
		{
			$("#billContainer").find(".easyui-layout").layout("unsplit","south");  
			$("#ul_card_children_tabs").hide();
		} 
		
		//设置选中数据
		ul.model.setBillValue(data["bean"]);
		
		//显示授权的菜单
		var menu = ul.model.getChildrenData("menuTree");
		if(menu)
		{
			menu = ul.view.tree.generalTreeData(menu,"id","name","parent_id","id"); 
			$("#ul_children_menu").treegrid({data:menu}); 
			$("#ul_children_menu").treegrid("collapseAll");
		}
		
		
		//隐藏没有记录提示
		var divEmpty = $("#ul_children_menu").parent().find(".datagrid-empty");
		if(!menu || menu.length==0)
		{
			divEmpty.show(); 	
		}else
		{
			divEmpty.hide();
		} 
	});
		
	ul.model.addListener("data_updated",function(eventType,data){
		
		if(data.bean && (ul.model.selIndex!=-1))
		{
			if(data.bean.id = ul.model.getSelBean()["id"])
			{
				ul.model.setBillValue(data.bean);
			}
		}
	});
		
	function beforeValid()
	{
		if(ul.view.status!="add" && ul.view.status!="edit")
		{
			$.messager.alert("错误","非编辑状态不能增加或删除行!");
			return true;
		}
	};
	ul.view.datagrid.handlerMap["before-ul_children_menu-addRow"] = beforeValid;
	ul.view.datagrid.handlerMap["before-ul_children_user-addRow"] = beforeValid;
	ul.view.datagrid.handlerMap["before-ul_children_menu-delRow"] = beforeValid;
	ul.view.datagrid.handlerMap["before-ul_children_user-delRow"] = beforeValid;
	
	ul.view.datagrid.handlerMap["ul_children_menu-allocate"] =  allocate;
	
	ul.view.datagrid.handlerMap["ul_children_orgs-alloOrgs"] =  alloOrgs;
}

function initialBtn()
{
	var btns = [{id:"add",qq9214_at:"add",name:"增加",enableStatus:["default"]},
	            {id:"update",qq9214_at:"update",name:"修改",enableStatus:["default"]},
	{id:"save",qq9214_at:"save",name:"保存",enableStatus:["add","edit"]},
	{id:"cancel",qq9214_at:"cancel",name:"取消",enableStatus:["add","edit"]},
	{id:"delete",qq9214_at:"delete",name:"删除",enableStatus:["default"]}, 
	{id:"refresh",qq9214_at:"refresh",name:"刷新",enableStatus:["default"]}
	];
	/** {"id":"test3","qq9214_at":"delete1","name":"删除1","parent_id":"test2"} */
	
	ul.view.initialBtns(btns);

	
	var vReadonly  = true;
	function addHandler(param){
		 
		if(ul.view.status!="default")
		{
			$.messager.alert("提示","当前状态无法新增!","info");
			return;
		}
		ul.model.cleanBill();
		ul.model.disableBill(false);  
		if(ul.model.selIndex!=-1)
		{
			var tv = new ul.view.textValuePair(ul.model.getSelBean()["id"],ul.model.getSelBean()["name"])
			ul.model.setFieldValue("parentid",tv);
		}
		ul.view.setStatus("add");
		
	};
	ul.view.addBtnHandler("add",addHandler);
	
	function saveHandler(param){
		 
		if(ul.view.status!="add"&&ul.view.status!="edit")
		{
			console.log(ul.view.status+" 没有保存的数据!");
			return ;
		}
		
		var sendData = {};
		sendData["qq9214_bt"] = ul.appctx.attrs["qq9214_bt"];
		sendData["qq9214_at"] = ul.view.status=="add"?"save":"update";
		sendData["billData"] = ul.model.getBillValue({isTextValue:false});
		ul.appctx.sendRequest(sendData,{dataType:'json',success:function(rspData){

			ul.appctx.validRsp(rspData);
			var obj = rspData.busiData;
			
			if(ul.view.status=="add")
			{
				ul.model.insert(obj);
				
			}else
			{
				ul.model.update(obj);
				
			} 
			
			var menu = ul.view.tree.generalTreeData(ul.model.data,"id","name","parent_id","id");
			var tt = $("#tt");
			tt.tree({data:menu});
			
			var node = tt.tree('find', obj["id"]);
			$('#tt').tree('select', node.target);
			
			ul.view.setStatus("default");
			ul.model.disableBill(true); 
			
		},error:function(){
			$.messager.alert("失败","服务器异常!","info");
			ul.model.disableBill(false);
		}});
	};
	ul.view.addBtnHandler("save",saveHandler);
	
	function delHandler(param){ 
		$.messager.confirm('确认','确定要删除这条记录吗?',function(r){
		    if (r){
		    	var selNode = $("#tt").tree("getSelected");
		    	if(selNode["children"])
	    		{
		    		$.messager.alert("错误","请先删除下级节点!","error");
		    		return;
	    		}
		    	
		    	var sendData = {};
				sendData["qq9214_bt"] = ul.appctx.attrs["qq9214_bt"];
				sendData["qq9214_at"] = "delete";
				sendData["billData"] = ul.model.getBillValue();
				
				ul.appctx.sendRequest(sendData,{success:function(rspData){
					//$.messager.alert("信息","操作成功!","info");
					ul.appctx.validRsp(rspData);
					var obj = rspData.busiData;
					
					ul.model.del(obj);
					var menu = ul.view.tree.generalTreeData(ul.model.data,"id","name","parent_id","id");
					var tt = $("#tt");
					tt.tree({data:menu});    
					
				},error:function(){
					$.messager.alert("失败","服务器异常!","info"); 
				}});
		    }
		});
	};
	ul.view.addBtnHandler("delete",delHandler);
	
	function updateHandler(param){ 
		
		if(ul.view.status!="default")
		{
			$.messager.alert("提示","请先取消编辑!","info");
			return;
		}
		if(ul.model.selIndex == -1)
		{
			return;
		}
		ul.view.setStatus("edit"); 
		ul.model.disableBill(false);
	}; 
	ul.view.addBtnHandler("update",updateHandler);
	
	function cancelHandler(param){ 
		if(ul.view.status=="default")
		{ 
			return;
		}
		ul.view.setStatus("default");
		var sel = $("#tt").tree("getSelected"); 
		if(sel)
		{
			ul.model.select(sel["attributes"]);
		}else
		{
			ul.model.select();
		}
		ul.model.disableBill(true);
	};
	ul.view.addBtnHandler("cancel",cancelHandler);
	
	function refreshHandler(param){ 
		
		if(ul.view.status!="default")
		{
			$.messager.alert("提示","请先取消编辑!","info");
			return;
		}
		var busiType = ul.appctx.attrs["qq9214_bt"];
		var queryParams = {qq9214_bt:busiType,qq9214_at:"query"};
		ul.appctx.sendRequest(queryParams,{dataType:"json",success:function(rspData){
			
			ul.appctx.validRsp(rspData); 
			var orgsData = rspData.busiData;
			ul.model.initData(orgsData); 
			
			var menu = ul.view.tree.generalTreeData(orgsData,"id","name","parent_id","id");
			$('#tt').tree("loadData",menu); 
			 
		}});
	}; 
	ul.view.addBtnHandler("refresh",refreshHandler);
};

function alloOrgs(param){
 
	if(ul.model.selIndex==-1){
		$.messager.alert("错误","请选择需要分配权限的岗位!","error");
		return;
	}
	var alloOrgs = $("#alloOrgs");  
	
	var options = {width:600,height:500,closed:false};
	options.buttons = [];
	options.buttons.push({ text : '确定', handler : function(){
		
		var alloOrgTree = alloOrgs.find("#alloOrgTree");
		//获取当前选中的组织
		var check = alloOrgTree.tree("getChecked");
		var attr = null,newChecked = [],isOld=null; 
		
		//获取之前已经分配的组织
		var mgedOrgs = ul.model.getChildrenData("orgs"); 
		
		//只需要组织ID方便后续操作
		var mgedIds = [];
		for(var i=0;i<mgedOrgs.length;i++)
		{
			mgedIds.push(mgedOrgs[i].org_id);
		}
		
		//获取新选择的组织
		for(var i=0;i<check.length;i++)
		{
			attr = check[i];
			isOld = false; 
			for(var j=0;j<mgedIds.length;j++)
			{
				if(attr.id==mgedIds[j])
				{
					isOld = true;  
					mgedIds.splice(j, 1);
					break;
				}
			} 
			if(isOld)
			{
				continue;
			}
			newChecked.push({org_id:attr.id,rowStatus:"inserted"});
		}
		
		//获取已经删除的组织
		for(var i=0;i<mgedIds.length;i++)
		{
			newChecked.push({org_id:mgedIds[i],rowStatus:"deleted"});
		}
		console.log(newChecked);
		//如果没有删除也没有新选择的组织则返回不做处理
		if(newChecked.length==0)
		{
			return;
		}
		
		//发送到后台服务，更新数据
		var queryParams  = {qq9214_bt:"orgs",qq9214_at:"alloOrgs",busiData:{orgs:newChecked,master_id:ul.model.getSelBean().id}};
		ul.appctx.sendRequest(queryParams,{async:false,dataType:'json',success:function(rspData){
			
			ul.appctx.validRsp(rspData); 
			
			ul.model.setChildrenData("orgs",rspData.busiData);
			 
			ul.model.update(ul.model.getSelBean());
			
		}}); 
		
		$("#alloOrgs").dialog({closed:true});
		
	}});
	options.buttons.push({ text : '取消', handler : function(){
		$("#alloOrgs").dialog({closed:true});
	}}); 
	alloOrgs.dialog(options);
	var orgsData = $('#tt').tree("options").data;
	var alloOrgTree = alloOrgs.find("#alloOrgTree");
	alloOrgTree.tree({data:orgsData,checkbox:true,cascadeCheck:false});
	
	var mgedOrgs = ul.model.getChildrenData("orgs"); 
	
	//将已经分配的组织选上 
	for(var i=0;i<mgedOrgs.length;i++)
	{ 
		var node = alloOrgTree.tree('find', mgedOrgs[i].org_id);
		console.log(node); 
		alloOrgTree.tree("check",node.target);
	} 
	
}
 

function allocate(param){
	
	if(ul.model.selIndex==-1){
		$.messager.alert("错误","请选择需要分配权限的岗位!","error");
		return;
	}
	var aDialog = $("#allocatePanel"); 
	if (aDialog.length==0) {
		var strPanel = '<div id="allocatePanel" class="easyui-dialog" style="width:800px;height:460px;" ></div>';
		$("body").append(strPanel);
		aDialog = $("#allocatePanel"); 
		var options = {
			closed : true, resizable : true, maximizable : false, cache : false, modal : true,title:"分配菜单",
			buttons : [{ text : '确定', handler : function(){
				//点击确定后，需要将差异数据保存至数据库
				var check = $("#menuTree").tree("getChecked");
				  
				var attr = null,newChecked = [],isOld=null; 
				for(var i=0;i<check.length;i++)
				{
					attr = check[i];
					isOld = false; 
					for(var j=0;j<checkNodes.length;j++)
					{
						if(attr.id==checkNodes[j].id)
						{
							isOld = true;
							checkNodes.splice(j,1);
							break;
						}
					} 
					if(isOld)
					{
						continue;
					}
					newChecked.push({menu_id:attr.id,rowStatus:"inserted"});
				}
				
				for(var i=0;i<checkNodes.length;i++)
				{
					newChecked.push({menu_id:checkNodes[i].id,rowStatus:"deleted"});
				}
				console.log(newChecked);
				if(newChecked.length==0)
				{
					return;
				}
				 
				var queryParams  = {qq9214_bt:"orgs",qq9214_at:"allocate",busiData:{menu:newChecked,master_id:ul.model.getSelBean()["id"]}};
				ul.appctx.sendRequest(queryParams,{async:false,dataType:'json',success:function(rspData){
					
					ul.appctx.validRsp(rspData); 
					
					ul.model.setChildrenData("menuTree",rspData.busiData);
					
					ul.model.select(ul.model.getSelBean());
					
				}});
				
				
				aDialog.dialog("close");
			} }, 
			{ text : '取消', handler : function(){
				aDialog.dialog("close");
			}}
			]
		}; 
		aDialog.dialog(options);
	}; 
	
	
	//加载数据
	var param = {qq9214_bt:"menu",qq9214_at:"query"};
	ul.appctx.sendRequest(param,{dataType:"json",async:false,success:function(rspData){
		//验证是否正常返回
		ul.appctx.validRsp(rspData);
		//生成树形数据
		var menuData = ul.view.tree.generalTreeData(rspData.busiData,"id","name","parent_id","id");
		var curMenu = ul.model.getSelBean();
		var exeMenu = [];
		var selMenu = ul.model.getChildrenData("menuTree"); 
		for(var i=0;i<selMenu.length;i++)
		{
			//可执行节点
			if(selMenu[i].type=="2")
			{
				exeMenu.push(selMenu[i]);
			}
		}
		//如果没有树，则先添加树
		if(aDialog.find("#menuTree").length==0)
		{
			aDialog.append("<ul id='menuTree' class='easyui-tree' data-options='lines:true'></ul>");
		}
		var treeOpts = {data:menuData};
		//保存已经有权限的按钮
		checkNodes = [];
		treeOpts["checkbox"] = function(node){ 
			var attr = node.attributes;
			//可执行功能节点才能选择，1、分类目录;2、可执行功能
			if(attr.type=="2")
			{
				for(var i=0;i<exeMenu.length;i++)
				{
					//如果已经分配过权限就先保存起来
					if(exeMenu[i].id==attr.id)
					{
						checkNodes.push(node);
						break;
					}
				}
				return true;
			} 
			return false;
		};
		
		var menuTree = $("#menuTree");
		menuTree.tree(treeOpts); 
		
		//将已经分配的权限选上 
		for(var i=0;i<checkNodes.length;i++)
		{ 
			var node = menuTree.tree('find', checkNodes[i].id);
			console.log(node); 
			$("#menuTree").tree("check",node.target);
		}  
	}});
	 
	aDialog.dialog("open"); 
};
$(initial);