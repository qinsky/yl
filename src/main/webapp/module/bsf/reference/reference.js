function initial()
{
	initialListener();
	initialBtn(); 
	ul.initial.initialUi();  
	//ul.initial.initialQuery(); 
}; 
function initialListener()
{
	ul.initial.initialEventHandler();
}

function initialBtn()
{
	var btns = [{id:"add",qq9214_at:"add",name:"增加",enableStatus:["default"]},
	            {id:"update",qq9214_at:"update",name:"修改",enableStatus:["default"]},
	{id:"save",qq9214_at:"save",name:"保存",enableStatus:["add","edit"]},
	{id:"cancel",qq9214_at:"cancel",name:"取消",enableStatus:["add","edit"]},
	{id:"delete",qq9214_at:"delete",name:"删除",enableStatus:["default"]}, 
	{id:"refresh",qq9214_at:"refresh",name:"刷新",enableStatus:["default"]}
	];
	/** {"id":"test3","qq9214_at":"delete1","name":"删除1","parent_id":"test2"} */
	
	ul.view.initialBtns(btns); 
	var vReadonly  = true; 
	//ul.view.addBtnHandler("add",addHandler);
	//初始化按钮处理功能
	ul.initial.initialActionHandler();
};
 
$(initial);