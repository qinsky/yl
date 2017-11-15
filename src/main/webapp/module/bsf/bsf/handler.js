/**添加默认的处理方法*/
ul.handler = {};
ul.handler.tree = {};
//增加操作
ul.handler.tree.addHandler = function(param){
	if(ul.view.status!="default")
	{
		$.messager.alert("提示","当前状态无法新增!","info");
		return;
	}
	ul.model.cleanBill();
	ul.model.disableBill(false);  
	if(ul.model.selIndex!=-1)
	{
		if(!ul.model.getSelBean()["type"]=="leaf")
		{
			var tv = new ul.view.textValuePair(ul.model.getSelBean()["id"],ul.model.getSelBean()["name"])
			ul.model.setFieldValue("parent_id",tv);
			//$.messager.alert("错误","末级节点不能添加下级节点!","error");
			//return;
		}
		
	}
	ul.view.setStatus("add");
};

//删除操作
ul.handler.tree.delHandler = function(param){ 
	$.messager.confirm('确认','确定要删除这条记录吗?',function(r){
	    if (!r){
	    	return;
	}});
	
	var selNode = $("#tree").tree("getSelected");
	if(selNode["children"])
	{
		$.messager.alert("错误","请先删除下级节点!","error");
		return;
	}
	
	var sendData = {}; 
	sendData["qq9214_at"] = "delete";
	sendData["billData"] = ul.model.getBillValue({isTextValue:false});
	
	ul.appctx.sendRequest(sendData,{success:function(rspData){
		 
		//验证是否正常
		ul.appctx.validRsp(rspData);
		
		var obj = rspData.busiData;
		
		ul.model.del(obj);
		var menu = ul.view.tree.generalTreeData(ul.model.data,"id","name","parent_id","id");
		var tt = $("#tree");
		tt.tree({data:menu});    
		
	}}); 
};

//修改按钮
ul.handler.tree.updateHandler = function(param){  
	if(ul.view.status!="default")
	{
		$.messager.alert("提示","请先取消编辑!","info");
		return;
	}
	if(ul.model.selIndex == -1)
	{
		$.messager.alert("提示","清选中需要编辑的数据!","info");
		return;
	}
	ul.view.setStatus("edit"); 
	ul.model.disableBill(false);
}; 
 

//取消操作
ul.handler.tree.cancelHandler = function(param){ 
	if(ul.view.status=="default")
	{ 
		return;
	}
	ul.view.setStatus("default");
	var sel = $("#tree").tree("getSelected"); 
	if(sel)
	{
		ul.model.select(sel["attributes"]);
	}else
	{
		ul.model.select();
	} 
	ul.model.disableBill(true);
	ul.model.setBillValue(ul.model.getSelBean());
};

//刷新操作
ul.handler.tree.refreshHandler = function(param){ 
	
	if(ul.view.status!="default")
	{
		$.messager.alert("提示","请先取消编辑!","info");
		return;
	} 
	var queryParams = {qq9214_at:"query"};
	ul.appctx.sendRequest(queryParams,{success:function(rspData){
		//验证是否正常
		ul.appctx.validRsp(rspData);
		
		var menuData = rspData.busiData;
		ul.model.initData(menuData);  
		ul.model.cleanBill(); 
	}});
}; 

//保存操作
ul.handler.tree.saveHandler = function(param){
	
	if(ul.view.status!="add"&&ul.view.status!="edit")
	{
		console.log(ul.view.status+" 没有保存的数据!");
		return ;
	}
	
	var sendData = {}; 
	sendData["qq9214_at"] = ul.view.status=="add"?"save":"update";
	sendData["busiData"] = ul.model.getBillValue({isTextValue:false});
	ul.appctx.sendRequest(sendData,{success:function(rspData){

		//验证数据
		ul.appctx.validRsp(rspData);
		
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
		var tt = $("#tree");
		tt.tree({data:menu});
		
		var node = tt.tree('find', obj["id"]);
		$('#tree').tree('select', node.target);
		
		ul.view.setStatus("default");
		ul.model.disableBill(true); 
		
	}});
};

//以下是非树形
ul.handler.addHandler = function(param){
	 
	if(ul.view.status!="default")
	{
		$.messager.alert("提示","当前状态无法新增!","info");
		return;
	} 
	ul.view.setStatus("add");  
};

ul.handler.saveHandler = function(param){
	 
	if(ul.view.status!="add"&&ul.view.status!="edit")
	{
		console.log(ul.view.status+" 没有保存的数据!");
		return ;
	}
	
	var sendData = {};
	sendData["qq9214_bt"] = ul.appctx.attrs["qq9214_bt"];
	sendData["qq9214_at"] = ul.view.status=="add"?"save":"update";
	sendData["busiData"] = ul.model.getBillValue({isTextValue:false});
	ul.appctx.sendRequest(sendData,{success:function(data){
		 
		ul.appctx.validRsp(data);
		
		var obj = data["busiData"];
		
		ul.model.handleRef({beans:[obj],success:function(data){
			
			if(ul.view.status=="add")
			{
				ul.model.insert(data[0]); 
				ul.model.select(data[0]);
			}else
			{
				/**分发事件的时候，需要提供更新的行*/
				ul.model.update(data[0]);  
			}  
			ul.view.setStatus("default");  
		}}); 
	}});
};


ul.handler.delHandler = function(param){ 
	var selBean = ul.model.getSelBean();  
	if(!selBean)
	{
		$.messager.alert("错误","请选择要删除的记录!","error");
		return;
	}
	if(selBean.processStatus && selBean.processStatus!="no")
	{
		$.messager.alert("错误","流程单据不能删除!","error");
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

ul.handler.updateHandler = function(param){ 
	
	if(ul.view.status!="default")
	{
		$.messager.alert("提示","请先取消编辑!","info");
		return;
	}
	
	var selBean = ul.model.getSelBean();  
	if(!selBean)
	{
		$.messager.alert("提示","请选择要修改的数据!","info");
		return;
	}
	
	if(selBean.processStatus && selBean.processStatus.value!="no")
	{
		$.messager.alert("错误","流程单据不能修改!","error");
		return;
	}
	ul.view.setStatus("edit");  
}; 

ul.handler.cancelHandler = function(param){ 
	if(ul.view.status=="default")
	{ 
		return;
	}
	ul.view.setStatus("default");   
	ul.model.setBillValue(ul.model.getSelBean());
};

var queryBusiData = null;
ul.handler.queryData = function(busiData){
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

ul.handler.refreshHandler = function(param){ 
	
	if(ul.view.status!="default")
	{
		$.messager.alert("提示","请先取消编辑!","info");
		return;
	}
	//卡片状态只刷新当前选中的数据
	if(ul.view.curPanel=="card")
	{
		var selBean = ul.model.getSelBean();
		if(!selBean)
		{
			return;
		}
		var busiType = ul.appctx.attrs["qq9214_bt"];
		var queryParams = {qq9214_bt:busiType,qq9214_at:"qdatabids"};
		queryParams["busiData"] = {ids:[selBean["id"]]}; 
		ul.appctx.sendRequest(queryParams,{dataType:"json",success:function(rspData){
			ul.appctx.validRsp(rspData);
			var data =  rspData["busiData"];
			if(data && data.length==1)
			{
				ul.model.update(data[0]);
			} 
		}});
	}else
	{	
		//列表状态刷新整个列表
		ul.view.pagination.refresh(true); 
	}
}; 

ul.handler.startprocessHandler = function(param){ 
	
	var selBean = ul.model.getSelBean();  
	if(!selBean)
	{
		$.messager.alert("错误","请选择要启动流程的记录!","error");
		return;
	}
	console.log(selBean);
	if(selBean.processStatus.value!="no")
	{
		$.messager.alert("错误","选中记录流程状态不正确,无法启动!","error");
		return;
	}
	var sendData = {};
	sendData["qq9214_bt"] = ul.appctx.attrs["qq9214_bt"];
	sendData["qq9214_at"] = "startprocess";
	sendData["busiData"] = selBean;
	
	ul.appctx.sendRequest(sendData,{dataType:'json',success:function(data){
		 
		//验证是否成功返回
		ul.appctx.validRsp(data);
		
		$.messager.alert("信息","操作成功!","info");
		
		var obj =  data["busiData"]; 
		//刷新数据
		ul.model.update(obj);    
	}}); 
};

ul.handler.dotaskHandler = function(param){
	
	var bean = ul.model.getSelBean();
	if(bean.processStatus.value!="processing")
	{
		$.messager.alert("错误","当前单据不需要审批","error");
		return;
	}
	var sendParam = {qq9214_bt:"process",qq9214_at:"cutask",busiData:{billId:bean.id}};
	ul.appctx.sendRequest(sendParam,{success:function(rspData){
		ul.appctx.validRsp(rspData);
		var result = rspData.busiData.result;
		if(result==true)
		{
			showApproveDialog();
		}else
		{
			$.messager.alert("错误","当前单据不需要你审批","error");
		}
		
	}});
	
	function doTaskOk(){
		//获取当前选中的单据
		var bean = ul.model.getSelBean();
		//获取审批对话框
		var doTaskDlg = $("#doTaskDlg");
		//获取审批意见
		var opinion = doTaskDlg.find("#proOpinion").textbox("getText"); 
		//获取是否同意
		var isAgree = doTaskDlg.find('input:radio:checked').val();
	 
		var sendParam = {qq9214_bt:"process",qq9214_at:"dotask",busiData:{billId:bean.id,isAgree:isAgree,opinion:opinion}};
		ul.appctx.sendRequest(sendParam,{success:function(rspData){
			ul.appctx.validRsp(rspData);
			
			//关闭审批窗口
			doTaskDlg.dialog("close");
			
			$.messager.alert("提示","操作成功","info");
			
			var busiData = rspData.busiData;
			
			//刷新单据
			ul.model.update(busiData);
		}});
		doTaskDlg.dialog("close");
	};
	
	function doTaskCancel(){
		var doTaskDlg = $("#doTaskDlg");
		doTaskDlg.dialog("close");
	};
	
	function showApproveDialog()
	{
		var doTaskDlg = $("#doTaskDlg");
		if(doTaskDlg.length==0)
		{ 
			var str = '<div id="doTaskDlg" class="easyui-dialog" title="审批" data-options="iconCls:\'icon-save\',modal:true" style="text-align:center;width:400px;height:260px;padding-top:20px;">';
			str += '<div style="text-align:left;padding-left:70px;">';
			str += '是否同意:<input type="radio" name="isAgree" value="Y" checked >是&nbsp;&nbsp;<input type="radio" name="isAgree" value="N">否<br/>';
			str += '<input class="easyui-textbox" id="proOpinion"  style="width:80%;height:120px" labelPosition="top" label="审批意见" data-options="multiline:true,required:true" value="同意"/><br/><br/>';
			str += '</div>';
			str += '<input id="proBtnOk" class="easyui-linkbutton" value="确  定" style="width:80px;height:30px;"   />&nbsp;&nbsp;';
			str += '<input id="proBtnCancel" class="easyui-linkbutton" value="取 消" style="width:80px;height:30px;"  />';
			str += '</div>';
			
			$("body").append(str); 
			doTaskDlg = $("#doTaskDlg");   
			$.parser.parse(doTaskDlg);
	
			doTaskDlg.find("#proBtnOk").linkbutton({onClick:doTaskOk});
			doTaskDlg.find("#proBtnCancel").linkbutton({onClick:doTaskCancel});
			doTaskDlg.dialog({close:false});
			
		}else
		{
			doTaskDlg.dialog("open");
		} 
	}
};

ul.handler.valueChange = function(newValue,oldValue)
{
	var id = $(this).attr("id"); 
	ul.model.fireEvent.call(this,id+"_value_changed",{comp:this,newValue:newValue,oldValue:oldValue});
}
