function initial()
{
	initialFields();
	initialBtn(); 
	initialListener(); 
	initialData();
};

function initialFields()
{ 
	ul.appctx.initial(cxtPath,"busi");
	ul.appctx.attrs["meta"]=billTemp;
	ul.view.generalFields("billContainer",billTemp);
	ul.model.disableBill(true);
};  
function initialData()
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
	
	//默认隐藏表体
	$("#ul_list_children_tabs").hide();
};

function initialListener()
{
	ul.model.addListener("data_selected",function(eventType,data){
		 
		if(data["bean"])
		{
			//显示授权的菜单  
			if(data["bean"].type=="2")
			{
				$("#ul_list_children_tabs").show();
			}else
			{
				$("#ul_list_children_tabs").hide();
			} 
			ul.model.setBillValue(data["bean"]);
		}else
		{
			ul.model.cleanBill();
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
	ul.view.datagrid.handlerMap["before_ul_children_action_addRow"] = beforeValid;
	ul.view.datagrid.handlerMap["before_ul_children_action_delRow"] = beforeValid;
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
		sendData["busiData"] = ul.model.getBillValue({isTextValue:false});
		ul.appctx.sendRequest(sendData,{async:false,dataType:"json",success:function(rspData){

			 
			if(rspData["code"]=="error")
			{
				$.messager.alert("错误",rspData["msg"],"error");
				return;
			}
			var obj = rspData["busiData"];
				
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
				sendData["busiData"] = ul.model.getBillValue();
				
				ul.appctx.sendRequest(sendData,{dataType:'json',async:false,success:function(data){
					if(rspData["code"]=="error")
					{
						$.messager.alert("错误",rspData["msg"],"error");
						return;
					}
					var obj = rspData["busiData"];
					
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
		ul.appctx.sendRequest(queryParams,{dataType:'json',params:{name:"laoxu",age:40},async:true,success:function(rspData){
			console.log(this);
			if(rspData["code"]=="error")
			{
				$.messager.alert("错误",rspData["msg"],"error");
				return;
			}
			var busiData = rspData["busiData"];
			
			ul.model.initData(busiData); 
			
			var menu = ul.view.tree.generalTreeData(busiData,"id","name","parent_id","id");
			$('#tt').tree("loadData",menu); 
			 
		}});
	}; 
	ul.view.addBtnHandler("refresh",refreshHandler);
};
 
$(initial);