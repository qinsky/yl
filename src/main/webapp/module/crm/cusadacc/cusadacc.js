
var queryTemp = {
		fields:[{
			field:"query_code",
			title:"编码",
			type:"text"
		},{
			field:"query_name",
			title:"名称",
			type:"text"
		}]
}

ul.model.addListener("before_initial_buttons",function(eventType,obj){
	 /**
	var btns = obj.data;
	btns.push({id:"capdata",qq9214_at:"capdata",name:"抓取数据",enableStatus:["default"]});
	ul.view.addBtnHandler("authcode",function(){
		
		var curBean =  ul.model.getSelBean();
		var sendData = {qq9214_at:"capdata",busiData:curBean}
		ul.appctx.sendRequest(sendData,{success:function(){
			
		}});
		
	});  */
});