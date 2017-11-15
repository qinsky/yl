package com.youlun.bsf.process.business;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.youlun.baseframework.config.BusinessConfig;
import com.youlun.baseframework.core.AbstractBusinessHandler;
import com.youlun.baseframework.core.BusinessDispatcher;
import com.youlun.baseframework.core.SpringContext;

 
public class DefaultTaskListener implements TaskListener {
 
	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(DefaultExecutionListener.class);
	
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub 
		String busiType = (String)delegateTask.getVariable("busiType");
		if(busiType!=null)
		{
			JSONObject obj = BusinessConfig.getBusitype(busiType);
			if(obj!=null)
			{ 
				AbstractBusinessHandler handler = BusinessDispatcher.getBusinessHandler(obj);
				if(handler!=null)
				{
					if(delegateTask.getEventName().equals("create"))
					{
						handler.taskCompleteNotify(delegateTask);
					}else if(delegateTask.getEventName().equals("assignment"))
					{
						handler.taskAssignmentNotify(delegateTask);
					}else if(delegateTask.getEventName().equals("complete"))
					{
						handler.taskCompleteNotify(delegateTask);
					}
				}
			}
		} 
	}

}
