/**初始化页面相关的函数*/ 
(function($){ 
	$.ylview = function(){};
	
	$.ylview.generalFields = function(panelId,billTemp)
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
		
		for(var i=0;i<billTemp["fields"].length;i++)
		{
			var field = billTemp["fields"][i];
			if(field.groupid)
			{
				mapFields[field.groupid].push(field);
				continue;
			}
			 
			if(field.type=="text")
			{ 
				$.ylview.genEasyuiTextComp(panelId,field);
			}else if(field.type=="select")
			{ 
				$.ylview.genEasyuiSelectComp(panelId,field);  
			}else if(field.type=="textarea")
			{
				$.ylview.genEasyuiTextareaComp(panelId,field);
			};
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
				$("#fm1").append(fieldsets);
				
				for(var j=0;j<tfields.length;j++)
				{
					field = tfields[j]; 
					
					if(field.type=="text")
					{ 
						genEasyuiTextComp(tfieldSetId,field);
					}else if(field.type=="select")
					{ 
						genEasyuiSelectComp(tfieldSetId,field);  
					}else if(field.type=="textarea")
					{
						genEasyuiTextareaComp(tfieldSetId,field);
					}; 
				};
			};
		}
	};
	
	$.ylview.genEasyuiTextareaComp = function(panelId,field)
	{
		var fieldValue = field.value?field.value:"";
		var strHtml = null;
		if(field.ishidden)
		{
			strHtml = "<input type='hidden' value='"+fieldValue+"'"; 
		}else
		{ 
			strHtml = "<div id='fdiv_"+field.id+"' >";
			strHtml += '<input data-options="multiline:true" class="easyui-textbox easyui-tooltip" style="width:100%;height:40px;" label="'+field.name+'" labelPosition="left"';
			strHtml += "' value='"+fieldValue+"'" +" id='"+field.id+"'";
			strHtml += " title='"+fieldValue+"' ";
		}
		if(field.editable=="false")
		{
			strHtml += " readonly "; 
		}else
		{
			strHtml += " name='"+field.id +"' ";
		}
		
		if(field.required=="true")
		{
			strHtml += " required "; 
		}
		
		strHtml += " />"; 
		$("#"+panelId).append(strHtml);
	}; 	
	
	$.ylview.genEasyuiSelectComp = function(panelId,field)
	{
		var strHtml = "<div id='fdiv_"+field.id+"'>";
		 
		//strHtml += '<div class="easyui-panel" style="border:none;width:100%;font-size:16px;" >';
		 
		strHtml += '<input  id="'+field.id+'"  style="width:100%;height:30px;"';
	   
		if(field.editable=="false")
		 {
			strHtml += " readonly "; 
		 }else
		 {
			strHtml += " name='"+field.id+"' " ;
		 }
		 strHtml += ' />';
		 //strHtml += '</div>';
		 strHtml += "</div>";
		 $("#"+panelId).append(strHtml);
		 
		 strHtml = '<div id="sp_'+field.id+'">';
		// strHtml += '<div style="line-height:22px;background:#fafafa;padding:5px;">请选择...</div>';
		 strHtml += '<div style="padding:10px">';
		 var curValue = null;
		 if(field.options.length!=0)
		 {
			 for(var i=0;i<field.options.length;i++)
			 {
				strHtml += '<input type="radio" style="height:16px;width:16px;" name="lang_'+field.id+'" value="'+field.options[i].id+'"';
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
		
		   
		 $('#'+field.id).combo({
				required:field.required=="true"?true:false,
				editable:false,	                		
				label:field.name,
				labelPosition:'left'
		});
	 
		$('#sp_'+field.id).appendTo($('#'+field.id).combo('panel'));
		var curId = field.id;
	   
		if(curValue)
		{
			 $('#'+curId).combo('setValue', curValue.id).combo('setText', curValue.text).combo('hidePanel');
		}
	   
		$('#sp_'+field.id+' input').click(function(){
			var v = $(this).val(); 
			var s = $(this).next('span').text(); 
			$('#'+curId).combo('setValue', v).combo('setText', s).combo('hidePanel');
		});
				 
	};
	
	$.ylview.genEasyuiTextComp = function(panelId,field)
	{
		var fieldValue = field.value?field.value:"";
		var strHtml = null;
		if(field.ishidden)
		{
			strHtml = "<input type='hidden' value='"+fieldValue+"'"; 
		}else
		{ 
			strHtml = "<div id='fdiv_"+field.id+"'>";
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
			strHtml += '<input type="text" class="'+fieldClass+'" placeholder="'+field.name+'" style="width:100%;height:30px;" label="'+field.name+'" labelPosition="left"';
			strHtml += " value='"+fieldValue+"'" +" id='"+field.id+"'";   
		}
		if(field.editable=="false")
		{
			strHtml += " readonly "; 
		}else
		{
			strHtml += " name='"+field.id +"' ";
		}
		
		if(field.required=="true")
		{
			strHtml += " required "; 
		}
		
		strHtml += " />"; 
		$("#"+panelId).append(strHtml);
	}; 

	/**分发按钮点击事件*/
	$.ylview.btnHandler = function(item)
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
			console.log("It's no handler for button: "+$.ylview.utils.toJsonStr(param));
			return;
		} 
		$.ylview.btnHandlerMap[strId](param); 
	};
	
	$.ylview.btnHandlerMap = {};
	$.ylview.addBtnHandler = function(name,func)
	{
		$.ylview.btnHandlerMap[name]= func;
	};
	
	/**生成菜单*/
	$.ylview.initialBtns = function(btns,typename,parentid,childid)
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
				strChild += $.ylview.generalBtn(btns,tObj,at,parentId,childId) + '</div>';
				
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
				param["onClick"] = $.ylview.btnHandler;
				param["plain"] = true;
				$('#btn_'+strId).linkbutton(param);
			}  
		} 
	};
	
	/**生成下级菜单*/
	$.ylview.generalBtn = function(btns,curBtn,typename,parentid,childid)
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
					strBtn += generalBtn(btns,btns[i]); 
				 }else{
					strBtn += btns[i]["name"]; 
				 } 
				 strBtn += "</div>";
			 }
		 }
		 return strBtn;
	};
})(jQuery);
  
/** 添加工具类 */
(function($){ 

	$.ylview.utils = function(){};
		
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
		return obj;
	}; 
	
})(jQuery);
 