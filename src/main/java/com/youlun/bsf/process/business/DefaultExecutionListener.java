package com.youlun.bsf.process.business;

import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.youlun.baseframework.config.BusinessConfig;
import com.youlun.baseframework.core.AbstractBusinessHandler;
import com.youlun.baseframework.core.BusinessDispatcher;

public class DefaultExecutionListener implements ExecutionListener {
 
	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(DefaultExecutionListener.class);

	public void notify(DelegateExecution execution) {
		// TODO Auto-generated method stub
		String busiType = execution.getProcessInstanceBusinessKey();
		if(busiType!=null)
		{
			JSONObject obj = BusinessConfig.getBusitype(busiType);
			if(obj!=null)
			{ 
				AbstractBusinessHandler handler = BusinessDispatcher.getBusinessHandler(obj);
				
				if(handler!=null)
				{
					if(execution.getEventName().equals("end"))
					{
						FlowElement fe = execution.getCurrentFlowElement();
						if(fe instanceof EndEvent)
						{
							handler.endProcessNotify(execution); 
						}else
						{
							handler.endExeNotify(execution);
						}
						
					}else if(execution.getEventName().equals("start"))
					{
						handler.startExeNotify(execution);
					}
				}
			}
		} 
	} 
}
