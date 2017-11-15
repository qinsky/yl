/**初始化模型*/
ul.model = {};
ul.model.selIndex = -1;
ul.model.listener = {};
ul.model.isLink = false;

ul.model.getSelBean = function()
{
	if(ul.model.selIndex!=-1 && (ul.model.data)	)
	{
		return ul.model.data[ul.model.selIndex];
	}
	return null;
};  

ul.model.setSelIndex = function(index)
{ 
	ul.model.selIndex = index;
};

ul.model.addListener = function(eventType,handler)
{
	if(!ul.model.listener[eventType])
	{
		ul.model.listener[eventType] = [];
	} 
	ul.model.listener[eventType].push(handler);
};

ul.model.removeListener = function(eventType,handler)
{
	if(!ul.model.listener[eventType])
	{
		return;
	} 
	var hs = ul.model.listener[eventType];
	/**未完待续*/
};

ul.model.fireEvent = function(eventType,param)
{ 
	if(!ul.model.listener[eventType])
	{
		return;
	} 
	var hs = ul.model.listener[eventType];
	for(var i=0;i<hs.length;i++)
	{
		hs[i].call(this,eventType,param);
	}
};

ul.model.initIds = function(ids)
{
	ul.model.fireEvent.call(this,"ids_initial",{ids:ids});
};

ul.model.initData = function(data)
{
	ul.model.data = data; 
	ul.model.setSelIndex(-1);
	ul.model.fireEvent.call(this,"data_initial",{data:data});
};

ul.model.insert = function(bean)
{
	if(!bean)
	{
		return;
	}
	if(ul.model.data)
	{
		ul.model.data.push(bean); 
	}else
	{
		ul.model.data = [];
		ul.model.data.push(bean);
	}
	ul.model.fireEvent.call(this,"data_inserted",{bean:bean});
	
};

ul.model.del = function(bean)
{ 
	var index = -1;
	for(var i=0;i<ul.model.data.length;i++)
	{
		if(bean["id"] == ul.model.data[i]["id"])
		{
			index = i;
			break;
		}
	}
	if(index!=-1)
	{
		ul.model.data.splice(index,1);
		ul.model.setSelIndex(ul.model.selIndex-1)
		ul.model.fireEvent.call(this,"data_deleted",{bean:bean});
	}
	
	/**
	if(ul.model.data.length>0)
	{
		ul.model.setSelIndex(0);
		ul.model.select(ul.model.data[0]);
	}else
	{
		ul.model.setSelIndex(-1);
	}*/
};

ul.model.update = function(bean)
{
	var has = false;
	for(var i=0;i<ul.model.data.length;i++)
	{
		if(ul.model.data[i]["id"] == bean["id"])
		{
			ul.model.data[i] = bean;
			ul.model.setSelIndex(i); 
			has = true; 
			break;
		} 
	}
	if(has)
	{
		ul.model.fireEvent.call(this,"data_updated",{bean:bean,index:ul.model.selIndex});
	}
}; 

ul.model.setChildrenData = function(code,data,sBean)
{
	var bean = sBean;
	if(sBean==null)
	{
		bean = ul.model.getSelBean();
	}
	
	if(!bean || !bean.children || !code || !data)
	{
		return;
	}
	for(var i=0;i<bean.children.length;i++)
	{
		if(bean.children[i].code==code)
		{
			bean.children[i].data = data;
			break;
		}
	} 
}

ul.model.getChildrenData = function(code,sBean)
{
	var bean = sBean;
	if(sBean==null)
	{
		bean = ul.model.getSelBean();
	}
	if(!bean || !bean.children)
	{
		return null;
	}
	for(var i=0;i<bean.children.length;i++)
	{
		if(bean.children[i].code==code)
		{
			return bean.children[i].data; 
		}
	} 
	return null;
}

ul.model.select = function(bean)
{   
	if(bean && (ul.model.selIndex!=-1))
	{
		if(bean["id"]==ul.model.data[ul.model.selIndex]["id"])
		{
			return;
		}
	} 
	
	if(bean && ul.model.data)
	{
		for(var i=0;i<ul.model.data.length;i++)
		{
			if(bean["id"]==ul.model.data[i]["id"])
			{
				ul.model.setSelIndex(i); 
				break;
			}
		}
	}else
	{
		ul.model.setSelIndex(-1); 
	} 
	ul.model.fireEvent.call(this,"data_selected",{bean:bean,index:ul.model.selIndex});
	 
	//ul.model.setBillValue(bean);
}; 

ul.model.setBillValue = function(originalBean,control)
{	
	try
	{
		//加载流程信息
		ul.model.loadProInfo(originalBean,control);
		
		ul.model.cleanBill();
		if(!originalBean)
		{
			return;
		}
		var bean = ul.utils.clone(originalBean);
		
		var meta = ul.appctx.attrs["meta"];
		if(!meta)
		{
			console.log("meta is null!");
			return value;
		}
		
		var fields = meta["fields"]; 
		/**转换数据*/
		ul.model.transData(fields,bean);
		for(var i=0;i<fields.length;i++)
		{  
			ul.model.setFieldValue(fields[i],bean[fields[i]["field"]]);   
		};
		
		/**设置表体数据,如果有参照类型的数据,需要先转化成 textValuePair */
		if(bean["children"])
		{
			var children = bean["children"]; 
			var childData = null;
			var child = null;
			for(var i=0;i<children.length;i++)
			{
				if($("#ul_children_"+children[i]["code"]).length==0)
				{
					continue;
				}
				childData = children[i]["data"]; 
				var options = $("#ul_children_"+children[i]["code"]).datagrid("options");
				if(!options["columns"] && options["columns"].length==0)
				{
					continue;
				}
				
				var columns = options["columns"][0];
				
				ul.model.transData(columns,childData); 
				$("#ul_children_"+children[i]["code"]).datagrid({data:childData});
				 
			}
			
		}
	}catch(e){
		console.error(e);
		//$.messager.alert("错误","设置单据值出错!","error");
	}
};

ul.model.loadProInfo = function(bean,control)
{
	try{
		control = control?control:{};
		var meta = ul.appctx.attrs["meta"];
		if(!bean || !meta.isProcess  || !bean.processStatus || bean.processStatus.value=='no')
		{
			ul.view.hideProInfo();
			return;
		}  
		
		var proInfo =  $("#_ulview_group_proinfo");
		//如果显示的就是当前流程信息，则不加载
		if(proInfo.data("id")==bean.id && control.noCache==true)
		{
			return;
		}
		//加载流程实例图片
		var url = ul.appctx.attrs["cxtPath"]+"?qq9214_bt=process&qq9214_at=proinspic&billId="+bean.id;
		
		console.log(bean.processStatus.value);
		if(bean.processStatus.value=='processing')
		{
			url += "&random="+Math.random();
		}else if(bean.processStatus.value=='finished' || bean.processStatus.value=='failure')
		{
			url += "&random=finished";
		}
		console.log(url);
		proInfo.find("#proImg").attr("src",url);
		
		//加载数据前先清空数据
		proInfo.find("#tblProInfo").datagrid("loadData",[]);
		
		//加载流程执行信息
		var sendParam = {qq9214_bt:"process",qq9214_at:"qproinfo",busiData:{billId:bean.id}};
		ul.appctx.sendRequest(sendParam,{success:function(rspData){
			ul.appctx.validRsp(rspData);
			
			var tblProInfo = $("#_ulview_group_proinfo").find("#tblProInfo");
			
			tblProInfo.datagrid("loadData",rspData.busiData);
		}});
		//标识当前显示的bean
		proInfo.data("id",bean.id);
		
		//显示流程信息
		ul.view.showProInfo();
	}catch(e)
	{ 
		console.log(e);
	}
};

ul.model.getBillValue = function(p1)
{
	var param = p1?p1:{};
	var value = {};
	var meta = ul.appctx.attrs["meta"];
	if(!meta)
	{
		console.log("meta is null!");
		return value;
	}
	
	var fields = meta["fields"]; 
	for(var i=0;i<fields.length;i++)
	{
		value[fields[i]["field"]] = ul.model.getFieldValue(fields[i],param); 
	};
	
	var children = meta["children"];
	if(children&&children.length>0)
	{
		value["children"] = [];
		for(var i=0;i<children.length;i++)
		{
			 var rows = null;
			 if(param["dgChanges"]==true)
			 {
				 rows = $("#ul_children_"+children[i].code).datagrid("hangesExt"); 
			 }else
			 {
				 rows = $("#ul_children_"+children[i].code).datagrid("getDataExt")["rows"];
			 }
			 
			 if(param["isTextValue"]==false)
			 { 
				 if(rows.length>0)
				 { 
					 for(var j=0;j<rows.length;j++)
					 {
						 for(var attr in rows[j])
						 {
							 if(rows[j][attr] instanceof ul.view.textValuePair)
							 {
								 rows[j][attr] = rows[j][attr]["value"];
							 }
						 }
					 }
				 }
			 }
			 value["children"].push({code:children[i].code,data:rows}); 
		}
	};
	return value;
};

ul.model.disableBill = function(flag)
{
	
	var meta = ul.appctx.attrs["meta"]; 
	var disable = false;
	if(!meta)
	{
		console.log("meta is null! Please first call initial!");
		return;
	};
	
	var fields =  meta["fields"]; 
	for(var i=0;i<fields.length;i++)
	{
		disable = flag;
		if(fields[i]["type"]=="hidden")
		{
			continue;
		}
		if(fields[i].editable==false)
		{
			disable = true;
		}  
		$("#"+fields[i]["field"]).textbox(disable?"disable":"enable");
	};
}; 


ul.model.getFieldValue = function(param,control)
{
	var fieldMeta = param;
	if(typeof(param)=="string")
	{
		fieldMeta = ul.appctx.getFieldMeta(param);
	};
	
	if(!fieldMeta)
	{
		return null;
	}
	
	var param = control?control:{};
	var fieldId = param.fieldId?param.fieldId:fieldMeta["field"];
	
	var fValue = "";
	var obj = $("#"+fieldId);
	if(obj.length==0)
	{
		console.log("No field,id = "+fieldId);
		return fValue;
	}
	switch(fieldMeta["type"])
	{
		case "text":
			fValue = obj.textbox("getValue");
			break;
		case "textarea":
			fValue = obj.textbox("getText");
			break;
		case "select":
			fValue = obj.combobox("getValue");
			break; 
		case "hidden":
			fValue = obj.val();
			break; 
		case "ref":
			var refText = obj.searchbox("getText");
			var refValue = obj.data("refValue");
	 
			if(!ul.utils.isEmpty(refText) && !ul.utils.isEmpty(refValue))
			{
				if(param["isTextValue"]==false)
				{
					fValue = obj.data("refValue");
				}else
				{
					fValue = new ul.view.textValuePair();
					fValue["value"] = obj.data("refValue");
					fValue["text"] = obj.searchbox("getText"); 
				}
			}
			break;
		break; 
	};
   
   return fValue;
	
}; 

ul.model.setFieldValue = function(param,value)
{
	var fieldMeta = param;
	if(typeof(param)=="string")
	{
		fieldMeta = ul.appctx.getFieldMeta(param);
	};
	
	if(!fieldMeta)
	{ 
		return;
	};
	
	switch(fieldMeta["type"])
	{
		case "text":
			switch(fieldMeta["dataType"])
			{
				case "datetime":
					$("#"+fieldMeta["field"]).datetimebox("setValue",value);
					break;
				case "date":
					$("#"+fieldMeta["field"]).datebox("setValue",value);
					break;
				default:
					$("#"+fieldMeta["field"]).textbox("setValue",value);
					break;
			}  
			break;
		case "select":
			if(value instanceof ul.view.textValuePair)
			{
				$("#"+fieldMeta["field"]).combobox("setValue",value["value"]);
			}else
			{
				$("#"+fieldMeta["field"]).combobox("setValue",value);
			}
			break; 
		case "hidden":
			$("#"+fieldMeta["field"]).val(value);
			break; 
		case "ref": 
			if(!value)
			{
				value = new ul.view.textValuePair("",""); 
			}
			$("#"+fieldMeta["field"]).data("refValue",value["value"]==null?"":value["value"]);
			$("#"+fieldMeta["field"]).searchbox("setText",value["text"]);
			break; 
		case "textarea":
			$("#"+fieldMeta["field"]).textbox("setValue",value);
			break; 
	};
};

ul.model.cleanBill = function()
{ 
	var meta = ul.appctx.attrs["meta"];
	if(!meta)
	{
		console.log("meta is null!");
		return;
	}
	
	var fields = meta["fields"]; 
	var dv = null;
	for(var i=0;i<fields.length;i++)
	{
		dv = fields[i]["default"]?fields[i]["default"]:"";
		ul.model.setFieldValue(fields[i],dv);
	};
	
	if(meta["children"] && meta["children"].length>0)
	{
		var children = meta["children"];
		for(var i=0;i<children.length;i++)
		{
			$("#ul_children_"+children[i]["code"]).datagrid({data:[]});
		}
	}
};

ul.model.handleRef = function(obj)
{
	if(!obj["beans"])
	{
		if(obj["success"])
		{
			obj["success"](obj["beans"]);
		}
		return;
	}
	var curMeta = obj["meta"];
	if(!curMeta)
	{
		curMeta = ul.appctx.attrs["meta"];
		obj["meta"] = curMeta;
	}
	var refKey = ul.model.getRefKey(obj["beans"],curMeta);
	var params = [];
	for(var n in refKey)
	{
		if(refKey[n].length>0)
		{
			params.push({"refType":n,"ids":refKey[n]});
		}
	}
	if(params.length>0)
	{
		var sendParam = {qq9214_bt:"reference",qq9214_at:"qmttext",busiData:{params:params}};
		ul.appctx.sendRequest(sendParam,{beforeSend:function(){
			this.callbackParams = obj;
		},success:function(rspData){
			//校验是否正常返回
			ul.appctx.validRsp(rspData);
			//获取异步前参数
			var obj = this.callbackParams;
			var refData = rspData.busiData;
			var sfData = {};
			var tData = null;
			var tKey = null;
			for(var i=0;i<refData.length;i++)
			{
				tData = refData[i];
				if(tData["error"])
				{
					console.error(tData["error"]);
					continue;
				}
				var temp = tData["data"];
				for(var j=0;j<temp.length;j++)
				{
					tKey = tData["refType"]+"-"+temp[j]["refvalue"];
					sfData[tKey] = temp[j]["reftext"];
					ul.cache.add(tKey,temp[j]["reftext"]);
				}
			}
			//设置查询到的引用数据
			obj["sfData"] = sfData;
			//处理bean的引用数据
			ul.model.setRefData(obj);
			if(obj["success"])
			{
				obj["success"](obj["beans"]);
			}
		}});
	}else
	{
		ul.model.setRefData(obj);
		if(obj["success"])
		{
			obj["success"](obj["beans"]);
		}
	}
	
};

ul.model.setRefData = function(obj){
	var beans = obj["beans"];
	var sfData = obj["sfData"];
	var meta = obj["meta"];
	var fields = meta["fields"];
	var bean = null;
	var field = null;
	var key = null;
	for(var i=0;i<beans.length;i++)
	{
		bean = beans[i];
		for(var j=0;j<fields.length;j++)
		{ 
			field = fields[j];
			if(field["type"]=="select")
			{
				ul.model.transSelect(field,bean);
			}else if(field.type=="ref")
			{
				 
				//有值并且不等于空，而且是字符串（没转换过）
				if(bean[field.field]!=null && bean[field.field]!="" && !(bean[field.field] instanceof ul.view.textValuePair))
				{
					
					key = field.refType+"-"+bean[field.field];
					var tv = new ul.view.textValuePair();
					tv["value"] = bean[field.field];
					tv["text"] = (sfData && sfData[key])? sfData[key]:ul.cache.get(key);
					bean[field.field] = tv;
				}
			}
			
			if(meta["children"])
			{
				var childrenMeta = meta["children"];
				var childMeta = null;
				var child = null;
				var childData = null;
				for(var n=0;n<childrenMeta.length;n++)
				{
					childMeta = childrenMeta[n];
					childData = ul.model.getChildrenData(childMeta["code"],bean);
					if(childData)
					{
						ul.model.setRefData({"beans":childData,"meta":childMeta,"sfData":sfData})
					}
				}
			}
		}
	}
};

ul.model.getRefKey = function(beans,meta,refData)
{
	var tRefData = refData;
	if(!refData)
	{
		tRefData = {};
	}
	if(!beans)
	{
		return tRefData;
	}
	
	var bean = null;
	var fields = meta.fields;
	var field = null;
	for(var i=0;i<beans.length;i++)
	{
		bean = beans[i];
		for(var j=0;j<fields.length;j++)
		{
			field = fields[j];
			if(field.type=="ref")
			{
				if(!tRefData[field["refType"]])
				{
					tRefData[field["refType"]] = [];
				}
				
				//有值并且不等于空，而且是字符串（没转换过）
				if(bean[field.field]!=null && bean[field.field]!="" && !(bean[field.field] instanceof ul.view.textValuePair))
				{ 
					if(jQuery.inArray(bean[field["field"]],tRefData[field["refType"]])!=-1)
					{
						continue;
					}
					var cacheKey = field["refType"]+"-"+bean[field["field"]];
					//如果没有缓冲才从服务器查询
					if(!ul.cache.get(cacheKey))
					{
						tRefData[field["refType"]].push(bean[field["field"]]); 
					}
				}
			}
		}
		if(meta["children"])
		{
			var childrenMeta = meta["children"];
			var childMeta = null;
			var child = null;
			var childData = null;
			for(var j=0;j<childrenMeta.length;j++)
			{
				childMeta = childrenMeta[j];
				childData = ul.model.getChildrenData(childMeta["code"],bean);
				if(childData)
				{
					ul.model.getRefKey(childData,childMeta,refData)
				}
			}
		}
	}
	
	return tRefData;
}; 

ul.model.transData = function(fields,dataTrans)
{
	if(!dataTrans)
	{
		return;
	}
	var data = [];
	if(!(dataTrans instanceof Array))
	{
		data.push(dataTrans);
	}else
	{
		data = dataTrans;
	}
	if(data.length==0)
	{
		return;
	}
	
	for(var i=0;i<fields.length;i++)
	{
		field = fields[i]; 
		for(var j=0;j<data.length;j++)
		{
			bean = data[j];
			if(field["type"]=="select")
			{
				ul.model.transSelect(field,bean);
			}else if(field.type=="ref")
			{
				ul.model.transRef(field,bean);
			}
		} 
	} 
};

ul.model.transSelect = function(field,bean)
{
	if(bean[field.field]!=null && bean[field.field]!="" && !(bean[field.field] instanceof ul.view.textValuePair))
	{
		var options = field.options;
		if(options && options.length>0)
		{
			for(var n=0;n<options.length;n++)
			{
					if(options[n].id==bean[field.field])
					{
						bean[field.field] = new ul.view.textValuePair(options[n].id,options[n].text);
						break;
					}
			}
		}
	}
};

ul.model.transRef = function(field,bean)
{
	//有值并且不等于空，而且是字符串（没转换过）
	if(bean[field.field]!=null && bean[field.field]!="" && !(bean[field.field] instanceof ul.view.textValuePair))
	{
		var reqParam = {ids:[bean[field.field]],fieldMeta:field,bean:bean}; 
		var tv = ul.view.refPanel.getRefText(reqParam);
		bean[field.field] = tv;
	}
};