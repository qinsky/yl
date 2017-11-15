$.extend($.fn.datagrid.methods, { 
	getChangesExt: function(jq){
		 var status = ["inserted","deleted","updated"];
		 var tmpRows = null;
		 var rows = [];
		 for(var n=0;n<status.length;n++)
		 {
			 tmpRows = jq.datagrid("getChanges",status[n]);
			 for(var j=0;j<tmpRows.length;j++)
			 {
				 tmpRows[j]["rowStatus"] = status[n];
				 rows.push(tmpRows[j]);
			 }
		 }
		 return rows;
	},
	getDataExt: function(jq){
		 
		 var data = jq.datagrid("getData");
		 var changes = jq.datagrid("getChangesExt");
		 for(var i=0;i<changes.length;i++)
		 {
			 if(changes[i]["rowStatus"]=="deleted")
			 {
				 data["rows"].push(changes[i]);
				 data["length"]++;
			 } 
		 }
		 for(var i=0;i<data["rows"].length;i++)
		 {
			 if(!data["rows"][i]["rowStatus"])
			 {
				 data["rows"][i]["rowStatus"] = "normal";
			 }
		 }
		 return data;
	}
});

$.extend($.fn.datagrid.defaults.editors, {
			
			combobox: {
				init: function(container, options){
					var strHtml = ""; 
					this.opts = options;
					strHtml += '<select  class="easyui-combobox" ';
					strHtml += 'style="width:'+container.width()+'px;height:100%;">';
					var curOption = null;
					var fieldMeta  = options["fieldMeta"];
					console.log(fieldMeta);
					if(fieldMeta.options && fieldMeta.options.length!=0)
					{
						for(var i=0;i<fieldMeta.options.length;i++)
						{
							curOption = fieldMeta.options[i];
							strHtml += '<option value="'+curOption["id"]+'">';
							strHtml += curOption["text"]+'</option>';
						};
					};
					strHtml += "</select>";
					var input = $(strHtml).appendTo(container);
					$.parser.parse(container); 
					
					return input;
				},
				destroy: function(target){
					$(target).remove();
				},
				getValue: function(target){ 
					var value = target.combobox("getValue"); 
					var text = target.combobox('getText');
					/** 如果文本框不为空，则取refText的值做为文本，因为text可能被用户手工修改*/
					if((value&&value!="") && (text&&text!=""))
					{  
						return new ul.view.textValuePair(value,text);
					}
					/**如果引用的值或者文本框的值为空，则认为是被用户手工清空或者是没有选择参照*/
					return null;
				},
				setValue: function(target, value){
					if(value instanceof ul.view.textValuePair)
					{
						this.oldValue = value;
						target.combobox("setValue",value["value"]);  
					}else if(value!=null && $.trim(value).indexOf("<label")==0)
					{
						var ele = jQuery.parseHTML(value);
						this.oldValue = new ul.view.textValuePair(ele[0].dataset.value,ele[0].innerText);
						target.combobox("setValue",this.oldValue["value"]); 
					}else
					{
						target.combobox("setValue",value); 
					} 
				},
				resize: function(target, width){
					$(target)._outerWidth(width);
				}
			}
		});

$.extend($.fn.datagrid.defaults.editors, {
	
	ref: {
		init: function(container, options){
			var strHtml = ""; 
			this.opts = options; 
			this.oldValue = null;
		 
			strHtml += '<input class="easyui-searchbox" data-options="searcher:ul.view.refPanel.open" ';
			strHtml += 'style="width:'+container.width()+'px;height:100%;" >';
			 
			var editor = $(strHtml).appendTo(container);
			editor.data("fieldMeta",options["fieldMeta"]); 
			$.parser.parse(container);
			
			return editor;
		},
		destroy: function(target){
			target.remove();
		},
		getValue: function(target){ 
			var value = target.data("refValue"); 
			var text = target.searchbox('getText');
			/** 如果文本框不为空，则取refText的值做为文本，因为text可能被用户手工修改*/
			if((value&&value!="") && (text&&text!=""))
			{ 
				text = target.data("refText");
				return new ul.view.textValuePair(value,text);
			}
			/**如果引用的值或者文本框的值为空，则认为是被用户手工清空或者是没有选择参照*/
			target.data("refValue","");
			target.searchbox('setText',"");
			return null;
		},
		setValue: function(target, value){ 
			if(value instanceof ul.view.textValuePair)
			{
				this.oldValue = value;
				target.searchbox("setText",value["text"]);
				target.data("refValue",value["value"]); 
				target.data("refText",value["text"]); 
			}else if(value!=null && $.trim(value).indexOf("<label")==0)
			{
				var ele = jQuery.parseHTML(value);
				this.oldValue = new ul.view.textValuePair(ele[0].dataset.value,ele[0].innerText);
				target.searchbox("setText",this.oldValue["text"]);
				target.data("refValue",this.oldValue["value"]);
				target.data("refText",this.oldValue["text"]);
			}else
			{
				target.searchbox("setText","");
				target.data("refValue","");
				target.data("refText","");
			} 
		},
		resize: function(target, width){
			$(target)._outerWidth(width);
		}
	}
});