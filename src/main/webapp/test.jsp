<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE>
<html>
  <head>
    <title>首页</title>
    <meta charset=utf-8">
	 <%@include file="/module/bsf/bsf/comm.jsp"%>
    <style type="text/css">
    	  
    </style>

	<script type="text/javascript">
	 
	</script>

  </head>
  
  <body style="padding:0px;">
  	<!-- 
	 <a class="easyui-linkbutton" onclick="javascript:saveData();"> 存储数据</a>&nbsp;&nbsp;<a class="easyui-linkbutton"  onclick="javascript:console.log(showData());" > 显示数据</a>
    -->
    <div id="layout" class="easyui-layout" style="width:100%;height:100%;padding:0px;margin:0px" >
    		   <div data-options="region:'center',height:200" style="margin:0px;padding:0px;">aaaaa</div>
    		    <div data-options="title:'south',region:'south',split:true,collapsible:true,collapsedSize:10" style="height:50%">
    		    <a class="easyui-linkbutton" onclick="javascript:addNorth();"> addNorth</a>
    		    </div>
    </div>
  </body>
  <script type="text/javascript">
	  function addNorth()
	  {
		  var center = $('#layout').find(".layout-panel-center").find(".panel-body");
		  center.append("1233242");
		  //console.log($('#layout').find(".layout-panel-center"));
		  $('#layout').layout('add',{
			    region: 'north',
			    width: '100%',
			    height:'20%', 
			    split: true 
			});
	  }
      /**
	  var db = null;
	  console.log(window.indexedDB);
	  if (!window.indexedDB) {
	      window.indexedDB = window.mozIndexedDB || window.webkitIndexedDB;  
	  }
	  var request = indexedDB.open("test",4);
	 request.onerror = function(e) {
	      console.log("Database error: ");
	      console.log(e);
	   };
	   request.onsuccess = function(event) {
		    console.log("Database success: ");
		    console.log(event);
	     	db = request.result;
	   };
	   request.onupgradeneeded = function (evt) {
		   console.log("call onupgradeneeded......");
// 		      var employeeStore = evt.currentTarget.result.createObjectStore
// 		         ("employees", {keyPath: "id"});
			  db = evt.currentTarget.result;
		      if(!db.objectStoreNames.contains('refCache')){
		    	  db.createObjectStore("refCache",{ keyPath: "refType" });
              }
		};
		var testData = {refType:"user",data:[{id:"HHJHKJIUJJ",text:"叶兵",refBean:{id:"HHJHKJIUJJ",text:"叶兵"}}]};
		function saveData()
		{  
			console.time("aaa");
			 
			     db.transaction("refCache", "readwrite").objectStore("refCache").add(document).onerror=function(event){
				 console.log(event.target.error);  
			 };
			 console.timeEnd("aaa");
			// console.log("添加数据成功!");
			  
		}
		function showData()
		{
			var v = 0;
			var request = db.transaction("refCache", "readwrite").objectStore("refCache").get("user");
			request.name = "ok";
			request.onsuccess=function(event){
				console.log(this);
				console.group("grou log");
				console.log("123443");
				console.log(event.target.result);
				v = event.target.result; 
				console.groupEnd();
			} 
			
			console.log(request);
			sleep(1000); 
			return v;
		} 
	  	console.log(request);*/
  </script> 
</html>
