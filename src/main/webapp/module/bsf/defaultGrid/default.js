function initial()
{
	//ul.initial.loadRes();
	ul.initial.initialEventHandler();
	//ul.initial.initialButtons();  //放到   ul.initial.initialFields 里面
	ul.initial.initialUi();  
	//ul.initial.initialQuery(); 
};  

$(initial);