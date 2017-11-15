

function saveTrace()
{
	var result = $("#traceForm").form("validate");
	if(!result)
	{
		return;
	}
	
	var busiType = ul.appctx.attrs["qq9214_bt"];
	var masterId = ul.model.getSelBean().id;
	var content =  $("#traceContent").textbox("getValue");  
	var traceTime =  $("#traceTime").textbox("getValue");
	var queryParams = {qq9214_bt:busiType,qq9214_at:"saveTrace",busiData:{master_id:masterId,content:content,trace_time:traceTime}};
	 
	ul.appctx.sendRequest(queryParams,{dataType:"json",async:false,success:function(rspData){
		 
		ul.appctx.validRsp(rspData);
		var traceRecode =  rspData["busiData"];
		
		var traceData = ul.model.getChildrenData("trace",ul.model.getSelBean());
		 
		traceData.push(traceRecode);
		var selBean = ul.model.getSelBean();
		ul.model.select(null);  
		ul.model.select(selBean);
		$("#traceDialog").dialog({closed:true});
	}});
	
}

function changeType(e,p1,p2)
{ 
	var menuItem = $('#contentMM').menu("getItem",this);
 
	$('#contentMM').menu("hide");
	var busiType = ul.appctx.attrs["qq9214_bt"]; 
	var id = $('#contentMM').data("bean").id;
	var csttypeId = menuItem.csttype.id;
	
	var queryParams = {qq9214_bt:busiType,qq9214_at:"update",busiData:{id:id,csttype_id:csttypeId}};
	ul.appctx.sendRequest(queryParams,{dataType:"json",async:false,success:function(rspData){
		ul.appctx.validRsp(rspData);
		var busiData = rspData.busiData;
		
		ul.model.update(busiData);
	}});
};

function initialEventHandler()
{ 
	function onRowContextMenu(e,index,row)
	{   
		if(index==-1)
		{
			return;
		}
		e.preventDefault();  
		if($('#contentMM').children().length==1)
		{
			var busiType = ul.appctx.attrs["qq9214_bt"];
			var queryParams = {qq9214_bt:busiType,qq9214_at:"qCsttype",busiData:{}};
			ul.appctx.sendRequest(queryParams,{dataType:"json",async:false,success:function(rspData){
				 
				ul.appctx.validRsp(rspData);
				var csttypes =  rspData["busiData"]; 
				var csttype = null;
				for(var i=0;i<csttypes.length;i++)
				{
					//style='width:200px;cursor:pointer;'
					csttype = csttypes[i];
					$('#contentMM').menu('appendItem', {
						text: csttype.name,
						iconCls:null,
						onclick: changeType,
						csttype:csttype 
					}); 
				}  
			}});
			
		}
		$('#contentMM').data("bean",row); 
		$('#contentMM').menu('show',{
            left: e.pageX,
            top: e.pageY 
        });
		
	};
	$("#listPanel").find("#tblHead").datagrid({onRowContextMenu:onRowContextMenu}); 
	/**表体不能编辑*/
	$("#cardPanel").find("#ul_children_trace").datagrid({onDblClickRow:showTrace,dblclickToEdit:false}).datagrid("disableCellEditing");
	
	
	
	function addTrace(param)
	{ 
		if(ul.model.selIndex==-1)
		{
			$.messager.alert("错误","请先选择需要添加跟进的客户!","error");
			return;
		}
		$("#traceForm").form("reset");
		$("#btnSaveTrace").linkbutton({disabled:false});
		$("#traceDialog").dialog({closed:false});
	}
	ul.view.datagrid.handlerMap["ul_children_trace-addTrace"]=addTrace;
	
	function insert(eventType,data)
	{
		ul.model.transData(billTemp["fields"],data.bean);
		
		$("#listPanel").find("#tblHead").datagrid('appendRow',data.bean);
		 
		//选中当前插入的VO
		ul.model.select(data.bean);
	}
	ul.model.addListener("data_inserted",insert);
	
	function initialData(eventType,data)
	{   
		/**清空数据*/
		ul.model.select(null); 
		$(".easyui-datagrid").datagrid({data:[]});
		
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
		$('#tblHead').datagrid({pageNumber:pageOptions["pageNumber"],pageSize:pageOptions["pageSize"],pageList:pageOptions["pageList"]});
		$("#tblHead").datagrid('getPager').pagination(pageOptions); 
	}
	ul.model.addListener("data_initial",initialData);
	
	function showTrace(index,row)
	{
		$("#traceForm").form("reset"); 
		$("#traceContent").textbox("setValue",row.content);  
	    $("#traceTime").textbox("setValue",row.trace_time);
	    $("#btnSaveTrace").linkbutton({disabled:true});
		$("#traceDialog").dialog({closed:false});
	}
	$("#listPanel").find("#ul_children_trace").datagrid({onDblClickRow:showTrace});
	
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
		if(!param["bean"])
		{
			return;
		}
		var fields = billTemp.children[0].fields; 
		var traceData = ul.model.getChildrenData("trace",ul.model.getSelBean());
		traceData = ul.utils.clone(traceData);
		ul.model.transData(fields,traceData); 
		 
		$("#listPanel").find("#ul_children_trace").datagrid({nowrap:true,striped:false,data:traceData});
		
		
	}
	ul.model.addListener("data_selected",select);
	
	function update(eventType,param)
	{
		ul.model.transData(billTemp["fields"],param["bean"]);
		
		if(ul.view.curPanel=="card")
		{
			ul.model.setBillValue(param["bean"]);
		}  
		$("#tblHead").datagrid('refreshRow',param["index"]);
		 
	}
	ul.model.addListener("data_updated",update);
	
	function deleted(eventType,data)
	{   
		$("#tblHead").datagrid('loadData',ul.model.data);
		var ids = ul.view.pagination.ids;
		var delIndex = null;
		for(var i=0;i<ids.length;i++)
		{
			if(ids[i]==data["bean"]["id"])
			{
				delIndex = i;
				break;
			}
		}
		ids.splice(delIndex,1);
		 
	}
	ul.model.addListener("data_deleted",deleted);
 	
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
	ul.appctx.initial(cxtPath,"cstinfo");
	ul.appctx.attrs["meta"]=billTemp;
	ul.view.generalListPanel("listPanel",billTemp);
	//ul.view.generalFields("billContainer",billTemp);
	 
	ul.view.generalFields("cardPanel",billTemp);
	ul.model.disableBill(true);
	
	//$("#cardPanel")
}; 
 
function initialData()
{
	var busiType = ul.appctx.attrs["qq9214_bt"];
	var queryParams = {qq9214_bt:busiType,qq9214_at:"qids"};
	ul.appctx.sendRequest(queryParams,{dataType:"json",success:function(rspData){ 
		ul.appctx.validRsp(rspData); 
		ul.view.pagination.initialIds(rspData["busiData"]);   
	}});  
	ul.view.setStatus("default");
};

function initialBtn()
{
	var btns = [{id:"add",qq9214_at:"add",name:"增  加",enableStatus:["default"]},
	            {id:"update",qq9214_at:"update",name:"修  改",enableStatus:["default"]},
	{id:"save",qq9214_at:"save",name:"保  存",enableStatus:["add","edit"]},
	{id:"cancel",qq9214_at:"cancel",name:"取  消",enableStatus:["add","edit"]},
	{id:"delete",qq9214_at:"delete",name:"删  除",enableStatus:["default"]}, 
	{id:"refresh",qq9214_at:"refresh",name:"刷  新",enableStatus:["default"]}
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
		queryData(queryBusiData); 
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
	ul.appctx.sendRequest(queryParams,{dataType:"json",success:function(rspData){
		ul.appctx.validRsp(rspData);
		var ids =  rspData["busiData"];
		ul.view.pagination.initialIds(ids);  
	}});
	
};
 
$(initial);