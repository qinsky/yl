
/** 添加工具类 */
ul.utils = {};

ul.utils.delEles = function(btns,dels){
	
	for(var i=0;i<btns.length;i++)
	{
		var flag = false;
		for(var j=0;j<dels.length;j++)
		{
			for(var n in dels[j])
			{
				if(btns[i][n]==dels[j][n])
				{
					btns.splice(i,1);
					flag = true;
					break;
				}
			}
			if(flag)
			{
				break;
			}
		}
	}
};

ul.utils.join = function(f1,f2)
{
	return function(data){
		f1.call(this,data);
		f2.call(this,data);
	};
};

ul.utils.getSrcElement=function()
{
	var ce = event?event:window.event;
	if(ce)
	{
		return ce.target?ce.target:ce.srcElement;
	}
	return null;
};
ul.utils.distinct = function(array) {
	 var r = []; 
	 for(var i = 0, l = array.length; i < l; i++) { 
		 for(var j = i + 1; j < l; j++) 
		  if (array[i] === array[j]) j = ++i; 
		 r.push(array[i]); 
	 } 
	return r;  
};

ul.utils.isEmpty = function(str)
{
	if(!str||ul.utils.trim(str)=="")
	{
		return true;
	}
	return false;
}
ul.utils.trim = function(str)
{
	return str.replace(/(^\s*)|(\s*$)/g, "");
};
ul.utils.trimLeft = function(str)
{
	return str.replace(/(^\s*)/g, "");
};
ul.utils.trimRight = function(str)
{
	return str.replace(/(\s*$)/g, "");
};

/**克隆一个对象*/
ul.utils.clone = function(cobj)
{
	var obj = null;
	if(cobj==null)
	{
		return null;
	}
	if(cobj instanceof Array)
	{
		obj = [];
		for(var i=0;i<cobj.length;i++)
		{
			obj.push(ul.utils.clone(cobj[i]));
		}
	}else{ 
		obj = new cobj.constructor();
		for(var name in cobj)
		{  
			switch(typeof(cobj[name]))
			{
				case "object":
					obj[name] = ul.utils.clone(cobj[name]);
					break;   
				default:
					obj[name] = cobj[name];
					break;
			} 
		}
	}
	return obj;
};

/**判断一个记录在一个集合中是否存在下级*/
ul.utils.hasChild = function(btns,curBtn,parentid,childid)
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
ul.utils.toJsonStr = function(obj)
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
				strRet += ul.utils.toJsonStr(obj[i])+",";
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
				strRet += "\"\",";
			}else if(typeof(obj[name]) == "string")
			{
				//将值中的引号转义
				strRet += "\""+(obj[name].indexOf("\"")<0?obj[name]:obj[name].replace(new RegExp("\"","gm"),"\\\""));
				strRet += "\",";
			}else if(typeof(obj[name]) == "number"||typeof(obj[name]) == "boolean")
			{
				strRet += obj[name]+",";
			}else{
				strRet += ul.utils.toJsonStr(obj[name])+",";
			}
		};
		if(strRet.indexOf(",")>=0)
		{
			strRet = strRet.substr(0,strRet.length-1);
		};
		strRet += "}"
	}else if(typeof(obj)=="string")
	{
		return obj;
	}
	return strRet;
};

//判断一个对象是否含有某个属性
ul.utils.hasAttr = function(obj,attr)
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
	
	return true;
};

//判断一个对象是否含有某个属性
ul.utils.isEmpty = function(obj)
{ 
	for(n in obj)
	{
		return false;
	}
	return true;
};

ul.utils.loading = function()
{ 
	
	$.blockUI();
	///{message:'<img src="busy.gif" />',css:{backgroundColor:'#9DA6B3',border:'0px'}} 
//	$.blockUI({message:'<img src="busy.gif" />',css:{backgroundColor:'#F3F3F3',border:'0px'}} );
	//$.blockUI({message:'<img src="loading.gif" />',css:{backgroundColor:'red',border:'0px'}} );
	//$.blockUI({message:'<img src="loading.gif" />',css:{backgroundColor:'rgb(244,244,244)',border:'0px'}} );
};

ul.utils.unloading = function()
{	 
	$.unblockUI();
};