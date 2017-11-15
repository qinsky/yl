function initial()
{  
	initialListener();
	initialBtn(); 
	ul.initial.initialUi();  
	//ul.initial.initialQuery();
	initialMetaTree(); 
}; 
$(initial);
 

function initialListener()
{
	ul.initial.initialEventHandler();
	
	ul.model.addListener("ui_initialized",function(){  
		$("#metaInfo").textbox("textbox").bind("dblclick",function(){ 
			var curBean = ul.model.getSelBean();
			if(!curBean)
			{
				$.messager.alert("错误","请选中要编辑的元数据!","error");
				return;
			}
			var strMetaInfo = ul.model.getFieldValue("metaInfo");
			var tObj = $.parseJSON(strMetaInfo);
			var data = ul.view.tree.jsonToTree(tObj,curBean.name);
			$("#editMetaDialog").dialog('open'); 
			metaTree.tree('loadData',[data]);
			metaTree.tree('collapseAll',metaTree.tree('getRoot').target);
		}); 
	});
	
}

function initialBtn()
{
	var btns = [{id:"add",qq9214_at:"add",name:"增加",enableStatus:["default"]},
	            {id:"update",qq9214_at:"update",name:"修改",enableStatus:["default"]},
	{id:"save",qq9214_at:"save",name:"保存",enableStatus:["add","edit"]},
	{id:"cancel",qq9214_at:"cancel",name:"取消",enableStatus:["add","edit"]},
	{id:"delete",qq9214_at:"delete",name:"删除",enableStatus:["default"]}, 
	{id:"refresh",qq9214_at:"refresh",name:"刷新",enableStatus:["default"]},
	{id:"buildTable",qq9214_at:"buildTable",name:"建表",enableStatus:["default"]} 
	];
	/** {"id":"test3","qq9214_at":"delete1","name":"删除1","parent_id":"test2"} */
	
	ul.view.initialBtns(btns);  
	
	//ul.view.addBtnHandler("add",addHandler);
	//初始化按钮处理功能
	ul.initial.initialActionHandler();
	
	//添加建表
	ul.view.addBtnHandler("buildTable",function(){
		var curBean = ul.model.getSelBean();
		if(!curBean)
		{
			$.messager.alert("错误","请选中要建表的元数据!","error");
			return;
		}
		
		var sendData = {qq9214_at:"bt",busiData:{id:curBean["id"]}};
		ul.appctx.sendRequest(sendData,{success:function(rspData){
			ul.appctx.validRsp(rspData); 
			$.messager.alert("提示","建表成功!","info");
		}});
	}); 
};
 
var metaTree = null;
var mtOpe = {};

mtOpe.ok = function()
{
	var rt = metaTree.tree('getRoot');
	var tObj = ul.view.tree.treeToJson(rt);
	var str = ul.utils.toJsonStr(tObj);
	ul.model.setFieldValue("metaInfo",str);
	mtOpe.cancel();
}

mtOpe.cancel = function()
{
	$("#editMetaDialog").dialog('close'); 
}
 
mtOpe.addMetaCancel = function()
{
	$("#meta_ele_name").textbox("setValue","");
	$("#meta_ele_value").textbox("setValue","");
	$("#dialog_add_meta_ele").dialog("close");
}
	
mtOpe.addMetaEle=function()
{
	var test;  
	var sn = metaTree.tree("getSelected");
	var pn = metaTree.tree("getParent",sn.target);
	var eleType = $("#meta_ele_type").combobox("getValue");
	var name = $.trim($("#meta_ele_name").textbox("getValue"));
	var value = $.trim($("#meta_ele_value").textbox("getValue"));
	
	var addNode = {};  
	
	var data = {type:eleType};
	if(sn.type=="array")
	{
		addNode.parent = sn.target;
		if(eleType=="object")
		{ 
			data.children = [];
			data.name = "";
		}else if(eleType=="array")
		{ 
			data.children = [];
			data.name = "";
		}else{
			$.messager.alert("错误","元素类型错误,当前元素为数组类型，只能添加array 或 object","error");
			return
		}
	}else{
	 
		if(name=="")
		{
			$.messager.alert("错误","元素名称不能为空!","error");
			return;
		} 
		data.name = name;
		
		if(eleType=="object")
		{ 
			data.children = [];
		}else if(eleType=="array")
		{ 
			data.children = [];
		}else if(eleType=="number")
		{  
			if(value!="")
			{
				if(!$.isNumeric(value))
				{
					$.messager.alert("错误","元素为number,值必须是数字类型!","error");
					return;
				}
				data.value = parseFloat(value);
			} 
		}else if(eleType == "boolean")
		{ 
			data.value = value=="true"?true:false; 
		}else{
			data.value = value;
		}
		
		if(sn.type!="object")
		{
			addNode.parent = pn?pn.target:null;
		}else{
			addNode.parent = sn.target;
		} 
	}
	
	addNode.data = [data];
	 
	var pnode = null;
	if(addNode.parent)
	{ 
		pnode = metaTree.tree("getNode",addNode.parent);
		
		//校验同级不能存在相同名称的元素
		if(pnode["type"]=="object" && pnode["children"])
		{
			for(var i=0;i<pnode["children"].length;i++)
			{
				if(pnode["children"][i]["name"]==data["name"])
				{
					$.messager.alert("错误","相同名称的元素已经存在!","error");
					return;
				}
			}
			
		}
		
		if(!pnode.name && data.name=="title")
		{
			pnode.name = data.value;
		}
	}
	metaTree.tree("append",addNode); 
	if(pnode)
	{
		metaTree.tree("update",{target:pnode.target});
	}
	mtOpe.addMetaCancel();
};
mtOpe.appendMetaEle = function(){ 
	$("#dialog_add_meta_ele").dialog({closed:false}); 
}
	
mtOpe.removeMetaEle=function(){
	var metaTree = $("#metaTree");
	var sn = metaTree.tree("getSelected");
	metaTree.tree("remove",sn.target); 
}


function initialMetaTree()
{  
	metaTree = $("#metaTree"); 
	
	metaTree.tree({lines:true,onDblClick:onclick,formatter:formatter,
		onAfterEdit:onAfterEdit,onBeforeEdit:onBeforeEdit,onContextMenu:oncontextmenu});
		
	function oncontextmenu(e,node)
	{
		e.preventDefault();
		$(this).tree('select',node.target); 
		$('#ctxMenu').menu('show',{
			left: e.pageX,
			top: e.pageY
		});
	} 
	function onBeforeEdit(node)
	{
		 if(node["children"])
		 {
			node.text = node.name;
		 }else{
			node.text = node.value;
		 }
	}
	
	function onAfterEdit(node)
	{
		 if(node["children"])
		 {
			node.name = node.text;
		 }else{ 
			node.value = getAppropriateValue(node);  
		 } 
		 metaTree.tree("update",{target:node.target});
		 if(node.name=="title")
		 {
			var pn = metaTree.tree("getParent",node.target);
			if(pn)
			{
				metaTree.tree("update",{target:pn.target});
			}
		 }
	}
	
	function getAppropriateValue(node)
	{
		if(node.type=="boolean")
		{
			return node.text=="true"?true:false;
		}if(node.type=="number")
		{
			if(!jQuery.isNumeric(node.text))
			{
				return;
			}
			return  parseFloat(node.text);
		}else{
			return node.text;
		}
	}
	
	function onclick(node)
	{
		if(!node.name)
		{
			return;
		}
		$(this).tree('beginEdit',node.target);
	}
	
	function formatter(node)
	{
		if(node["children"])
		{
			if(node.type=="object" && node.target)
			{
				var pn = metaTree.tree("getParent",node.target); 
				if(pn && pn.type=="array")
				{   
					for(var i=0;i<node.children.length;i++)
					{ 
						if(node.children[i].name=="title")
						{
							node.name = node.children[i].value;
							break;
						}
					}
				}
			}
			
			return node.name+" ："+node.type;
		}else{ 
			return node.name+" : "+node.type+" = "+node.value;
		}
	}
}
