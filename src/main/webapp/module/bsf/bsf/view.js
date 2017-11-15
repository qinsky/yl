/**初始化页面相关的函数*/
 if(!ul)
 {
	var ul = {};
	
	//生成唯一的index
	ul.idIndex = (function(){
		var index = 0;
		function getId(){
			return ++index;
		}
		return {getId:getId};
	})();
	 
	
	/**初始化页面相关的函数*/
	ul.view = {};
	
	/** default(默认状态)、add(新增)、edit(编辑)*/
	ul.view.status = "default";
	ul.view.setStatus = function(status)
	{ 
		var oldStatus = ul.view.status;
		ul.view.status = status;
		ul.view.updateBtns();
		ul.model.fireEvent.call(this,"uiStatus_changed",{oldStatus:oldStatus,newStatus:status});
	};
	
	ul.view.updateBtns = function()
	{ 
		if(ul.view.btns && ul.view.btns.length>0)
		{
			var btn = null;
			var isShow = false;
			for(var i=0;i<ul.view.btns.length;i++)
			{
				btn = ul.view.btns[i];
				isShow = false;
				if(btn["enableStatus"])
				{
					for(var j=0;j<btn["enableStatus"].length;j++)
					{
						if(btn["enableStatus"][j]==ul.view.status)
						{
							isShow = true;
							break;
						}
					}
				}
				if(!isShow)
				{
					$("#btn_"+btn["qq9214_at"]).linkbutton("disable");
				}else
				{
					$("#btn_"+btn["qq9214_at"]).linkbutton("enable");
				}
			}
		}
	};
	 
	ul.view.curPanel = "list";
	/**显示列表面板*/
	ul.view.showListPanel = function(param)
	{
		
		var options = param?param:{width:"100%",height:"100%"}; 
		if($("#cardPanel").length>0)
		{
			$("#retPanel").panel('close');
			$("#cardPanel").panel('close'); 
		}; 
		if($("#listPanel").length>0)
		{ 
			$("#listPanel").panel(options);
			$("#listPanel").panel('open');
		};
		
		ul.view.curPanel = "list";
		ul.model.fireEvent("ui_showPanel",{curPanel:"list"});
	};
	
	/**显示卡片面板*/
	ul.view.showCardPanel = function(param)
	{
		
		var options = param?param:{width:"100%",height:"100%"};
		
		if($("#cardPanel").length>0)
		{
			$("#cardPanel").panel(options);
			$("#cardPanel").panel('open');
			
			
		}; 
		if($("#listPanel").length>0)
		{
			$("#retPanel").panel('open');
			$("#listPanel").panel('close');
		};
		
		ul.view.curPanel = "card";
		
		ul.model.fireEvent("ui_showPanel",{curPanel:"card"});
	};
	
	ul.view.generalFields = function(containerId,billTemp)
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
		//生成布局
		var layoutId = "_ulview_layout"+ul.idIndex.getId();
		var strHtml = '<div id="'+layoutId+'" class="easyui-layout" style="width:99%;height:100%;padding:0px;margin:0px;border:0px;" ></div>';
		$("#"+containerId).append(strHtml);
		$.parser.parse($("#"+containerId)); 
		
		//添加中心分区
		$('#'+layoutId).layout('add',{region: 'center', width: '99%',border:false});
		
		var center = $('#'+layoutId).find(".layout-panel-center").find(".panel-body");
		//生成卡片的返回面板
		strHtml = '<div id="retPanel" class="easyui-panel" style="background-color:rgb(244,244,244);border-left:none;border-right:none;border-top:none;width:98%;height:30px;">';
		strHtml += '<a  href="#" style="margin-top:5px;margin-left:10px;height:20px;" class="easyui-linkbutton" data-options="iconCls:\'icon-undo\'">返 回</a></div>';
		center.append(strHtml);
		$.parser.parse(center); 
		
		//默认隐藏返回按钮
		$("#retPanel").panel('close'); 
		$("#retPanel").find(".easyui-linkbutton").bind('click',function(){
			if(ul.view.status=="add"||ul.view.status=="edit")
			{
				$.messager.alert("提示","请先完成编辑!","info");
				return;
			}
			ul.view.showListPanel();
		});
		
		
		var mainSetId = "_ulview_mainset_"+ul.idIndex.getId();
		strHtml = '<fieldset id="'+mainSetId+'" style="margin-left:0px;margin-top:0px;border:none;"><legend id="billName"></legend></fieldset>';
		center.append(strHtml);

		var panelId = mainSetId;
		
		var  hasPro = false;

		for(var i=0;i<billTemp["fields"].length;i++)
		{
			var field = billTemp["fields"][i];
			if(field["groupId"])
			{
				mapFields[field["groupId"]].push(field);
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
		
		//生成分组的字段
		if(hasGroup)
		{
			var tfields = null;
			var tfieldSetId = null;
			for(var i=0;i<billTemp.groups.length;i++)
			{
				tfields = mapFields[billTemp.groups[i].id];
				tfieldSetId = "_ulview_group_"+billTemp.groups[i].id+ul.idIndex.getId();
				fieldsets = '<fieldset  id="'+tfieldSetId+'" style="width:99%;margin:0px;padding:0px;border:none;border-top:1px solid rgb(149,184,231);"><legend style="font-size:15px;">'+billTemp.groups[i].name+'</legend></fieldset>';
				//$("#"+containerId).append(fieldsets);
				$("#"+mainSetId).append(fieldsets);

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
		
		//生成流程信息
		if(billTemp.isProcess == true)
		{
			ul.view.genProInfo(panelId);
		};
		
		//生成表体
		ul.view.genBody(layoutId,billTemp);
		
		//对界面进行渲染
		$.parser.parse($("#"+layoutId));
		 
	}; 
	
	ul.view.genProInfo = function(containerId)
	{
		var container = $("#"+containerId);
		var tfieldSetId = "_ulview_group_proinfo";
		var fieldsets = '<br><br><fieldset  id="'+tfieldSetId+'" style="width:99%;margin:0px;padding:0px;border:1px solid rgb(149,184,231);"><legend style="font-size:15px;">流程信息</legend></fieldset>';
		container.append(fieldsets);
		
		//src='http://p1.img.cctvpic.com/photoAlbum/page/performance/img/2017/10/25/1508930448321_154.jpg'
		$("#"+tfieldSetId).append("<div style='text-align:left;width:99%'><img id='proImg' ></div>");
		
		$("#"+tfieldSetId).append('<br><br><br><table id="tblProInfo" style="width:99%" class="easyui-datagrid" data-options=""> </table>');
		
		 
		var columns = [];
		columns.push({field:"name",title:"任务名称"});
		columns.push({field:"assignee",title:"执行人"});
		columns.push({field:"isAgree",title:"是否通过",formatter:function(value,row,index){if(value=="N")return "否";if(value=="Y")return "是";return value;}});
		columns.push({field:"opinion",title:"执行意见"});
		columns.push({field:"create_time",title:"创建时间"});
		columns.push({field:"end_time",title:"完成时间"});
		var options = {emptyMsg:"还没有记录哟!", singleSelect:true,rownumbers:true,fit:false,fitColumns:true,nowrap:true,width:'99%',height:'260px'};
		options["columns"] = [columns];  
		options["clickToEdit"] = false;
		options["dblclickToEdit"] = false;
		$("#"+tfieldSetId).find("#tblProInfo").datagrid(options);
	 
		
		$.parser.parse(container); 
	};
	
	ul.view.hideProInfo = function()
	{
		$("#_ulview_group_proinfo").hide();
	};
	
	ul.view.showProInfo = function()
	{ 
		$("#_ulview_group_proinfo").show();
		$("#_ulview_group_proinfo").find("#tblProInfo").datagrid({width:'100%'});
	};
	
	ul.view.genBody = function(layoutId,billTemp)
	{
		if(!billTemp["children"]||billTemp["children"].length==0)
		{
			return;
		}
		
		$('#'+layoutId).layout('add',{border:false,region: 'south', height:'50%',split:true});
		
		var south = $('#'+layoutId).find(".layout-panel-south").find(".panel-body");
		var strHtml = '<div class="easyui-tabs" id="ul_card_children_tabs" data-options="narrow:true,fit:true" style="margin-left:0px;padding:0px;margin-top:0px;"></div>';
		south.append(strHtml);
		$("#ul_card_children_tabs").tabs({border:false});
		
		var children = billTemp["children"];
		var tmpid = null;
		var options = null; 
		var toolbar = null;
		for(var i=0;i<children.length;i++)
		{   
			tmpid = 'ul_children_'+children[i]["code"];
			  
			//设置子表页签属性 
			options = {title:children[i]["name"],border:false};
			options["style"] ={borderTopWidth:"0px"}; 
			options["title"] = "<div>"+children[i]["name"]+"</div>"
			//options["content"] = '<table id="'+tmpid+'" class="easyui-datagrid"> </table>';
			//添加子表页签
			$("#ul_card_children_tabs").tabs("add",options);
			
			//根据不同的类型生成不同的表体
			if(!children[i]["uiType"] || children[i]["uiType"]=="grid")
			{
				var tab = $("#ul_card_children_tabs").tabs("getTab",children[i]["name"]);
				tab.panel({content:'<table id="'+tmpid+'" class="easyui-datagrid"> </table>'});
			}else
			{
				continue;
			}
			 
			
			//添加表体操作按钮
			toolbar = [];  
			if(children[i].buttons && children[i].buttons.length>0)
			{
				var tblBtnObj = null;
				for(var m=0;m<children[i].buttons.length;m++)
				{
					tblBtnObj = {handler: ul.view.datagrid.btnHandler};
					tblBtnObj.text = "<div table='"+tmpid+"' action='"+children[i].buttons[m].action+"'  >"+children[i].buttons[m].name+"</div>";
					tblBtnObj.iconCls = children[i].buttons[m].iconCls;
					toolbar.push(tblBtnObj);
				}
			}
			
			var columns = children[i]["fields"];
			for(var j=0;j<columns.length;j++)
			{
				columns[j]["editor"] = ul.view.datagrid.getCellEditor(columns[j]);
				columns[j]["editor"]["options"]["dgId"] = tmpid;  
				columns[j]["editor"]["options"]["fieldMeta"] = columns[j];//{refType:columns[j]["refType"],type:columns[j]["type"],id:columns[j]["field"],name:columns[j]["title"]};
			}
			
			//设置子表参数
			options = {emptyMsg:"还没有记录哟!", singleSelect:true,rownumbers:true,fit:true,fitColumns:true,nowrap:true};
			options["columns"] = [columns]; 
			options["toolbar"] = toolbar; 
			options["clickToEdit"] = false;
			options["dblclickToEdit"] = true;
			$(".datagrid-toolbar,.datagrid,.datagrid-wrap").css({"border-top-width":"0px"});
			
			//添加子表 
			$("#"+tmpid).datagrid(options).datagrid((children[i].editable==false)?"disableCellEditing":"enableCellEditing");
		  
			
		};  
		//$("#ul_card_children_tabs").tabs("disableTab",0);
		//$("#ul_card_children_tabs").tabs("disableTab",1); 
	};
	
	
	
	//初始化子表操控类
	ul.view.datagrid = {};
	ul.view.datagrid.handlerMap = {};
	ul.view.datagrid.btnHandler = function(param){
	 
		if(!param.target.attributes["table"])
		{
			return;
		}
		var dgId = param.target.attributes["table"].value;
		var action = param.target.attributes["action"].value; 
		var key = dgId+"-"+action;
		var params = {dgId:dgId,action:action,target:param.target};
		 
		if(ul.view.datagrid.handlerMap["before-"+key])
		{
			var prevent = ul.view.datagrid.handlerMap["before-"+key](param);
			if(prevent)
			{
				return;
			}
		}
		
		if(ul.view.datagrid.handlerMap[key])
		{
			ul.view.datagrid.handlerMap[key](params);
		}else if(ul.view.datagrid.handlerMap[action+"-default"])
		{
			ul.view.datagrid.handlerMap[action+"-default"](params);
		}
		
		if(ul.view.datagrid.handlerMap["after-"+key])
		{
			ul.view.datagrid.handlerMap["after-"+key](param); 
		}
	};
	
	ul.view.datagrid.handlerMap["addRow-default"] = function(params)
	{ 
		$("#"+params["dgId"]).datagrid("appendRow",{parentid:ul.model.getFieldValue("id")});
	};
	ul.view.datagrid.handlerMap["delRow-default"] = function(params)
	{
		var dg = $("#"+params["dgId"]);
		//var selected = dg.datagrid("getPanel").find(".datagrid-row-selected").attr("datagrid-row-index");
		var cell = dg.datagrid("cell");  
		if(cell)
		{
			dg.datagrid("deleteRow",cell.index); 
		}else{
			$.messager.alert("提示","请选择要删除的行!","info");
		} 
	};
	
	ul.view.datagrid.getCellEditor = function(column)
	{
		var editor = {options:{}};
		switch(column["type"])
		{
			case "select":
				editor["type"] = "combobox";
				break;
			case "ref":
				editor["type"] = "ref";
				break; 
			case "checkbox":
				editor["type"] = "checkbox";
				break; 
			default:
				editor["type"] = "textbox";
				break;
		}
		return editor;
	};
	
	
	ul.view.genEasyuiSearchBox = function(panelId,field)
	{
		var fieldWidth = field.width?field.width:"280px";
		var labelWidth = field.labelWidth?field.labelWidth:"80px";
		
		var strHtml = "<div id='fdiv_"+field["field"]+"' style='width:"+fieldWidth+"'>";
		strHtml +=  '<input class="easyui-searchbox" '; 
		
		strHtml += 'data-options="required:'+field["required"]+',editable:true,label:\''+field["title"]+'\',labelAlign:\'right\',labelPosition:\'left\', labelWidth:\''+labelWidth+'\',searcher:ul.view.refPanel.open" style="width:100%;height:30px;"';
		strHtml += ' id="'+field["field"]+'"'; 
		
		if(typeof(field["editable"])!="undefined" && !field["editable"])
		{
			strHtml += " readonly ";
		}
		strHtml += " name='"+field["field"] +"' ";
		 
		strHtml += ' /></div>';
		
		$("#"+panelId).append(strHtml);  
		//$("#"+field["id"]).data("fieldMeta",field);
	};
 
	ul.view.genHiddenComp = function(panelId,field)
	{
		var strHtml =  "<input type='hidden' id='"+field["field"] +"' />";
		$("#"+panelId).append(strHtml);
	};
	
	ul.view.genEasyuiTextareaComp = function(panelId,field)
	{
		var strHtml = null; 
		var fieldWidth = field.width?field.width:"280px";
		var labelWidth = field.labelWidth?field.labelWidth:"80px";
		var fieldHeigh = field.height?";height:"+field.height:";height:80px";
		var position = field.titlePosition?field.titlePosition:"left";
		var labelAlign =  field.labelAlign?field.labelAlign:"right";
		
		strHtml = "<div id='fdiv_"+field["field"]+"' style='width:"+fieldWidth+fieldHeigh+"'>";
		strHtml += '<input data-options="onChange:ul.handler.valueChange,required:'+field["required"]+',multiline:true" class="easyui-textbox easyui-tooltip" style="width:100%;height:100%;" labelAlign="'+labelAlign+'"  labelWidth="'+labelWidth+'" label="'+field.title+'" labelPosition="'+position+'"';
		strHtml += ' id="'+field["field"]+'"'; 
		 
		if(typeof(field["editable"])!="undefined" && !field["editable"])
		{
			strHtml += " readonly ";
		}
		
		strHtml += " name='"+field["field"] +"' ";
		 

		if(field["required"])
		{
			strHtml += " required ";
		}

		strHtml += " />";
		
		$("#"+panelId).append(strHtml);
	};
	
	ul.view.genEasyuiComboBoxComp = function(panelId,field)
	{
		var fieldWidth = field.width?field.width:"280px";
		var labelWidth = field.labelWidth?field.labelWidth:"80px";
		
		var strHtml = "<div id='fdiv_"+field["field"]+"' style='width:"+fieldWidth+"'>";
		var curId = field["field"];
		strHtml += '<select id="'+curId+'" class="easyui-combobox" ';
		strHtml += 'data-options="onChange:ul.handler.valueChange,editable:false,label:\''+field["title"]+'\',labelAlign:\'right\',labelPosition:\'left\', labelWidth:\''+labelWidth+'\'" style="width:100%;height:30px;">';
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
	};
	
	/**
	ul.view.genEasyuiComboComp = function(panelId,field)
	{
		var fieldWidth = field.width?field.width:"280px";
		var labelWidth = field.labelWidth?field.labelWidth:"80px";
		
		var strHtml = "<div id='fdiv_"+field["field"]+"' style='width:"+fieldWidth+"'>";

		//strHtml += '<div class="easyui-panel" style="border:none;width:100%;font-size:16px;" >';

		strHtml += '<input  id="'+field["field"]+'"  style="width:100%;height:30px;"';

		if(typeof(field["editable"])!="undefined" && !field["editable"])
		 {
			strHtml += " readonly ";
		 }else
		 {
			strHtml += " name='"+field["field"]+"' " ;
		 }
		 strHtml += ' />';
		 //strHtml += '</div>';
		 strHtml += "</div>";
		 $("#"+panelId).append(strHtml);

		 strHtml = '<div id="sp_'+field["field"]+'">';
		// strHtml += '<div style="line-height:22px;background:#fafafa;padding:5px;">请选择...</div>';
		 strHtml += '<div style="padding:10px">';
		 var curValue = null;
		 if(field.options.length!=0)
		 {
			 for(var i=0;i<field.options.length;i++)
			 {
				strHtml += '<input type="radio" style="height:16px;width:16px;" name="lang_'+field["field"]+'" value="'+field.options[i].id+'"';
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


		 $('#'+field["field"]).combo({
				required:field.required?true:false,
				editable:false,
				label:field["title"],
				labelPosition:'left',
				labelWidth:"60px",
				labelAlign:"right"
		});

		var curId = field["field"];
		$('#sp_'+curId).appendTo($('#'+curId).combo('panel'));


		$('#sp_'+curId+' input').click(function(){
			var v = $(this).val();
			var s = $(this).next('span').text();
			$('#'+curId).combo('setValue', v).combo('setText', s).combo('hidePanel');
		});

	};
	*/
	ul.view.genEasyuiTextComp = function(panelId,field)
	{
		var strHtml = null;
		var fieldWidth = field.width?field.width:"280px";
		var labelWidth = field.labelWidth?field.labelWidth:"80px";
		
		if(field["type"]=="hidden")
		{
			strHtml = "<input type='hidden' value='"+fieldValue+"'";
		}else
		{
			strHtml = "<div id='fdiv_"+field["field"]+"' style='width:"+fieldWidth+"' >";
			var appStr = "";
			var fieldClass = "easyui-textbox";
			if(field.dataType=="number")
			{
				fieldClass = "easyui-numberbox";
				if(field.precision)
				{
					appStr += " precision='2'";
				}
				if(field.groupSeparator)
				{
					appStr += " groupSeparator=','";
				}
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
			strHtml += '<input type="text" class="'+fieldClass+'" '+appStr+'    style="width:100%;height:30px;" label="'+field["title"]+'" labelPosition="left"';
			strHtml += ' labelWidth="'+labelWidth+'" labelAlign="right" data-options="onChange:ul.handler.valueChange,required:'+field["required"]+'"';

			strHtml += "  id='"+field["field"]+"'";
		}
		if(typeof(field["editable"])!="undefined" && !field["editable"])
		{
			strHtml += " readonly ";
		}else
		{
			strHtml += " name='"+field["field"] +"' ";
		} 

		strHtml += " />";
		$("#"+panelId).append(strHtml);
		
	}; 
	
	ul.view.generalListPanel = function(containerId,billTemp)
	{ 
		if(billTemp["fields"] && billTemp["fields"].length>0)
		{
			var strHeml = '<div class="easyui-panel" style="width100%;height:100%;border:0px;" id="panelHead"><table class="easyui-datagrid" id="tblHead"></table></div>';
			$("#"+containerId).append(strHeml);
			var options = {emptyMsg:"没有数据!",columns:[billTemp["fields"]],singleSelect:true,rownumbers:true, autoRowHeight:false,
					fitColumns:true,nowrap:true,fit:true,pagination:true,stripe:true,pageSize:10,pageList:[10],pageNumber:1};
			options["onSelect"] = function(index,row){ 
					ul.model.select(row,index);
			};
			options["onDblClickRow"] = function(index,row){ 
				ul.view.showCardPanel();
			};  
			
			$("#tblHead").datagrid(options); 
		} 
		
		var children = billTemp["children"];
		if(children&&children.length>0)
		{
			$("#panelHead").panel({height:"50%"});
			var strHtml = '<div class="easyui-tabs" id="ul_list_children_tabs" data-options="narrow:true" style="width:100%;height:50%;"></div>';
			$("#"+containerId).append(strHtml);
			$("#ul_list_children_tabs").tabs({border:false});
			var toolbar = null;
			for(var i=0;i<children.length;i++)
			{  
				tmpid = 'ul_children_'+children[i]["code"];
				 
				//设置子表页签属性
				//title:children[i]["name"],
				options = {title:children[i]["name"],border:false,fit:true};
				options["style"] ={borderTopWidth:"0px"}; 
				//options["title"] = "<div>"+children[i]["name"]+"</div>"
				//options["content"] = '<table id="'+tmpid+'" class="easyui-datagrid"> </table>';
				 
				//添加子表页签
				$("#ul_list_children_tabs").tabs("add",options);
				 
				if(!children[i]["uiType"] || children[i]["uiType"]=="grid")
				{
					var tab = $("#ul_list_children_tabs").tabs("getTab",children[i]["name"]);
					tab.panel({content:'<table id="'+tmpid+'" class="easyui-datagrid"> </table>'});
				}else
				{
					continue;
				}
				//生成按钮
				toolbar = [];  
				if(children[i].buttons && children[i].buttons.length>0)
				{
					var tblBtnObj = null;
					for(var m=0;m<children[i].buttons.length;m++)
					{
						if(!children[i].buttons[m].isList)
						{
							continue;
						}
						tblBtnObj = {handler: ul.view.datagrid.btnHandler};
						tblBtnObj.text = "<div table='"+tmpid+"' action='"+children[i].buttons[m].action+"'  >"+children[i].buttons[m].name+"</div>";
						tblBtnObj.iconCls = children[i].buttons[m].iconCls;
						toolbar.push(tblBtnObj);
					}
				}
			 
				//设置子表参数
				var columns = children[i]["fields"];
				options = {emptyMsg:"还没有记录哟!", singleSelect:true,rownumbers:true,fit:true,fitColumns:true,nowrap:true};
				options["columns"] = [columns];  
				options["clickToEdit"] = false;
				options["dblclickToEdit"] = false;
				options.toolbar = toolbar;
				$(".datagrid-toolbar,.datagrid,.datagrid-wrap").css({"border-top-width":"0px"});
				
				//添加子表
				$("#"+tmpid).datagrid(options).datagrid("enableCellEditing");
			  
			}; 
		}
	}; 

	ul.view.initialQueryPanel = function(containerId,queryTemp,handler) 
	{
		
		ul.appctx.attrs["queryMeta"] = queryTemp;
		
		strHtml = '<fieldset  id="querySet" style="margin-left:0px;margin-top:8%;border:none;"><legend id="billName"></legend></fieldset>';
		$("#"+containerId).append(strHtml);

		var panelId = "querySet";

		for(var i=0;i<queryTemp["fields"].length;i++)
		{
			var field = queryTemp["fields"][i]; 
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
		jQuery.parser.parse($("#"+containerId));
		
		var strBtn = "<a id='queryBtn' href='#' style=''>查  询</a>";
		$("#queryPanel").append(strBtn);
		$("#queryBtn").linkbutton({width:"80px",onClick:function(){
			
			if(ul.view.status!="default")
			{
				$.messager.alert("提示","请先取消编辑!","info");
				return;
			}
			
			var fields = queryTemp["fields"];
			var field = null;
			var busiData = {};
			var v = null;
			for(var i=0;i<fields.length;i++)
			{
				field = fields[i];
				v = ul.model.getFieldValue(field,{isTextValue:false});  
				v = ul.utils.trim(v);
				if(v)
				{
					busiData[field["field"].replace("query_","")] = v;
				}
			}  
			if(handler)
			{
				handler(busiData);
			}
		}});
	};
  	 
	ul.view.btnHandler = function(item)
	{ 
		var param = {};
		if(item)
		{
			param = item;
		}else{
			 
			param["id"] = this.id
			param["text"] = this.innerText;
			param["target"] = this; 
		}

		if(!param["id"]||param["id"].indexOf("btn_")!=0)
		{
			console.log("It's not exists or invalid id!");
			return;
		}
		var strId = param["id"].replace("btn_","");
		if(!ul.view.btnHandlerMap[strId])
		{
			console.log("defaultHandler handler for button: "+ul.utils.toJsonStr(param));
			//ul.view.defaultBtnHandler(param); 
			return;
		}
		ul.view.btnHandlerMap[strId](param);
	};
	
	ul.view.defaultBtnHandler = function(params){
		
		var qq9214_at = params["id"].replace("btn_","");
		var sendData = {};
		sendData["qq9214_bt"] = ul.appctx.attrs["qq9214_bt"];
		sendData["qq9214_at"] = qq9214_at;
		sendData["billData"] = ul.model.getBillValue();
		ul.view.appctx.sendRequest(sendData,{success:function(data){
			$.message.alert("信息","操作成功!","info");
			ul.model.disableBill(flag);
		}
		//,error:function(obj){}
		});
		
	};
	
	ul.view.btnHandlerMap = {};
	
	ul.view.addBtnHandler = function(name,func)
	{ 
		ul.view.btnHandlerMap[name]= func;
	};
	
	ul.view.btns = null;
	ul.view.initialBtns = function(btns,typename,parentid,childid)
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
		ul.view.btns = btns;
		
		if($('#btns_panel').length==0)
		{
			console.log("The div that id equals 'btns_panel' is not found!");
			return;
		}
		//按钮居中显示
		$('#btns_panel').css("text-align","center");
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

			if(ul.utils.hasChild(btns,tObj))
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
			if(ul.utils.hasChild(btns,btns[i]))
			{
				param["menu"] = "#"+strId+"_child";
				$('#btn_'+strId).menubutton(param);
				$(param["menu"]).menu(
					{
						onClick:ul.view.btnHandler
					}
				);
			}else{
				param["onClick"] = this.btnHandler;
				param["plain"] = true;
				$('#btn_'+strId).linkbutton(param);
			}
		}
	};

	ul.view.generalBtn = function(btns,curBtn,typename,parentid,childid)
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
				 if(ul.utils.hasChild(btns,btns[i]))
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
	};
	
	ul.view.textValuePair = function(param1,param2)
	{
		this.value = param1;
		this.text = param2; 
		this.toString = function()
		{
			return "<label data-value='"+this.value+"' >"+this.text+"</label>";
		};
	}; 

ul.view.pagination = {
			pageSize:10,
			pageNumber:0,
			pageList:[10],
			ids:[],
			total:0,
			data:{},  /**缓存已经加载的数据*/
			addIds:function(addIds){
				var cur = ul.view.pagination;
				if(!(addIds instanceof Array))
				{
					addIds = [addIds];
				}
				for(var i=0;i<addIds.length;i++)
				{
					cur.ids.push(addIds[i]);
				}
				cur.ids = ul.utils.distinct(cur.ids);
				ul.view.pagination.total = cur.ids.length;
			},
			delIds:function(delIds){
				var cur = ul.view.pagination;
				if(!(delIds instanceof Array))
				{
					delIds = [delIds];
				}
				for(var i=0;i<delIds.length;i++)
				{
					for(var j=0;j<cur.ids.length;j++)
					{
						if(delIds[i]==cur.ids[j])
						{
							cur.ids.splice(j,1);
							break;
						}
						
					}
				} 
				ul.view.pagination.total = cur.ids.length;
			},
			addData:function(beans){ 
				var cur = ul.view.pagination;
				if(beans instanceof Array)
				{
					for(var i=0;i<beans.length;i++)
					{
						cur.data[beans[i]["id"]] = beans[i]; 
					}
				}
				cur.data[beans["id"]] = beans; 
			},
			getCurrIds:function()
			{
				var cur = ul.view.pagination;
				var begin = (cur.pageNumber-1) * cur.pageSize;
				var len = begin+cur.pageSize;
				if(len>cur.ids.length)
				{
					len = cur.ids.length;
				}
				var curIds = [];
				if(cur.ids.length>0)
				{
					for(var i=begin;i<len;i++)
					{
						curIds.push(cur.ids[i]);
					}
				}
				return curIds;
			},
			showIdPage:function(id)
			{ 
				var cIndex = -1;
				var cur = ul.view.pagination;
				for(var i=cur.ids.length-1;i>=0;i--)
				{
					if(cur.ids[i]==id)
					{
						cIndex = i;
						break;
					}
				}
				var pages = cur.ids.length/cur.pageSize;
				if(cur.ids.length%cur.pageSize!=0)
				{
					pages++;
				} 
				
				for(var i=1;i<=pages;i++)
				{
					if(i*cur.pageSize>=cIndex)
					{ 
						cur.pageNumber = i;
						break;
					}
				}
				cur.queryData();
			},
			initialIds:function(ids)
			{
				ul.view.pagination.ids = ids;
				if(!ids)
				{
					ul.view.pagination.pageNumber = 0;
					ul.view.pagination.total = 0; 
					return;
				}
				ul.view.pagination.total = ids.length; 
				ul.view.pagination.pageNumber = ids?1:0;
				ul.view.pagination.queryData();
			},
			onSelectPage:function(pageNumber, pageSize)
			{
				if(pageSize)
				{
					ul.view.pagination.pageSize = pageSize;
				}
				ul.view.pagination.pageNumber = pageNumber;
				
				/**刷新加载动作会先触发onselectPage 后触发 onRefresh,因为 onRefresh也会加载数据，
				 * 所以为了避免重复加载数据,将onselectPage的加载数据操作屏蔽*/ 
				var src = ul.utils.getSrcElement();
				if(src)
				{
					if($(src).find(".pagination-load").length==0)
					{
						ul.view.pagination.queryData();
					};
				}else
				{
					ul.view.pagination.queryData();
				}
				
			},
			onRefresh:function(pageNumber, pageSize)
			{
				ul.view.pagination.queryData(true);
			},
			refresh:function(noCache)
			{
				ul.view.pagination.queryData(noCache);
			},
			queryData:function(noCache)
			{
				var cur = ul.view.pagination;
				var curIds = cur.getCurrIds();
				/**是否需要缓存，如果启用缓存，只查询未缓存的数据*/
				if(!noCache)
				{
					var needIds = [];
					for(var i=0;i<curIds.length;i++)
					{
						if(!cur.data[curIds[i]])
						{
							needIds.push(curIds[i]);
						}
					}
					curIds = needIds;
				}
				if(curIds.length==0)
				{
					/**使用本地缓存*/
					curIds = cur.getCurrIds();
					var nData = [];
					for(var i=0;i<curIds.length;i++)
					{
						nData.push(cur.data[curIds[i]]);
					} 
					ul.model.initData(nData); 
				}else{  
					/**远程加载数据*/
					var busiType = ul.appctx.attrs["qq9214_bt"]; 
					var queryParams = {qq9214_bt:busiType,qq9214_at:"qdatabids",busiData:{ids:curIds}};
					ul.appctx.sendRequest(queryParams,{dataType:"json",beforeSend:ul.utils.loading,complete:ul.utils.unloading,success:function(rspData){
						if(rspData["code"]!="success")
						{
							$.messager.alert("错误",rspData["msg"],"error");
							return;
						} 
						var tData =  rspData["busiData"];
						var cur = ul.view.pagination;
						cur.addData(tData); 
						
						/**如果启用了缓存，展现时是否需要加上缓存的数据*/
						if(!noCache)
						{
							var curIds = cur.getCurrIds();
							var nData = [];
							for(var i=0;i<curIds.length;i++)
							{
								nData.push(cur.data[curIds[i]]);
							}
							tData = nData;
						} 
						ul.model.initData(tData); 
					}});
				}
			} 
	};
	

ul.view.tree = {};
ul.view.tree.generalTreeData = function (datas,strId,strName,parentId,childId,curObj)
{ 
	var tmpObj = null;
	if(curObj)
	{
		for(var i=0;i<datas.length;i++)
		{
			var parentValue = datas[i][parentId];
			if(parentValue instanceof ul.view.textValuePair)
			{
				parentValue = parentValue["value"];
			}
			if(!parentValue)
			{
				continue;
			}
			if(parentValue==curObj[childId])
			{ 
				tmpObj = {id:datas[i][strId],text:datas[i][strName],attributes:datas[i]};
				if(!curObj["children"])
				{
					curObj["children"] = [];
				}
				curObj["children"].push(tmpObj);
				ul.view.tree.generalTreeData(datas,strId,strName,parentId,childId,tmpObj);
			}
		}
	}else{
		var tree = [];
		for(var i=0;i<datas.length;i++)
		{
			if(datas[i][parentId]==""||datas[i][parentId]==null)
			{
				tmpObj = {id:datas[i][strId],text:datas[i][strName],attributes:datas[i]};
				tree.push(tmpObj); 
				ul.view.tree.generalTreeData(datas,strId,strName,parentId,childId,tmpObj);
			}	
		}
		return tree;
	}   
}; 

ul.view.tree.jsonToTree = function(json,parentName)
{  
	var obj = {};
	obj.children = [];
	if(json instanceof Array)
	{ 
	    obj.name = parentName;
		obj.type = "array"; 
		var title = "";
		for(var i=0;i<json.length;i++)
		{ 
			title = "";
			if(typeof(json[i])=="object" || json[i] instanceof Array)
			{ 
				if(typeof(json[i])=="object" && json[i].title)
				{
					title = json[i].title;
				}
				obj.children.push(ul.view.tree.jsonToTree(json[i],title));
			} 
		}
	}else if(typeof(json)=="object")
	{ 
	    obj.name = parentName;
		obj.type = "object";
		for(var n in json)
		{
			if(typeof(json[n])=="object" || json[n] instanceof Array)
			{
				obj.children.push(ul.view.tree.jsonToTree(json[n],n));
			}else{ 
				obj.children.push({name:n,value:json[n],type:typeof(json[n])});
			}
		}
	}
	return obj;
};	

ul.view.tree.treeToJson = function(rootNode)
{
	var node = rootNode;
	var obj = null;
	if(node["type"]=="object")
	{
		obj = {};
	}else if(node["type"]=="array")
	{
		obj = [];
	}else{
		console.log("错误的类型");
		console.log(node);
	}
	
	if(node["children"])
	{
		var child = null;
		var ret = null;
		
		for(var i=0;i<node["children"].length;i++)
		{
			child = node["children"][i]; 
			if(child.type=="object" || child.type=="array")
			{
				var ret = ul.view.tree.treeToJson(child);
			}else{
				ret = child.value;
			}
			if(node.type=="object")
			{
				obj[child.name] = ret;
			}else{
				obj.push(ret);
			}
		}
	}
	return obj;
};

}
