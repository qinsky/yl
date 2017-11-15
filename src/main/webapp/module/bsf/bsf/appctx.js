
/**初始化单据信息方法*/
ul.appctx = {}; 
ul.appctx.attrs = {};  
ul.appctx.initial = function(cxtPath,pBusiType)
{
	if(ul.appctx.attrs["qq9214_bt"] || !pBusiType)
	{
		console.log("Repeat call appctx.initial or the parameter is empty!");
		return;
	}
	ul.appctx.attrs["qq9214_bt"]=pBusiType;
	ul.appctx.attrs["cxtPath"]=cxtPath;
	if(menuId)
	{
		ul.appctx.attrs["menuId"]=menuId;
		ul.appctx.attrs["menuName"]=menuName;
	}
	ul.appctx.loadMeta();
};

ul.appctx.loadMeta = function()
{
	if(!ul.appctx.attrs["cxtPath"])
	{
		console.log("The cxtPath is null,Please first initialization!");
		return;
	};  
	
	var qq9214Bt = ul.appctx.attrs["qq9214_bt"]; 
	if(qq9214Bt)
	{
		var sendParams = {qq9214_bt:"meta",qq9214_at:"qUIMeta",busiData:{busiCode:qq9214Bt}}; 
		ul.appctx.sendRequest(sendParams,{dataType:"json",success:function(rspData){
			  
			ul.appctx.validRsp(rspData);
			//获取返回的元数据
			var meta =  rspData["busiData"]; 
			if(meta && !ul.utils.isEmpty(meta))
			{ 
				//利用元数据初始化页面
				ul.appctx.attrs["meta"]=meta; 
				ul.appctx.initialQueryMeta(meta);
				ul.model.fireEvent.call(this,"meta_loaded",{data:meta});
			}
		}});
	}
};

ul.appctx.initialQueryMeta = function(meta)
{
	ul.appctx.attrs["qMeta"] = getMeta(meta); 
	
	function getMeta(meta)
	{
		var fields = meta.fields;
		if(!fields)
		{
			return;
		}
		var qMeta = {};
		if(meta.code)
		{
			qMeta.code = meta.code;
		}
		if(meta.name)
		{
			qMeta.name = meta.name;
		}
		qMeta.fields = [];
		for(var i=0;i<fields.length;i++)
		{
			if(fields[i].qInfo)
			{ 
				qMeta.fields.push(fields[i]);
			}
		} 
		if(meta.children)
		{
			qMeta.children = [];
			for(var  i=0;i<meta.children.length;i++)
			{
				qMeta.children.push(getMeta(meta.children[i]));
			}
		} 
		return qMeta;
	}
}

ul.appctx.sendRequest = function(sendData,ctrlData)
{
	var param = {};
	if(ctrlData)
	{
		param = ctrlData;
	}  
	if(!sendData["qq9214_bt"])
	{
		sendData["qq9214_bt"] = ul.appctx.attrs["qq9214_bt"];
	}
	if(ul.appctx.attrs["menuId"])
	{
		sendData["menuId"] = ul.appctx.attrs["menuId"];
		sendData["menuName"] = ul.appctx.attrs["menuName"];
	}
	var sendParam = {
		url:ul.appctx.attrs["cxtPath"]
		,type:"POST"
		,params:param.params
		,data:ul.utils.toJsonStr(sendData)  
		,headers:param["contentType"]?param["contentType"]:{"Content-type":"application/json;charset=UTF-8"}
		,processData:param["processData"]?param["processData"]:false
		,success:param["success"]?ul.utils.join(ul.utils.unloading,param["success"]):null
	    ,error:param["error"]?param["error"]:ul.appctx.anscErrHandler
	    ,complete:param["complete"]?param["complete"]:ul.utils.unloading
	    ,dataType:param["dataType"]?param["dataType"]:"json" 
	    ,async:param["async"]==false?false:true 
	    ,beforeSend:param["beforeSend"]?param["beforeSend"]:ul.utils.loading
	}; 
	$.ajax(sendParam); 
};

ul.appctx.getForm = function()
{
	var form = $("#ul_hidden_form");
	if(form.length==0)
	{
		var str = '<div style="display:hidden;"><form id="ul_hidden_form" accept-charset="utf-8"></form></div>';
		$("body").append(str);
		form = $("#ul_hidden_form");
	}
	form.empty();
	return form[0];
};

ul.appctx.sendFormData = function(formData,ctrlData)
{
	var param = {};
	if(ctrlData)
	{
		param = ctrlData;
	}  
	if(!formData.get("qq9214_bt"))
	{
		formData.append("qq9214_bt",ul.appctx.attrs["qq9214_bt"]);
	}
 
	var sendParam = {
		url:ul.appctx.attrs["cxtPath"]
		,type:"POST"
		//,headers:param["contentType"]?param["contentType"]:{"text/html":"charset=UTF-8"}
		,params:param.params
		,data:formData
		,processData:false
		,contentType:false
		,success:param["success"]?ul.utils.join(ul.utils.unloading,param["success"]):null
	    ,error:param["error"]?param["error"]:ul.appctx.anscErrHandler
	    ,complete:param["complete"]?param["complete"]:ul.utils.unloading
	    ,dataType:param["dataType"]?param["dataType"]:"json" 
	    ,async:param["async"]==false?false:true 
	    ,beforeSend:param["beforeSend"]?param["beforeSend"]:ul.utils.loading
	}; 
	$.ajax(sendParam); 
};
ul.appctx.validRsp = function(rspData)
{ 
	if(rspData.code!="success")
	{
		ul.utils.unloading(); 
		$.messager.alert("错误",rspData.msg,"error"); 
		throw(rspData.msg);
	}
};
ul.appctx.anscErrHandler = function(response)
{  
	ul.utils.unloading();
	console.log(this);
	console.log(response);
	$.messager.alert("错误",response["statusText"],"error"); 
};

ul.appctx.getQueryFieldMeta = function(fieldName)
{
	var qm = ul.appctx.attrs["queryMeta"];
	qm = qm?qm:ul.appctx.attrs["qMeta"];
	return getQMeta(qm) ;
	
	function getQMeta(qm){
		if(!qm)
		{
			return null;
		}
		var fields = qm["fields"];
		for(var i=0;i<fields.length;i++)
		{
			if(fields[i]["field"]==fieldName)
			{
				return fields[i];
			}
		} 
		if(qm.children)
		{
			
		}
		return null;
	}
}

 

ul.appctx.getFieldMeta = function(fieldName)
{
	if(!fieldName)
	{
		return null;
	}; 
	
	var fields = ul.appctx.attrs["meta"]["fields"];
	for(var i=0;i<fields.length;i++)
	{
		if(fields[i]["field"]==fieldName || fields[i]["title"]==fieldName)
		{
			return fields[i];
		}
	};
	
	console.log("The field '"+fieldName+"' is not exist!");
	return null; 
}; 