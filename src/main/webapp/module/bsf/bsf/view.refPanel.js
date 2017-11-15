ul.view.refPanel = {
		fieldMeta:null
		,content:null
		,control:null
		,comp:null
		,open:function (p1, fName) {
			
			var curPath = ul.view.refPanel; 
			
			curPath.fieldMeta = $(this).data("fieldMeta");
			if (!curPath.fieldMeta) { 
				if(fName)
				{
					if(fName.indexOf("query_")==0)
					{
						curPath.fieldMeta = ul.appctx.getQueryFieldMeta(fName.replace("query_",""));
					}else
					{
						curPath.fieldMeta = ul.appctx.getFieldMeta(fName);
					}
				}  
			}  
			
			if(!curPath.fieldMeta)
			{
				console.log("Missing fieldMeta!");
				return;
			}
			 
			control = this;
			var sendData = {qq9214_bt : "reference",qq9214_at:"qmeta", busiData : {refType : curPath.fieldMeta["refType"]}}; 
			//请求参照数据
			ul.appctx.sendRequest(sendData, {success : curPath.rspHandler,beforeSend:function(){
				this.fieldMeta = curPath.fieldMeta;
			}}); 
		},
		/** 异步加载数据后处理类 */
		rspHandler:function(rspData){
			 
			ul.appctx.validRsp(rspData);
			
			var meta =  rspData.busiData.meta;
			
			var curPath = ul.view.refPanel;
			
			var comp = curPath.refComp[meta.type];
			
			if(!comp){
				console.log("The reftype has no showComponent!");
				return;
			} 
			curPath.comp = comp;
			
			var options = {}
			if (!curPath.content||curPath.content.length == 0) {
				var strPanel = '<div id="refPanel"  style="width:56%;height:66%;" ></div>';
				$("body").append(strPanel);

				 options = {
					closed : true, resizable : true, maximizable : false, cache : false, modal : true,
					buttons : [{ text : '确定', handler : ul.view.refPanel.ok }, { text : '取消', handler : ul.view.refPanel.cancel}
					]
				};
				 
				 curPath.content = $("#refPanel");  
			};
			
			options["title"] = meta["title"];
			curPath.content.dialog(options);  
			curPath.content.empty(); 
			 
			comp.initialComp(meta,curPath.content);
			
			var tFieldMeta = this.fieldMeta;
			if(meta["autoLoad"]==true)
			{
				var sendData = {qq9214_bt : "reference",qq9214_at:"qdata",busiData : { refType : tFieldMeta["refType"]}}; 
				//请求参照数据
				ul.appctx.sendRequest(sendData, {beforeSend:function(){
					this.callbackParams = {"comp":comp,"content":curPath.content};
				},success : function(refData){ 
					ul.appctx.validRsp(refData); 
					var cp = this.callbackParams;
					cp.comp.initialData(refData.busiData,cp.content); 
				}}); 
			} 
			curPath.content.dialog({closed:false});
			
		},
		ok:function()
		{
			var curPath = ul.view.refPanel; 
			var retObj = curPath.comp.getValue(curPath.content);
			if(!retObj)
			{
				$.messager.alert("提示","没有选择数据",'error');
				return;
			}
			var tv = retObj.tv;
			var fieldComp = $(control);
			var oldTv = new ul.view.textValuePair(fieldComp.data("refValue"),fieldComp.data("refText"));
			fieldComp.data("refValue",tv["value"]==null?"":tv["value"]);
			fieldComp.data("refText",tv["text"]==null?"":tv["text"]);
			fieldComp.data("refBean",retObj.refBean);
			fieldComp.searchbox("setText",tv["text"]==null?"":tv["text"]);
			//ul.model.setFieldValue(ul.view.refPanel.fieldMeta,tv); 
			
			/**缓存选择的数据*/
			var key = curPath.fieldMeta["refType"]+"-"+tv["value"];
			var cacheData = tv.text;
			ul.cache.add(key,cacheData);
			
			ul.view.refPanel.content.dialog({closed:true});
			
			if(tv.value!=oldTv.value)
			{
				ul.handler.valueChange.call(fieldComp[0],tv,oldTv);
			}
		},
		cancel:function()
		{
			console.log("cancel");
			ul.view.refPanel.content.dialog({closed:true});
		},
		refTextParam:null,
		getRefText:function(reqParam)
		{
			var tv = null;
			ul.view.refPanel.refTextParam = reqParam;
			var key = reqParam["fieldMeta"]["refType"]+"-"+reqParam["ids"][0]
			var cacheText = ul.cache.get(key);

			 
			if(cacheText)
			{
				tv = new ul.view.textValuePair(reqParam["ids"][0],cacheText);
				return tv;
			}
			
			var sendData = {
				qq9214_bt : "reference",
				qq9214_at : "qtext", 
				busiData : { 
					//action:"text",
					refType : reqParam["fieldMeta"]["refType"], 
					ids : reqParam["ids"]
				}
			}; 
			
			//请求参照数据
			ul.appctx.sendRequest(sendData, {async:false, success : function(rspData){
					   ul.appctx.validRsp(rspData);
					   tv = ul.view.refPanel.getRefTextHandler(rspData.busiData); 
					} 
			});  
			
			/**缓存数据*/
			if(tv && tv["text"])
			{
				ul.cache.add(key,tv["text"]);
			}
			return tv;
		},
		getRefTextHandler:function(busiData)
		{    
			var data = busiData; 
//			var reqParam = ul.view.refPanel.refTextParam;
			if(data.length==0)
			{
				return null;
			}
				
			var refData = data[0];   
			var tv = new ul.view.textValuePair();
			tv["text"] = refData["reftext"];
			tv["value"] = refData["refvalue"];
			/**
			var columns = busiData.meta.fields;
			for(var i=0;i<columns.length;i++)
			{
				if(columns[i]["refText"]==true)
				{
					tv["text"] = refData[columns[i]["field"]];
					continue;
				}
				if(columns[i]["refValue"]==true)
				{
					tv["value"] = refData[columns[i]["field"]];
					continue;
				} 
			}; */
			//if(!reqParam.isField)
			//{
				return tv; 
			//}else
			//{
				//如果是普通字段，获取到参照内容之后，直接赋值
				//var fieldMeta = reqParam["fieldMeta"];
				//reqParam.bean[fieldMeta["field"]] = tv; 
				//ul.model.setFieldValue(fieldMeta,tv); 
			//	return tv;
			//} 
		}
	};

ul.view.refPanel.refComp = {
		grid:{
			initialComp:function(meta,container){
				container.html('<table class="easyui-datagrid" ></table>');
				var tbOptions = {
					emptyMsg : "还没有记录哟!", selectOnCheck : true,
					checkOnSelect : true, singleSelect : true,
					rownumbers : true, fit : true, fitColumns : true, nowrap : true,
					onDblClickRow:ul.view.refPanel.ok
				};
				 
				meta["fields"].splice(0,0,{field:"select",title:"选择",checkbox:true});
				tbOptions["columns"] = [meta["fields"]]; 
				container.find(".easyui-datagrid").datagrid(tbOptions);
			},
			initialData:function(busiData,container){
				var grid = container.find(".easyui-datagrid"); 
				grid.datagrid({data:busiData});
			},
			getValue:function(container){
				var refTable = container.find(".easyui-datagrid");
				var selData = refTable.datagrid("getSelected");
				if(selData==null){ 
					return null;
				}
				var options = refTable.datagrid("options");
				var columns = options.columns[0];
				var tv = new ul.view.textValuePair();
				for(var i=0;i<columns.length;i++) {
					if(columns[i]["refText"]==true) {
						tv["text"] = selData[columns[i]["field"]]; 
					}
					if(columns[i]["refValue"]==true) {
						tv["value"] = selData[columns[i]["field"]]; 
					} 
				}; 
				return {tv:tv,refBean:selData};
			},
			setValue:function(value){
				
			}
		},
		tree:{
			
			initialComp:function(meta,container){
				container.html("<ul id='menuTree' class='easyui-tree' data-options='lines:true,onDblClick:ul.view.refPanel.ok'></ul>");
				var tbOptions = {meta:meta}; 
				container.find(".easyui-tree").tree(tbOptions);
			},
			initialData:function(busiData,container){
				var varTree = container.find(".easyui-tree");  
				var menuData = ul.view.tree.generalTreeData(busiData,"id","name","parent_id","id"); 
				varTree.tree({data:menuData});
			},
			getValue:function(container){
				var varTree = container.find(".easyui-tree");
				var selNode = varTree.tree("getSelected"); 
				if(selNode==null){  
					return null;
				}
				var selData = selNode.attributes;
				var options = varTree.tree("options");
				var columns = options.meta.fields;
				var tv = new ul.view.textValuePair();
				for(var i=0;i<columns.length;i++) {
					if(columns[i]["refText"]==true) {
						tv["text"] = selData[columns[i]["field"]];
						continue;
					}
					if(columns[i]["refValue"]==true) {
						tv["value"] = selData[columns[i]["field"]];
						continue;
					} 
				}; 
				return {tv:tv,refBean:selData};
			},
			setValue:function(value){
				
			}
		}
};