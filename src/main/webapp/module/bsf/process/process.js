ul.model.addListener("before_initial_buttons",function(eventType,obj){
	 
	var btns = obj.data; 
	btns.splice(0,btns.length); 
	btns.push({id:"deploy",qq9214_at:"deploy",name:"部署流程",enableStatus:["default"]});
	//btns.push({id:"save",qq9214_at:"save",name:"保存",enableStatus:["default"]});
	//{id:"cancel",qq9214_at:"cancel",name:"取消",enableStatus:["add","edit"]},
 
	ul.view.addBtnHandler("deploy",function(){
		
		var dlgFile = $("#dlgProFile");
		if(dlgFile.length==0)
		{
			var str = '<div id="dlgProFile" class="easyui-dialog" title="部署流程" data-options="iconCls:\'icon-save\'" style="text-align:center;width:400px;height:200px;padding-top:20px;">';
			str += '<input class="easyui-filebox" name="file" style="width:70%;" data-options="accept:\'.bpmn\',onChange:onFileChange,required:true,buttonText:\'请选择文件\',prompt:\'请选择流程文件\'" /><br/><br/>';
			str += '<input class="easyui-textbox" id="deployName" name="deployName" style="width:70%;" data-options="required:true,prompt:\'部署名称\'" /><br/><br/>';
			str += '<input class="easyui-linkbutton" value="部 署" style="width:80px;height:30px;" onclick="deploy()" />';
			str += '</div>';
			$("body").append(str);
			dlgFile = $("#dlgProFile");
			$.parser.parse(dlgFile);
		} 
		dlgFile.dialog({closed:false});
		dlgFile.dialog("center");
	}); 
	 
});

function onFileChange(newValue,oldValue)
{
	$("#deployName").textbox("setValue",newValue);
}

function deploy()
{
	var proFile = $(".easyui-filebox").filebox("textbox").next()[0].files[0];
	var deployName = $("#deployName").textbox("getValue");
	if(!deployName)
	{
		$.messager.alert("错误","部署名称不能为空!","error");
		return;
	}
	var data = new FormData(ul.appctx.getForm());
	data.append("qq9214_at","deploy");
	data.append("proFile",proFile);
	data.append("deployName",deployName);
	ul.appctx.sendFormData(data,{success:function(rspData){
		ul.appctx.validRsp(rspData); 
		$.messager.alert("信息","部署成功!","info"); 
		ul.model.insert(rspData.busiData);
	}});
	
	$("#dlgProFile").dialog("close");
};

//加载页面时初始化流程图页签
ul.model.addListener("ui_initialized",function(eventType,obj){ 
	var tab = $("#ul_list_children_tabs").tabs("getTab","流程图"); 
	tab.panel({content:"<img id='imgPro'   />",style:{textAlign:"center"}}); 
});

/**显示流程图*/
ul.model.addListener("data_selected",function(eventType,param){   
	var url = "";
	if(param.bean)
	{
		url = ul.appctx.attrs["cxtPath"]+"?qq9214_bt=process&qq9214_at=propic&billId="+param.bean.id;
	}
	$("#imgPro").attr("src",url);
});