ul.model.addListener("recBankAcc_value_changed",function(event,param){
	var refBean = $(param.comp).data("refBean");
	//设置收款户名
	ul.model.setFieldValue("accName",refBean.acc_name);
	//设置收款银行地址
	ul.model.setFieldValue("recBankAddr",refBean.address);
});