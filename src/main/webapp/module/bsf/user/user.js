

function initialEventHandler()
{ 
	function initialData(eventType,data)
	{   
		if(data["data"] && data["data"].length>0)
		{
			var fields = billTemp["fields"];
			ul.model.transData(fields,data.data);
		}else
		{
			ul.model.select(null); 
		} 
		$('#tblHead').datagrid({data:data["data"]});  
		var pageOptions = ul.view.pagination;
		$("#tblHead").datagrid('getPager').pagination(pageOptions); 
	}
	ul.model.addListener("data_initial",initialData);
	
	
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
			ul.model.setBillValue(param["bean"]);
		} 
		ul.view.pagination.addData(param["bean"]);
		ul.view.pagination.refresh(false);
	}
	ul.model.addListener("data_updated",update);
	
	function deleted(eventType,data)
	{     
		ul.view.pagination.delIds(data["bean"]["id"]);
		ul.view.pagination.refresh(false);
	}
	ul.model.addListener("data_deleted",deleted);
 	
	 /**卡片显示时增加处理函数*/
	$("#cardPanel").panel({onOpen:function(){
		
		if(ul.model.getSelBean())
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
		}
		
		if(param["newStatus"]=="default")
		{
			ul.model.disableBill(true); 
		}
	};
	ul.model.addListener("uiStatus_changed",uiStatusHandler);
	
}



function initial()
{
	initialFields();
	initialBtn();  
	initialData(); 
	initialQuery();
	initialEventHandler(); 
};

function initialQuery()
{ 
	ul.view.initialQueryPanel("queryPanel",queryTemp,queryData);
}; 

function initialFields()
{ 
	ul.appctx.initial(cxtPath,"user");
	ul.appctx.attrs["meta"]=billTemp;
	ul.view.generalListPanel("listPanel",billTemp);
	//ul.view.generalFields("billContainer",billTemp);
	 
	ul.view.generalFields("cardPanel",billTemp);
	ul.model.disableBill(true);
	
	//$("#cardPanel")
}; 
 
function initialData()
{
	/**
	var busiType = ul.appctx.attrs["qq9214_bt"];
	var queryParams = {qq9214_bt:busiType,qq9214_at:"qids"};
	ul.appctx.sendRequest(queryParams,{dataType:"json",success:function(rspData){ 
		ul.appctx.validRsp(rspData);
		console.log(rspData);
		ul.view.pagination.initialIds(rspData["busiData"]);   
	}});  
	*/
	ul.view.setStatus("default");
	queryData(); 
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
		ul.appctx.sendRequest(sendData,{dataType:'json',success:function(data){
			//$.messager.alert("信息","操作成功!","info"); 
			if(data["code"]=="error")
			{
				$.messager.alert("错误",data["msg"],"error");
				return;
			}
			var obj = data["busiData"];
			if(ul.view.status=="add")
			{
				ul.model.insert(obj);  
			}else
			{
				/**分发事件的时候，需要提供更新的行*/
				ul.model.update(obj);  
			}  
			ul.view.setStatus("default"); 
			
		},error:function(){
			$.messager.alert("失败","服务器异常!","info");
			ul.model.disableBill(false);
		}});
	};
	ul.view.addBtnHandler("save",saveHandler);
	
	function delHandler(param){ 
		var selBean = ul.model.getSelBean(); 
		if(!selBean)
		{
			$.messager.alert("错误","请选择要删除的记录!","error");
			return;
		}
		$.messager.confirm('确认','确定要删除这条记录吗?',function(r){
		    if (r){
		    	
		    	var sendData = {};
				sendData["qq9214_bt"] = ul.appctx.attrs["qq9214_bt"];
				sendData["qq9214_at"] = "delete";
				sendData["busiData"] = selBean;
				
				ul.appctx.sendRequest(sendData,{dataType:'json',success:function(data){
					//$.messager.alert("信息","操作成功!","info");
					if(data["code"]=="error")
					{
						$.messager.alert("错误",data["msg"],"error");
						return;
					} 
					//清除
					ul.model.cleanBill();
					var obj =  data["busiData"]; 
					ul.model.del(obj);  
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
			$.messager.alert("提示","请选择要修改的数据!","info");
			return;
		}
		ul.view.setStatus("edit");  
	}; 
	ul.view.addBtnHandler("update",updateHandler);
	
	function cancelHandler(param){ 
		if(ul.view.status=="default")
		{ 
			return;
		}
		ul.view.setStatus("default");   
	};
	ul.view.addBtnHandler("cancel",cancelHandler);
	
	function refreshHandler(param){ 
		
		if(ul.view.status!="default")
		{
			$.messager.alert("提示","请先取消编辑!","info");
			return;
		}
		ul.view.pagination.refresh(true);
	}; 
	ul.view.addBtnHandler("refresh",refreshHandler);
};
var queryBusiData = null;
function queryData(busiData){
	queryBusiData = busiData;
	var busiType = ul.appctx.attrs["qq9214_bt"];
	var queryParams = {qq9214_bt:busiType,qq9214_at:"qids"};
	if(busiData)
	{
		queryParams["busiData"] = busiData;
	} 
 
	ul.appctx.sendRequest(queryParams,{dataType:"json",beforeSend:ul.utils.loading,complete:ul.utils.unloading,success:function(rspData){
		 
		ul.appctx.validRsp(rspData);
		var ids =  rspData["busiData"];
		ul.view.pagination.initialIds(ids);  
	}});
	
};
 
$(initial);