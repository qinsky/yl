//默认初始化对象
ul.initial = {};

/**
ul.initial.loadRes = function(res)
{
	res = res==""?[]:jQuery.parseJSON(res);
	for(var i=0;i<res.length;i++)
	{
		if(res[i].indexOf(".css")>0)
		{
			$("head").append("<link>");
			var css = $("head").children(":last");
			css.attr({
			      rel:  "stylesheet",
			      type: "text/css",
			      href: res[i]
			});
		}else if(res[i].indexOf(".js")>0)
		{ 
			jQuery.getScript(res[i], function (data) {
				 
			});
		}
	}
}
*/
ul.initial.tree = {};
ul.initial.tree.initialUi = function() 
{ 
	ul.model.addListener("meta_loaded",function(eventType,data){ 
		 
		//利用元数据初始化页面 
		ul.view.generalFields("billContainer",data.data);
		
		ul.model.disableBill(true);
		
		//初始化操作按钮
		ul.initial.tree.initialButtons();
		
		//设置界面默认状态
		ul.view.setStatus("default"); 
		
		ul.model.fireEvent("ui_initialed",{});
	});  
	
	ul.appctx.initial(cxtPath,qq9214Bt);
}; 

ul.initial.tree.initialButtons = function(){
	
	var btns = [{id:"add",qq9214_at:"add",name:"增加",enableStatus:["default"]},
	            {id:"update",qq9214_at:"update",name:"修改",enableStatus:["default"]},
	{id:"save",qq9214_at:"save",name:"保存",enableStatus:["add","edit"]},
	{id:"cancel",qq9214_at:"cancel",name:"取消",enableStatus:["add","edit"]},
	{id:"delete",qq9214_at:"delete",name:"删除",enableStatus:["default"]}, 
	{id:"refresh",qq9214_at:"refresh",name:"刷新",enableStatus:["default"]}
	];
	
	ul.view.initialBtns(btns); 
	
	ul.view.addBtnHandler("add",ul.handler.tree.addHandler); 
	ul.view.addBtnHandler("save",ul.handler.tree.saveHandler); 
	ul.view.addBtnHandler("delete",ul.handler.tree.delHandler); 
	ul.view.addBtnHandler("update",ul.handler.tree.updateHandler); 
	ul.view.addBtnHandler("cancel",ul.handler.tree.cancelHandler); 
	ul.view.addBtnHandler("refresh",ul.handler.tree.refreshHandler);
};

ul.initial.tree.initialData = function()
{
	var busiType = ul.appctx.attrs["qq9214_bt"];
	var queryParams = {qq9214_bt:busiType,qq9214_at:"query"};
	ul.appctx.sendRequest(queryParams,{dataType:'json',async:true,success:function(rspData){
	 
		if(rspData["code"]=="error")
		{
			$.messager.alert("错误",rspData["msg"],"error");
			return;
		}
		var busiData = rspData["busiData"]; 
		ul.model.initData(busiData); 
		
		var menu = ul.view.tree.generalTreeData(busiData,"id","name","parent_id","id");
		
		$('#tree').tree({
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
	
	//默认隐藏表体
	$("#ul_list_children_tabs").hide();
};

ul.initial.tree.initialEventHandler = function()
{
	function initialData(eventType,data)
	{   
		if(data["data"] && data["data"].length>0)
		{
			ul.model.handleRef({beans:data["data"],success:function(beans){			
				var menu = ul.view.tree.generalTreeData(beans,"id","name","parent_id","id");
				$('#tree').tree("loadData",menu);
			}});
			  
		}else
		{
			ul.model.select(null); 
		} 
	}
	ul.model.addListener("data_initial",initialData);
	
	ul.model.addListener("data_selected",function(eventType,data){
		ul.model.setBillValue(data["bean"]);
	});
}


ul.initial.initialUi = function()
{ 
	
	ul.model.addListener("meta_loaded",function(eventType,data){ 
		 
		//利用元数据初始化页面 
		ul.view.generalListPanel("listPanel",data.data); 
		ul.view.generalFields("cardPanel",data.data);
		
		//初始化查询面板
		ul.query.initialQuery("queryPanel",null,ul.handler.queryData);
		
		//初始化操作按钮
		ul.initial.initialButtons();
		
		ul.model.disableBill(true); 
		//设置界面默认状态
		ul.view.setStatus("default"); 
		  
		ul.model.fireEvent("ui_initialized",{});
	});  
	ul.appctx.initial(cxtPath,qq9214Bt);
	 /**卡片显示时增加处理函数*/
	$("#cardPanel").panel({onOpen:function(){
		
		if(ul.model.selIndex != -1)
		{
			if(ul.model.status!="add")
			{
				ul.model.setBillValue(ul.model.getSelBean()); 
			}else
			{
				ul.model.cleanBill();
			}
		}
	}}); 
	$("#cardPanel").panel('close'); 
	//$("#cardPanel") 
};  


/**
ul.initial.initialQuery = function()
{ 
	if(queryTemp)
	{
		ul.view.initialQueryPanel("queryPanel",queryTemp,ul.handler.queryData);
	}
}; */

ul.initial.initialEventHandler = function()
{ 
	/**
	 * 界面初始化完成后，处理联查单据
	 * */
	function uiInitialed(){
		if(iniBillId){
			ul.model.initIds([iniBillId]);
			ul.model.isLink = true;
		};
	};
	ul.model.addListener("ui_initialized",uiInitialed);
	
	function initialIds(eventType,data){
		ul.view.pagination.initialIds(data.ids); 
	};
	ul.model.addListener("ids_initial",initialIds);
	
	function initialData(eventType,data)
	{   
		if(data["data"] && data["data"].length>0)
		{
			ul.model.handleRef({beans:data["data"],success:function(beans){
				
				$('#tblHead').datagrid({data:beans});  
				var pageOptions = ul.view.pagination;
				$("#tblHead").datagrid('getPager').pagination(pageOptions);
				
			}});
			
			//处理联查单据
			if(ul.model.isLink)
			{
				ul.model.isLink = false;
				ul.model.select(data["data"][0]);
				ul.view.showCardPanel();
			}
			  
		}else
		{
			//清空表体数据
			$('#tblHead').datagrid({data:[]}); 
			var pageOptions = ul.view.pagination;
			$("#tblHead").datagrid('getPager').pagination(pageOptions);
			ul.model.select(null); 
		} 
	}
	ul.model.addListener("data_initial",initialData);
	
	function linkQuery(eventType,data)
	{   
		ul.model.isLink = true;
		ul.model.initIds(data.ids);
	}
	ul.model.addListener("link_query",linkQuery);
	
	function insertData(eventType,data)
	{   
		ul.view.pagination.addData(data["bean"]);
		ul.view.pagination.addIds(data["bean"]["id"]);
		ul.view.pagination.showIdPage(data["bean"]["id"]); 
	}
	ul.model.addListener("data_inserted",insertData);
	
	function select(eventType,param)
	{
		if(ul.view.curPanel == "card")
		{
			ul.model.setBillValue(param["bean"]);
		}else
		{
			if(!param["bean"])
			{
				ul.model.cleanBill();
			}
		} 
	}
	ul.model.addListener("data_selected",select);
	
	function update(eventType,param)
	{ 
		if(ul.view.curPanel=="card")
		{
			ul.model.setBillValue(param["bean"],{noCache:true});
		} 
		
		ul.view.pagination.addData(param["bean"]); 
		ul.view.pagination.refresh(false);
		ul.model.select(param["bean"]);
	}
	ul.model.addListener("data_updated",update);
	
	function deleted(eventType,data)
	{     
		ul.view.pagination.delIds(data["bean"]["id"]);
		ul.view.pagination.refresh(false);
	}
	ul.model.addListener("data_deleted",deleted);
 	 
	
	function uiStatusHandler(eventType,param)
	{  
		if(param["newStatus"]=="edit" || param["newStatus"]=="add")
		{
			ul.view.showCardPanel(); 
			ul.model.disableBill(false);
			
			if(param["newStatus"]=="add")
			{
				ul.model.cleanBill(); 
			}
			
			//将流程信息隐藏掉
			ul.view.hideProInfo();
		}
		
		if(param["newStatus"]=="default")
		{
			ul.model.disableBill(true);
			
			//非编辑状态并且已经启动流程
			var bean = ul.model.getSelBean();
			if(bean && bean.processStatus && bean.processStatus.value!='no')
			{
				ul.view.showProInfo();
			}else
			{
				ul.view.hideProInfo();
			}
		}
	};
	ul.model.addListener("uiStatus_changed",uiStatusHandler);
	 
	ul.model.addListener("query_event",function(eventType,param){
		ul.handler.queryData(param.busiData);
	});
	
}


ul.initial.initialButtons = function()
{
	var btns = [{id:"add",qq9214_at:"add",name:"增加",enableStatus:["default"]},
	            {id:"update",qq9214_at:"update",name:"修改",enableStatus:["default"]},
	{id:"save",qq9214_at:"save",name:"保存",enableStatus:["add","edit"]},
	{id:"cancel",qq9214_at:"cancel",name:"取消",enableStatus:["add","edit"]},
	{id:"delete",qq9214_at:"delete",name:"删除",enableStatus:["default"]}, 
	{id:"refresh",qq9214_at:"refresh",name:"刷新",enableStatus:["default"]}, 
	]; 
	
	var meta = ul.appctx.attrs["meta"];
	if(meta.isProcess==true)
	{
		btns.push({id:"startprocess",qq9214_at:"startprocess",name:"提交审批",enableStatus:["default"]});
		btns.push({id:"dotask",qq9214_at:"dotask",name:"审批",enableStatus:["default"]});
	}
	
	ul.model.fireEvent.call(this,"before_initial_buttons",{data:btns});
	
	ul.view.initialBtns(btns);   
	
	//初始化按钮处理功能
	ul.initial.initialActionHandler(); 
	
	ul.model.fireEvent.call(this,"after_initial_buttons",{data:btns});
};

ul.initial.initialActionHandler= function()
{
	ul.view.addBtnHandler("add",ul.handler.addHandler); 
	ul.view.addBtnHandler("delete",ul.handler.delHandler);
	ul.view.addBtnHandler("update",ul.handler.updateHandler);
	ul.view.addBtnHandler("cancel",ul.handler.cancelHandler);
	ul.view.addBtnHandler("save",ul.handler.saveHandler);
	ul.view.addBtnHandler("refresh",ul.handler.refreshHandler);
	ul.view.addBtnHandler("startprocess",ul.handler.startprocessHandler);
	ul.view.addBtnHandler("dotask",ul.handler.dotaskHandler);
}
