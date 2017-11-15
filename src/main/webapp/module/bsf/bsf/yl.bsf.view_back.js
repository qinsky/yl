
 if(!ul)
 {
	 var ul = {};
	 ul.view = {};
	 yl.view.
 }

/**初始化页面相关的函数*/
(function($){

	var yl  = {};
	yl.name = "ok";
	
	if($.ylview){
		console.log("ylview has initialed!");
		return ;
	}
	
	$.ylview = {
		 
		generalFields:function(containerId,billTemp)
		{
			var mapGroup = {};
			var mapFields = {};
			var hasGroup = billTemp.groups&&billTemp.groups.length>0;
			if(hasGroup)
			{
				for(var i=0;i<billTemp.groups.length;i++)
				{
					mapGroup[billTemp.groups[i].id] = billTemp.groups[i];
					mapFields[billTemp.groups[i].id] = [];
				};
			}

			var strHtml = '<fieldset  id="mainset" style="margin-left:0px;border:none;"><legend id="billName"></legend></fieldset>';
			$("#"+containerId).append(strHtml);

			var panelId = "mainset";

			for(var i=0;i<billTemp["fields"].length;i++)
			{
				var field = billTemp["fields"][i];
				if(field.groupid)
				{
					mapFields[field.groupid].push(field);
					continue;
				}
				
				switch(field["type"])
				{
					case "hidden":
						this.genHiddenComp(panelId,field);
						break;
					case "text":
						this.genEasyuiTextComp(panelId,field);
						break;
					case "select":
						this.genEasyuiComboBoxComp(panelId,field);
						break;
					case "textarea":
						this.genEasyuiTextareaComp(panelId,field);
						break;
					case "ref":
						this.genEasyuiSearchBox(panelId,field);
						break;
					default:
						break;
				}
				 
			}

			if(hasGroup)
			{
				var tfields = null;
				var tfieldSetId = null;
				for(var i=0;i<billTemp.groups.length;i++)
				{
					tfields = mapFields[billTemp.groups[i].id];
					tfieldSetId = "group_"+billTemp.groups[i].id;
					fieldsets = '<fieldset  id="'+tfieldSetId+'" style="margin-left:0px;border:none;"><legend style="font-size:18px;">'+billTemp.groups[i].name+'</legend></fieldset>';
					$("#"+containerId).append(fieldsets);

					for(var j=0;j<tfields.length;j++)
					{
						field = tfields[j];

						switch(field["type"])
						{
							case "hidden":
								this.genHiddenComp(tfieldSetId,field);
								break;
							case "text":
								this.genEasyuiTextComp(tfieldSetId,field);
								break;
							case "select":
								this.genEasyuiComboBoxComp(tfieldSetId,field);
								break;
							case "textarea":
								this.genEasyuiTextareaComp(tfieldSetId,field);
								break;
							case "ref":
								this.genEasyuiSearchBox(tfieldSetId,field);
								break;	
							default:
								break;
						} 
					};
				};
			};
			
			//对界面进行渲染
			$.parser.parse($("#"+panelId));
		},
		
		genEasyuiSearchBox:function(panelId,field)
		{
			var strHtml = "<div id='fdiv_"+field["id"]+"' >";
			strHtml +=  '<input class="easyui-searchbox" ';
			
			strHtml += 'data-options="editable:false,label:\''+field["name"]+'\',labelAlign:\'right\',labelPosition:\'left\', labelWidth:\'60px\',searcher:$.ylview.refPanel.open" style="width:100%;height:30px;"';
			strHtml += ' id="'+field["id"]+'"'; 
			
			if(typeof(field["editable"])!="undefined" && !field["editable"])
			{
				strHtml += " readonly ";
			}
			strHtml += " name='"+field["id"] +"' ";
			
			if(field["required"])
			{
				strHtml += " required ";
			}
			strHtml += ' /></div>';
			
			$("#"+panelId).append(strHtml); 
		},
		
		genHiddenComp:function(panelId,field)
		{
			var strHtml =  "<input type='hidden' id='"+field["id"] +"' />";
			$("#"+panelId).append(strHtml);
		},

		genEasyuiTextareaComp:function(panelId,field)
		{
			var strHtml = null; 
			
			strHtml = "<div id='fdiv_"+field["id"]+"' >";
			strHtml += '<input data-options="multiline:true" class="easyui-textbox easyui-tooltip" style="width:100%;height:40px;" label="'+field.name+'" labelPosition="left"';
			strHtml += ' id="'+field["id"]+'"'; 
			 
			if(typeof(field["editable"])!="undefined" && !field["editable"])
			{
				strHtml += " readonly ";
			}
			
			strHtml += " name='"+field["id"] +"' ";
			 

			if(field["required"])
			{
				strHtml += " required ";
			}

			strHtml += " />";
			
			$("#"+panelId).append(strHtml);
		},

		genEasyuiComboBoxComp:function(panelId,field)
		{
			var strHtml = "<div id='fdiv_"+field["id"]+"'>";
			var curId = field["id"];
			strHtml += '<select id="'+curId+'" class="easyui-combobox" ';
			strHtml += 'data-options="editable:false,label:\''+field["name"]+'\',labelAlign:\'right\',labelPosition:\'left\', labelWidth:\'60px\'" style="width:100%;height:30px;">';
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

			$("#"+panelId).append(strHtml);
		},

		genEasyuiComboComp:function(panelId,field)
		{
			var strHtml = "<div id='fdiv_"+field["id"]+"'>";

			//strHtml += '<div class="easyui-panel" style="border:none;width:100%;font-size:16px;" >';

			strHtml += '<input  id="'+field["id"]+'"  style="width:100%;height:30px;"';

			if(typeof(field["editable"])!="undefined" && !field["editable"])
			 {
				strHtml += " readonly ";
			 }else
			 {
				strHtml += " name='"+field["id"]+"' " ;
			 }
			 strHtml += ' />';
			 //strHtml += '</div>';
			 strHtml += "</div>";
			 $("#"+panelId).append(strHtml);

			 strHtml = '<div id="sp_'+field["id"]+'">';
			// strHtml += '<div style="line-height:22px;background:#fafafa;padding:5px;">请选择...</div>';
			 strHtml += '<div style="padding:10px">';
			 var curValue = null;
			 if(field.options.length!=0)
			 {
				 for(var i=0;i<field.options.length;i++)
				 {
					strHtml += '<input type="radio" style="height:16px;width:16px;" name="lang_'+field["id"]+'" value="'+field.options[i].id+'"';
					if(field.options[i].id==field.value)
					{
						curValue = field.options[i];
						strHtml += ' checked ';
					}
					strHtml += ' />';
					strHtml += '<span style="font-size:16px">'+field.options[i].text+'</span>';
					if(i!=field.options.length-1)
					{
						strHtml += '<br/>';
					};

				 };
			 }
			 strHtml += '</div></div>';
			 $("body").append(strHtml);


			 $('#'+field["id"]).combo({
					required:field.required?true:false,
					editable:false,
					label:field.name,
					labelPosition:'left',
					labelWidth:"60px",
					labelAlign:"right"
			});

			var curId = field["id"];
			$('#sp_'+curId).appendTo($('#'+curId).combo('panel'));


			$('#sp_'+curId+' input').click(function(){
				var v = $(this).val();
				var s = $(this).next('span').text();
				$('#'+curId).combo('setValue', v).combo('setText', s).combo('hidePanel');
			});

		},

		genEasyuiTextComp:function(panelId,field)
		{
			var strHtml = null;
			if(field["type"]=="hidden")
			{
				strHtml = "<input type='hidden' value='"+fieldValue+"'";
			}else
			{
				strHtml = "<div id='fdiv_"+field["id"]+"'>";
				var fieldClass = "easyui-textbox";
				if(field.datatype=="number")
				{
					fieldClass = "easyui-numberbox";
				}else if(field.datatype=="date")
				{
					fieldClass = "easyui-datebox";
				}else if(field.datatype=="time")
				{
					fieldClass = "easyui-timespinner";
				}else if(field.datatype=="datetime")
				{
					fieldClass = "easyui-datetimebox";
				}
				strHtml += '<input type="text" class="'+fieldClass+'"  style="width:100%;height:30px;" label="'+field.name+'" labelPosition="left"';
				strHtml += ' labelWidth="60px" labelAlign="right" ';

				strHtml += "  id='"+field["id"]+"'";
			}
			if(typeof(field["editable"])!="undefined" && !field["editable"])
			{
				strHtml += " readonly ";
			}else
			{
				strHtml += " name='"+field["id"] +"' ";
			}

			if(field.required)
			{
				strHtml += " required ";
			}

			strHtml += " />";
			$("#"+panelId).append(strHtml);
		},

		/**分发按钮点击事件*/
		btnHandler:function(item)
		{
			var param = {};
			if(item)
			{
				param = item;
			}else{
				 var src = event.srcElement;
				 while(true)
				 {
					 if(!src||(src["id"]&&src["id"].indexOf("btn_")==0))
					 {
						 break;
					 }else{
						 src = src.parentElement;
					 }
				 }
				 if(src)
				 {
					param["id"] = src["id"];
					param["text"] = src.innerText;
					param["target"] = src;
				 }
			}

			if(!param["id"]||param["id"].indexOf("btn_")!=0)
			{
				console.log("It's not exists or invalid id!");
				return;
			}
			var strId = param["id"].replace("btn_","");
			if(!$.ylview.btnHandlerMap[strId])
			{
				console.log("defaultHandler handler for button: "+$.ylview.utils.toJsonStr(param));
				$.ylview.defaultBtnHandler(param); 
				return;
			}
			$.ylview.btnHandlerMap[strId](param);
		},
		
		defaultBtnHandler:function(params){
			console.log(params);
			var qq9214_at = params["id"].replace("btn_","");
			var sendData = {};
			sendData["qq9214_bt"] = $.ylview.appctx.getAttr("qq9214_bt");
			sendData["qq9214_at"] = qq9214_at;
			sendData["billData"] = $.ylview.appctx.getBillValue();
			$.ylview.appctx.sendRequest(sendData,{success:function(data){
				$.message.alert("信息","操作成功!","info");
				$.ylview.appcxt.disableBill(flag);
			},error:function(){$.message.alert("错误","服务器异常!","error");}});
			
		},

		btnHandlerMap:{},
		addBtnHandler:function(name,func)
		{ 
			$.ylview.btnHandlerMap[name]= func;
		},

		/**生成菜单*/
		initialBtns:function(btns,typename,parentid,childid)
		{
			var at = typename?typename:"qq9214_at";
			var parentId = parentid?parentid:"parent_id";
			var childId = childid?childid:"id";
			if(!btns)
			{
				console.log("the parameter 'btns' is empty!");
				return;
			}
			if(!(btns instanceof Array))
			{
				console.log("the parameter 'btns''s type is not Array!");
				return;
			}
			
			if($('#btns_panel').length==0)
			{
				console.log("The div that id equals 'btns_panel' is not found!");
				return;
			}
			var strBtns = '<div class="easyui-panel" data-options="fit:true" style="padding:5px;border:none;background:rgb(253,253,255);">';
			var strAllChilds="";
			var strChild = "";
			var strId = "";
			for(var i=0;i<btns.length;i++)
			{
				tObj = btns[i];
				if(tObj[parentId])
				{
					continue;
				}
				strChild = "";
				strBtns += '<a href="#" id="btn_'+tObj[at]+'"';

				if($.ylview.utils.hasChild(btns,tObj))
				{
					strId = tObj[at]+"_child";
					strBtns += ' class="easyui-menubutton" >';
					strChild = '<div id="'+strId+'" style="width:150px;">';
					strChild += this.generalBtn(btns,tObj,at,parentId,childId) + '</div>';

					strAllChilds += strChild;
				}else{
					strBtns += ' class="easyui-linkbutton">';
				}
				strBtns += tObj["name"]+'</a>';
			}
			strBtns += " </div>";

			//console.log(strBtns+strAllChilds);

			$('#btns_panel').append(strAllChilds);
			$('#btns_panel').append(strBtns);


			var param = "";
			for(var i=0;i<btns.length;i++)
			{
				if(btns[i][parentId])
				{
					continue;
				}
				strId = btns[i][at];
				param = {};
				if($.ylview.utils.hasChild(btns,btns[i]))
				{
					param["menu"] = "#"+strId+"_child";
					$('#btn_'+strId).menubutton(param);
					$(param["menu"]).menu(
						{
							onClick:$.ylview.btnHandler
						}
					);
				}else{
					param["onClick"] = this.btnHandler;
					param["plain"] = true;
					$('#btn_'+strId).linkbutton(param);
				}
			}
		},

		/**生成下级菜单*/
		generalBtn:function(btns,curBtn,typename,parentid,childid)
		{
			var at = typename?typename:"qq9214_at";
			var parentId = parentid?parentid:"parent_id";
			var childId = childid?childid:"id";
			 var strBtn = "";
			 for(var i=0;i<btns.length;i++)
			 {
				 if(btns[i][parentId]==curBtn[childId])
				 {
					 strBtn = '<div id=btn_'+btns[i][at]+'>';
					 if($.ylview.utils.hasChild(btns,btns[i]))
					 {
						strBtn += '<span>'+btns[i]["name"]+'</span>';
						strBtn += this.generalBtn(btns,btns[i]);
					 }else{
						strBtn += btns[i]["name"];
					 }
					 strBtn += "</div>";
				 }
			 }
			 return strBtn;
		},
		
		textValuePair:function(param1,param2)
		{
			this.value = param1;
			this.text = param2; 
			this.toString = function()
			{
				return "<option value='"+this.value+"' >"+this.text+"</option>";
			};
		}
	};

})(jQuery);

$.ylview.refPanel = {
		fieldMeta:null,
		content:null,
		open:function (p1, fid) {
			var curPath = $.ylview.refPanel;
			if (!fid) {
				console.log("the field miss id!");
				return;
			}  
			curPath.fieldMeta = $.ylview.appctx.getFieldMeta(fid);

			var sendData = {
				qq9214_bt : "reference",
				busiData : {
					refType : curPath.fieldMeta["refType"]
				}
			};

			//请求参照数据
			$.ylview.appctx.sendRequest(sendData, {
				success : curPath.rspHandler,
				error:function(errData){console.log(errData);$.messager.alert("提示","服务器异常!",'error');}
			}); 
		},
		/** 异步加载数据后处理类 */
		rspHandler:function(rspData){
			
			console.log(rspData);
			rspData = jQuery.parseJSON(rspData);
			
			var curPath = $.ylview.refPanel;
			var options = {}
			if (!curPath.content||curPath.content.length == 0) {
				var strPanel = '<div id="refPanel" class="easyui-dialog" style="width:800px;height:600px;" ></div>';
				$("body").append(strPanel);

				 options = {
					closed : true, resizable : true, maximizable : false, cache : false, modal : true,
					buttons : [{ text : '确定', handler : $.ylview.refPanel.ok }, { text : '取消', handler : $.ylview.refPanel.cancel}
					]
				};
				 
				 curPath.content = $("#refPanel");  
			};
			
			options["title"] = rspData["title"];
			curPath.content.dialog(options); 
			
			curPath.content.empty();
			curPath.content.html('<table id="refPanelTable" class="easyui-datagrid" ></table>');
			var tbOptions = {
				emptyMsg : "No record!", selectOnCheck : true,
				checkOnSelect : true, singleSelect : true,
				rownumbers : true, fit : true, fitColumns : true, nowrap : true
			};
			
			tbOptions["data"] = rspData["data"];
			rspData["fields"].splice(0,0,{field:"select",title:"选择",checkbox:true});
			tbOptions["columns"] = [rspData["fields"]];
			
			$("#refPanelTable").datagrid(tbOptions);
			
			curPath.content.dialog({closed:false});
		},
		ok:function()
		{
			var refTable = $("#refPanelTable");
			var selData = refTable.datagrid("getSelected");
			if(selData==null)
			{
				$.messager.alert("提示","没有选择数据",'error');
				return;
			}
			var options = refTable.datagrid("options");
			var columns = options.columns[0];
			var tv = new $.ylview.textValuePair();
			for(var i=0;i<columns.length;i++)
			{
				if(columns[i]["refText"]==true)
				{
					tv["text"] = selData[columns[i]["field"]];
					continue;
				}
				if(columns[i]["refValue"]==true)
				{
					tv["value"] = selData[columns[i]["field"]];
					continue;
				} 
			}; 
			$.ylview.appctx.setFieldValue($.ylview.refPanel.fieldMeta,tv); 
			$.ylview.refPanel.content.dialog({closed:true});
		},
		cancel:function()
		{
			console.log("cancel");
			$.ylview.refPanel.content.dialog({closed:true});
		}
	};

/** 添加工具类 */
(function($){

	if($.ylview.utils){
		console.log("ylview has initialed!");
		return;
	};

	$.ylview.utils = {};

	/**判断一个记录在一个集合中是否存在下级*/
	$.ylview.utils.hasChild = function(btns,curBtn,parentid,childid)
	{
		var parentId = parentid?parentid:"parent_id";
		var childId = childid?childid:"id";

		for(var i=0;i<btns.length;i++)
		{
			if(btns[i][parentId]==curBtn[childId]){
				return true;
			}
		}
		return false;
	};

	/**将obj对象转换成json数组*/
	$.ylview.utils.toJsonStr = function(obj)
	{  
		var strRet = "";
		if(!obj||(obj instanceof HTMLElement)){
			return strRet;
		}else if(obj instanceof Array)
		{
			strRet += "[";
			for(var i=0;i<obj.length;i++)
			{
				if(obj[i]==null){
					strRet += ",";
				}if(typeof(obj[i]) == "string")
				{
					strRet += "\""+(obj[i].indexOf("\"")<0?obj[i]:obj[i].replace(new RegExp("\"","gm"),"\\\""))
					strRet += "\",";
				}else if(typeof(obj[i]) == "number"||typeof(obj[name]) == "boolean")
				{
					strRet += obj[i]+",";
				}else{
					strRet += $.ylview.utils.toJsonStr(obj[i])+",";
				}
			};

			if(strRet.indexOf(",")>=0)
			{
				strRet = strRet.substr(0,strRet.length-1);
			};
			strRet += "]";
		}else if(typeof(obj)=="object")
		{
			strRet += "{"
			for(var name in obj)
			{
				
				if(typeof(obj[name])=="function"||(obj[name] instanceof HTMLElement))
				{
					continue;
				};
				strRet += "\""+name+"\":";

				if(obj[name]==null){
					strRet += "\"\"";
				}else if(typeof(obj[name]) == "string")
				{
					//将值中的引号转义
					strRet += "\""+(obj[name].indexOf("\"")<0?obj[name]:obj[name].replace(new RegExp("\"","gm"),"\\\""));
					strRet += "\",";
				}else if(typeof(obj[name]) == "number"||typeof(obj[name]) == "boolean")
				{
					strRet += obj[name]+",";
				}else{
					strRet += $.ylview.utils.toJsonStr(obj[name])+",";
				}
			};
			if(strRet.indexOf(",")>=0)
			{
				strRet = strRet.substr(0,strRet.length-1);
			};
			strRet += "}"
		}
		console.log("jsinfo = "+strRet);
		return strRet;
	};
	
	//判断一个对象是否含有某个属性
	$.ylview.utils.hasAttr = function(obj,attr)
	{ 
		if((!obj)||(!attr))
		{
			console.log("The parameter obj or attr is null!");
			return;
		};
		
		if(type(obj[attr])=="undefined")
		{
			return false;
		}
		
		return ntrue;
	};

})(jQuery);

/**初始化单据信息方法*/
(function($){

	if($.ylview.appctx){
		console.log("ylview has initialed!");
		return;
	};

	$.ylview.appctx = new function()
	{
		var attrs={};
		this.initial = function(cxtPath,pBusiType)
		{
			if(attrs["qq9214_bt"] || !pBusiType)
			{
				console.log("Repeat call $.ylview.appctx.initial or the parameter is empty!");
				return;
			}
			attrs["qq9214_bt"] = pBusiType;
			attrs["cxtPath"] = cxtPath;
			this.loadBillTemplate();
		};
		 
		this.loadBillTemplate = function()
		{
			if(!attrs["cxtPath"])
			{
				console.log("The cxtPath is null,Please first initialization!");
				return;
			}; 
			//attrs["billTemplate"] = {"fields":[{"name":"工程性质","options":[{"id":1,"text":"新装水表"},{"id":2,"text":"户表改造"},{"id":3,"text":"水表扩缩"},{"id":4,"text":"管道工程"},{"id":6,"text":"抢修"},{"id":8,"text":"消防栓迁移"},{"id":9,"text":"拆表销户"},{"id":10,"text":"水表原址迁移"},{"id":11,"text":"水表迁移"}],"id":"bztype_id","type":"select","editable":"true","value":1},{"name":"身份证号","id":"id_card","type":"text","value":"123456789"},{"name":"申请人","id":"wt_man","type":"text","value":"测试业主"}]};;
		};
		
		
		this.sendRequest = function(sendData,ctrlData)
		{
			var param = {};
			if(ctrlData)
			{
				param = ctrlData;
			} 
			$.ajax({
				url:attrs["cxtPath"],
				type:"POST",
				data:$.ylview.utils.toJsonStr(sendData),  
				headers:param["contentType"]?param["contentType"]:{"Content-type":"application/json;charset=UTF-8"},
				processData:param["processData"]?param["processData"]:false,
				success:param["success"]?param["success"]:null,
			    error:param["error"]?param["error"]:function(data){$.messager.alert("错误","服务器异常!","error");console.log(data);},
			    complete:param["complete"]?param["complete"]:null	
			}); 
		};
		
		this.getBillValue = function()
		{
			var value = {};
			var billTemplate = attrs["billTemplate"];
			if(!billTemplate)
			{
				console.log("BillTemplate is null!");
				return value;
			}
			
			var fields = billTemplate["fields"]; 
			for(var i=0;i<fields.length;i++)
			{
				value[fields[i]["id"]] = this.getFieldValue(fields[i]); 
			};
			return value;
		},
		
		this.disableBill = function(flag)
		{
			var billTemplate = attrs["billTemplate"];
			if(!billTemplate)
			{
				console.log("BillTemplate is null! Please first call initial!");
				return;
			};
			
			var fields =  billTemplate["fields"]; 
			for(var i=0;i<fields.length;i++)
			{
				if(fields[i]["type"]=="hidden")
				{
					continue;
				}
				$("#"+fields[i]["id"]).textbox(flag?"disable":"enable");
			};
		};
		
		this.getAttr = function(attrName)
		{
			return attrs[attrName];
		};
		
		this.setAttr = function(attrName,value)
		{
			return attrs[attrName] = value;
		};
		
		this.getFieldValue = function(param)
		{
			var fieldMeta = param;
			if(typeof(param)=="string")
			{
				fieldMeta = this.getFieldMeta(param);
			};
			
			if(!fieldMeta)
			{
				return null;
			}
			
			var fValue = "";
			switch(fieldMeta["type"])
			{
				case "text":
					fValue = $("#"+fieldMeta["id"]).textbox("getText");
					break;
				case "select":
					fValue = $("#"+fieldMeta["id"]).combobox("getValue");
					break; 
				case "hidden":
					fValue = $("#"+fieldMeta["id"]).val();
					break; 
				case "ref":
					fValue = new $.ylview.textValuePair();
					fValue["value"] = $("#"+fieldMeta["id"]).data("refValue");
					fValue["text"] = $("#"+fieldMeta["id"]).searchbox("getText"); 
					break;
				break; 
			};
		   
		   return fValue;
			
		};
		
		this.getFieldMeta = function(fieldName)
		{
			if(!fieldName)
			{
				return null;
			}; 
			
			var fields = attrs["billTemplate"]["fields"];
			for(var i=0;i<fields.length;i++)
			{
				if(fields[i]["id"]==fieldName || fields[i]["name"]==fieldName)
				{
					return fields[i];
				}
			};
			
			console.log("The field '"+fieldName+"' is not exist!");
			return null; 
		};
		
		this.setFieldValue = function(param,value)
		{
			var fieldMeta = param;
			if(typeof(param)=="string")
			{
				fieldMeta = this.getFieldMeta(param);
			};
			
			if(!fieldMeta)
			{ 
				return;
			};
			
			switch(fieldMeta["type"])
			{
				case "text":
					$("#"+fieldMeta["id"]).textbox("setValue",value);
					break;
				case "select":
					$("#"+fieldMeta["id"]).combobox("setValue",value);
					break; 
				case "hidden":
					$("#"+fieldMeta["id"]).val(value);
					break; 
				case "ref":
					if(!value||typeof(value)=="string")
					{
						value = new $.ylview.textValuePair("","");
					}
					$("#"+fieldMeta["id"]).data("refValue",value["value"]==null?"":value["value"]);
					$("#"+fieldMeta["id"]).searchbox("setText",value["text"]);
					break; 
			};
		};
		
		this.cleanBill = function()
		{ 
			var billTemplate = attrs["billTemplate"];
			if(!billTemplate)
			{
				console.log("BillTemplate is null!");
				return;
			}
			
			var fields = billTemplate["fields"]; 
			var dv = null;
			for(var i=0;i<fields.length;i++)
			{
				dv = fields[i]["default"]?fields[i]["default"]:"";
				this.setFieldValue(fields[i],dv);
			};
		};
	};

})(jQuery);


(function($){
	
	var tt = {};
	
	if($.ylview.model){
		console.log("$.ylview.model has initialed!");
		return;
	};
	
	$.ylview.model = new function{
		
			this.iniData = function(datas)
			{
				
			};
			
			this.add = function(data)
			{
				
			};
			
			this.del = function(data)
			{
				
			};
			
			this.update = function(data)
			{
				
			};
			
	}; 
	
	return $.ylview.model;
})(jQuery);

