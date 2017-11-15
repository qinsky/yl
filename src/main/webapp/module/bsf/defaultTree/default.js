function initial()
{ 
	//ul.initial.loadRes();
	ul.initial.tree.initialEventHandler();
	ul.initial.tree.initialUi();
	//ul.initial.tree.initialButtons();  放到initialFields 里面 
	ul.initial.tree.initialData(); 
	//initialHandler();
	//console.log($("#ul_children_user"));
};
  
/**
function initialHandler()
{ 
	function beforeValid()
	{
		if(ul.view.status!="add" && ul.view.status!="edit")
		{
			$.messager.alert("错误","非编辑状态不能增加或删除行!");
			return true;
		}
	};
	ul.view.datagrid.handlerMap["before_ul_children_function_addRow"] = beforeValid;
	ul.view.datagrid.handlerMap["before_ul_children_user_addRow"] = beforeValid;
	ul.view.datagrid.handlerMap["before_ul_children_function_delRow"] = beforeValid;
	ul.view.datagrid.handlerMap["before_ul_children_user_delRow"] = beforeValid;
}*/

$(initial);
