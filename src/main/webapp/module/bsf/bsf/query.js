ul.query = {};

ul.query.initialQuery = function(containerId,metaObj,handler)
{
	var qmeta = null;
	if(!metaObj)
	{
		qmeta = ul.appctx.attrs["qMeta"];
	} 
	
	if(!qmeta)
	{
		console.log("Can't initial "+containerId+",because 'qMeta' is null!");
		return;
	} 
	
	strHtml = '<fieldset  id="querySet" style="padding:0px;padding-left:3px;margin-left:0px;margin-top:8%;margin-right:0px;border:none;"><legend id="billName"></legend></fieldset>';
	$("#"+containerId).append(strHtml); 

	var panelId = "querySet";
	
	//生成控件
	ul.query.genComp(panelId,qmeta);
 
	jQuery.parser.parse($("#"+containerId));
	
	//生成查询按钮
	ul.query.genBtn(containerId);
}

ul.query.genBtn = function(containerId){ 
	
	var strBtn = "<a id='queryBtn' style='margin-top:20px' href='#' style=''>查  询</a>";
	$("#"+containerId).append(strBtn); 
	
	$("#queryBtn").linkbutton({width:"80px",onClick:function(){
		
		if(ul.view.status!="default")
		{
			$.messager.alert("提示","请先取消编辑!","info");
			return;
		}
		
		var qMeta = ul.appctx.attrs["qMeta"];
		if(qMeta==null)
		{
			console.log("qMeta is null!");
			return;
		}
		var fields = qMeta.fields;
		var field = null;
		var busiData = {fields:[]};
		var v = null;
		
		for(var i=0;i<fields.length;i++)
		{
			field = fields[i];
			v = ul.model.getFieldValue(field,{isTextValue:false,fieldId:("query_"+field.field)});  
			v = ul.utils.trim(v);
			var cdType = null;
			var cdTypeObj = $("#cdType_"+field["field"]);
			if(cdTypeObj.length!=0)
			{
				cdType = cdTypeObj.combobox("getValue"); 
			} 
			var v2 = null;
			if(cdType=="><")
			{
				v2 = ul.model.getFieldValue(field,{isTextValue:false,fieldId:("query2_"+field.field)});  
				v2 = ul.utils.trim(v2); 
			}
			 
			if(v || v2)
			{   
				var qField = {field:field.field,cdType:cdType};
				if(v)
				{ 
						qField.value = v;
				}
				if(v2)
				{
					qField.value2 = v2;
				}
				busiData["fields"].push(qField);
			}
		}  
		
		ul.model.fireEvent("query_event",{busiData:busiData});
	}});
};

ul.query.genComp = function(panelId,qmeta)
{
	for(var i=0;i<qmeta["fields"].length;i++)
	{
		var field = qmeta["fields"][i]; 
		if(!field.qInfo || !field.qInfo.default)
		{
			continue;
		}
		switch(field["type"])
		{
			case "hidden":
				ul.query.genEasyuiTextComp(panelId,field);
				break;
			case "text":
				ul.query.genEasyuiTextComp(panelId,field);
				break;
			case "select":
				ul.query.genEasyuiComboBoxComp(panelId,field);
				break;
			case "textarea":
				ul.query.genEasyuiTextareaComp(panelId,field);
				break;
			case "ref":
				ul.query.genEasyuiSearchBox(panelId,field);
				break;
			default:
				break;
		} 
	} 
}

ul.query.genEasyuiTextComp = function(panelId,field)
{
	var strHtml = null;
	var fieldWidth = field.width?field.width:"332px"; 
	
	if(field["type"]=="hidden")
	{
		strHtml = "<input type='hidden' id='"+field.field+"' value='"+fieldValue+"'";
	}else
	{
		strHtml = "<div id='fdiv_query_"+field["field"]+"' style='text-align:right;width:"+fieldWidth+"' >";
		var fieldClass = ul.query.getTextComClass(field);
		 
		//获取条件控件
		var label = ul.query.getCdTypeControl(field); 
		strHtml += label;
		
		var width= ul.query.getCompWidth(field);
		var q2Width = width>210?"210px":((width+30)+"px");
		width = width>180?"180px":(width+"px");
		
		strHtml += '<input type="text" class="'+fieldClass+'"  style="width:'+width+';height:30px;"'; 

		strHtml += "  id=query_"+field["field"] +" />";
		
		if(ul.query.isContainSymbol(field,"><"))
		{
			strHtml += '<div style="display:none;"><input type="text" hidden=true class="'+fieldClass+'" style="width:'+width+';height:30px;margin-right:0px;"'; 
			strHtml += "  id=query2_"+field["field"] +" /></div>"; 
		}  
	}  
	
	strHtml += " </div>"; 
	$("#"+panelId).append(strHtml);
};

ul.query.genEasyuiComboBoxComp = function(panelId,field)
{
	var fieldWidth = field.width?field.width:"332px"; 
	
	var width= ul.query.getCompWidth(field);
	width = width>180?"180px":(width+"px");
	
	var strHtml = "<div id='fdiv_query_"+field["field"]+"' style='width:"+fieldWidth+"'>";
	strHtml += ul.query.getCdTypeControl(field); 
	var curId = field["field"];
	strHtml += '<select id="query_'+field["field"]+'"  class="easyui-combobox" ';
	strHtml += 'data-options="editable:false" style="width:'+width+';height:30px;margin-right:0px;">';
	var curOption = null;
	if(field.options.length!=0)
	{
			for(var i=0;i<field.options.length;i++)
			{
				curOption = field["options"][i];
				strHtml += '<option value="'+curOption["id"]+'">';
				strHtml += curOption["text"]+'</option>';
			};
	};
	strHtml += "</select></div>";
	
	if(ul.query.isContainSymbol(field,"><"))
	{
		strHtml += '<div style="display:none;">';
		strHtml += '<select id="query_'+field["field"]+'" hidden=true class="easyui-combobox" ';
		strHtml += 'data-options="editable:false" style="width:'+width+';height:30px;margin-right:0px;">';
		var curOption = null;
		if(field.options.length!=0)
		{
				for(var i=0;i<field.options.length;i++)
				{
					curOption = field["options"][i];
					strHtml += '<option value="'+curOption["id"]+'">';
					strHtml += curOption["text"]+'</option>';
				};
		};
		strHtml += "</select></div>"; 
	}  

	$("#"+panelId).append(strHtml);
};


ul.query.genEasyuiSearchBox = function(panelId,field)
{
	var fieldWidth = field.width?field.width:"332px"; 
	
	var width= ul.query.getCompWidth(field);
	width = width>180?"180px":(width+"px");
	
	var strHtml = "<div id='fdiv_query_"+field["field"]+"' style='width:"+fieldWidth+";margin:0px;padding:0px;text-align:right;'>";
	strHtml += ul.query.getCdTypeControl(field); 
	strHtml +=  '<input class="easyui-searchbox" ';  
	strHtml += 'data-options="editable:true,searcher:ul.view.refPanel.open" style="width:'+width+';height:30px;"';
	
	strHtml += ' id="query_'+field["field"]+'"'; 
 
	strHtml += " name='query_"+field["field"] +"' ";
	 
	strHtml += ' />';
	strHtml += '</div>'; 
	
	$("#"+panelId).append(strHtml);  
}; 


ul.query.isContainSymbol = function(field,symbol)
{
	var cdType = field.qInfo.cdType;  
	for(var i=0;i<cdType.length;i++)
	{
		 if(cdType[i].symbol==symbol)
		 {
		 	return true;
		 }
	} 
    return false;
};

ul.query.cdtypeChange = function(){ 
	var field = $(this).attr("id").replace("cdType_",""); 
	var cdType = $(this).combobox("getValue");
	var fp = $("#query2_"+field).parent();
	if(cdType=="><")
	{
		fp.show();
	}else{
		fp.hide();
	}
};

ul.query.getTextComClass = function(field)
{
	var fieldClass = "easyui-textbox";
	if(field.dataType=="number")
	{
		fieldClass = "easyui-numberbox";
	}else if(field["dataType"]=="date")
	{
		fieldClass = "easyui-datebox";
	}else if(field["dataType"]=="time")
	{
		fieldClass = "easyui-timespinner";
	}else if(field["dataType"]=="datetime")
	{
		fieldClass = "easyui-datetimebox";
	}else if(field["dataType"]=="password")
	{
		fieldClass = "easyui-passwordbox";
	}
	 
	return fieldClass;
}

//获取条件下拉框控件
ul.query.getCdTypeControl = function(field)
{ 
	var cdType = field.qInfo.cdType;
	var fieldWidth = field.qInfo.width?field.qInfo.width:"140px";
	var labelWidth = field.qInfo.labelWidth?field.qInfo.labelWidth:"80px";
	var label = "<select id=\"cdType_"+field.field+"\" class=\"easyui-combobox\" label='"+field.title+"' labelPosition='left' labelAlign='right' labelWidth='"+labelWidth+"' ";
	label += " data-options=\"onChange:ul.query.cdtypeChange,editable:false\" style=\"width:"+fieldWidth+";height:30px\">";
	
	var flag = false;
	for(var i=0;i<cdType.length;i++)
	{ 
		label += ("<option value=\""+cdType[i].symbol +"\"");
		if(cdType[i]["default"] && !flag)
		{
			label += " selected='selected'";
			flag = true;
		}
		label += " >"+cdType[i].title+"</option>";
		
	}
    label += "</select>&nbsp;"; 
    return label;
} 

ul.query.getCompWidth = function(field)
{
	var fw = 332;
	if(field.width && field.width.indexOf("px")>0)
	{
		fw = parseInt(field.width.replace("px",""));
	}
	var qw = 140;
	if(field.qInfo.width && field.qInfo.width.indexOf("px")>0)
	{
		qw = parseInt(field.qInfo.width.replace("px",""));
	}
	return (fw-qw);
};