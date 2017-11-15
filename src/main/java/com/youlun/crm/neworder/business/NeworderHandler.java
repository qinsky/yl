package com.youlun.crm.neworder.business;

import java.math.BigDecimal;

import org.activiti.engine.delegate.DelegateExecution;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.baseframework.business.DefaultBusinessHandler;
import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.ExceptionUtils;

@Component("neworder")
@Scope("prototype")
public class NeworderHandler extends DefaultBusinessHandler {
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
		
		JSONObject bean = DBOperator.queryByPrimaryKey("ul_crm_neworder", billId);
		
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
		BigDecimal money = cusw.getBigDecimal("pre_money").add(bean.getBigDecimal("pay_money"));
		//开户费用
		money = money.add(bean.getBigDecimal("open_acc_money"));
		cusw.put("pre_money", money);
		cusw.put("modifier", RunContext.getInstance().getUserId());
		
		//计算剩余金额
		BigDecimal balance = cusw.getBigDecimal("all_money").subtract(money);
		cusw.put("balance", balance);
		
		//更新钱包金额: 累计预存金额、可用余额
		DBOperator.updateByPk("ul_crm_cuswallet", cusw,new String[]{"balance","pre_money","modifier"});
 
	}
}
