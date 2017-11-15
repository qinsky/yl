ul.model.addListener("before_initial_buttons",function(eventType,obj){
	 
	var btns = obj.data;
	
	ul.utils.delEles(btns,[{id:"update"},{id:"delete"}]);
	/**
	for(var i=0;i<btns.length;i++)
	{
		console.log(btns[i]["id"]);
		if(btns[i]["id"]=="update" || btns[i]["id"]=="delete")
		{
			btns.splice(i,1);
		}
	}
	*/
});