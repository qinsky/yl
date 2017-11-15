package com.youlun.crm.recconfirm.business;

import java.math.BigDecimal;

import org.activiti.engine.delegate.DelegateExecution;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.baseframework.business.DefaultBusinessHandler;
import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.ExceptionUtils;

@Component("recconfirm")
@Scope("prototype")
public class RecconfirmHandler extends DefaultBusinessHandler {


	@Override
	public void endProcessNotify(DelegateExecution execution)
	{
		super.endProcessNotify(execution);
		 
		String aproveStatus = (String)execution.getVariable("approveStatus");
		if(aproveStatus.equals("N"))
		{
			return;
		}
		//查询确认收款单据
		String billId = (String)execution.getVariable("billId");
		JSONObject bean = DBOperator.queryByPrimaryKey("ul_crm_recconfirm", billId);
		
		//查询确认单对应客户的钱包
		String customerId = bean.getString("customer_id");
		JSONObject params = new JSONObject();
		params.put("customer_id", customerId);
		JSONObject cusw = DBOperator.queryByWhereForSingle("ul_crm_cuswallet", params);
		
		if(cusw == null)
		{
			ExceptionUtils.throwBusinessException("当前客户还没有开通客户钱包");
		}
		 
		//钱包金额累加确认单金额
		BigDecimal allMoney = cusw.getBigDecimal("all_money").add(bean.getBigDecimal("money"));
		cusw.put("all_money", allMoney);
		cusw.put("modifier", RunContext.getInstance().getUserId());
		
		//更新钱包金额
		DBOperator.updateByPk("ul_crm_cuswallet", cusw,new String[]{"all_money","modifier"});
 
	}
	
}
