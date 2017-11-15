function initial()
{
	initialFields();
	initialBtn(); 
	initialData();
	initialHandler();
	//console.log($("#ul_children_user"));
	
	/**
	$("#ul_children_user").datagrid({data:[{name:"<option value='yb'>叶兵</option>"}]});
	console.log($("#ul_children_user").datagrid('options'));
	*/
	 
	$("#ul_children_user").datagrid({onAfterEdit:function(p1,p2,p3){
		
		console.log(p1);
		console.log(p2);
		console.log(p3);
	}});
};

function initialFields()
{ 
	ul.appctx.initial(cxtPath,"menu");
	ul.appctx.attrs["meta"]=billTemp;
	ul.view.generalFields("billContainer",billTemp);
	ul.model.disableBill(true);
}; 
 
function initialData()
{
	var busiType = ul.appctx.attrs["qq9214_bt"];
	var queryParams = {qq9214_bt:busiType,qq9214_at:"query"};
	ul.appctx.sendRequest(queryParams,{dataType:'json',success:function(rspData){
	 
		if(rspData.code!="success")
		{
			$.messager.alert("错误",rspData.msg,"error");
			return;
		}
		
		var menusData = rspData.busiData;
		console.log(menusData);
		
		ul.model.initData(rspData.busiData); 
		
		var menu = ul.view.tree.generalTreeData(menusData,"id","name","parent_id","id");
		console.log(menu);
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
	
	ul.model.addListener("data_selected",function(eventType,data){
		ul.model.setBillValue(data["bean"]);
	});
};

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
			if(ul.model.getSelBean()["type"]=="2")
			{
				$.messager.alert("错误","可执行功能不能添加下级节点!","error");
				return;
			}
			var tv = new ul.view.textValuePair(ul.model.getSelBean()["id"],ul.model.getSelBean()["name"])
			ul.model.setFieldValue("parent_id",tv);
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
		ul.appctx.sendRequest(sendData,{success:function(rspData){

			if(rspData.code!="success")
			{
				$.messager.alert("错误",rspData.msg,"error");
				return;
			} 
			var obj = rspData.busiData;
			if(ul.view.status=="add")
			{
				ul.model.insert(obj);
				
			}else
			{
				ul.model.update(obj); 
			} 
			ul.model.setBillValue(obj);
			
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
					if(rspData.code!="success")
					{
						$.messager.alert("错误",rspData.msg,"error");
						return;
					} 
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
		ul.appctx.sendRequest(queryParams,{success:function(rspData){
			if(rspData.code!="success")
			{
				$.messager.alert("错误",rspData.msg,"error");
				return;
			} 
			var menuData = rspData.busiData;
			ul.model.initData(menuData); 
			
			ul.model.cleanBill();
			
			var menu = ul.view.tree.generalTreeData(menuData,"id","name","parent_id","id");
			$('#tt').tree("loadData",menu); 
			 
		}});
	}; 
	ul.view.addBtnHandler("refresh",refreshHandler);
};

function initialHandler()
{ 
	function beforeValid()
	{
		if(ul.view.status!="add" && ul.view.status!="edit")
		{
			$.messager.alert("错误","非编辑状态不能增加或删除行!");
			return true;
		}
	};
	ul.view.datagrid.handlerMap["before_ul_children_function_addRow"] = beforeValid;
	ul.view.datagrid.handlerMap["before_ul_children_user_addRow"] = beforeValid;
	ul.view.datagrid.handlerMap["before_ul_children_function_delRow"] = beforeValid;
	ul.view.datagrid.handlerMap["before_ul_children_user_delRow"] = beforeValid;
}
$(initial);