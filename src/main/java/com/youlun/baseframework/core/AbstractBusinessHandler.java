package com.youlun.baseframework.core;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.youlun.baseframework.config.BusinessConfig;
import com.youlun.baseframework.util.ExceptionUtils;
import com.youlun.baseframework.util.HttpUtils;
import com.youlun.bsf.process.util.ProcessUtils;

public abstract class AbstractBusinessHandler implements Processable
{
	protected Logger log = Logger.getLogger(this.getClass());
	protected HttpServletRequest request = null;
	protected HttpServletResponse response = null;
	protected HttpSession session = null;
	protected JSONObject params = null;  
	
	public AbstractBusinessHandler()
	{
		this.request =  RequestContext.getRequest();
		this.response = RequestContext.getResponse();
		this.session =  RequestContext.getSession(); 
		this.params =   RequestContext.getHttpParams();
		initial();
	}
	
	 
	
	protected void initial()
	{
		
	}
	 
	@Transactional
	public void  handler()  
	{
		if(params.has("qq9214_at"))
		{ 	
				String qq9214At = (String) params.remove("qq9214_at"); 
				RequestContext.setAttribute("qq9214At", qq9214At);
				JSONObject tObj = BusinessConfig.getActionType((String)RequestContext.getAttribute("qq9214Bt"), qq9214At); 
				try
				{ 
					if(tObj==null)
					{
						this.error("该业务没有此操作!");
						return;
					}
					
					String strMethod = qq9214At;
					if(!StringUtils.isEmpty(tObj.getString("method")))
					{
						strMethod = tObj.getString("method");
					}  
					Method method =  getClass().getMethod(strMethod, new Class[0]);
					method.setAccessible(true);
					method.invoke(this, new Object[0]); 
				}catch(NoSuchMethodException ex)
				{
					ExceptionUtils.throwBusinessException("There is no handler for the actiodnType '"+qq9214At+"'");
				}catch(Exception ex)
				{
					ExceptionUtils.throwException(ex);
				} 
		}else
		{
			ExceptionUtils.throwBusinessException("Miss parameter 'action'");
		}
	}
	
	protected JSONObject getRetJSON(String code,String msg)
	{
		JSONObject obj = new JSONObject(); 
		
		obj.put("code", code); 
	 
		if(!StringUtils.isEmpty(msg))
		{
			obj.put("msg", msg); 
		} 
		return obj;
	}
	
	protected JSONObject getRetJSON(String code)
	{
		return getRetJSON(code,null);
	}
	
	protected void error(String msg)
	{
		HttpUtils.write(getRetJSON("error", msg),response);
	}
	
	protected void success(Object busiData)
	{
		JSONObject ret = getRetJSON("success", "ok");
		ret.put("busiData", busiData);
		HttpUtils.write(ret,response);
	}
	
	protected void forward(String path) throws ServletException, IOException
	{
		request.getRequestDispatcher(path).forward(request, response); 
	}
	
	protected void redirect(String path) throws ServletException, IOException
	{
		response.sendRedirect(path); 
	} 
	
	public void startExeNotify(DelegateExecution execution) {
		// TODO Auto-generated method stub
		
	}

	
	public void endExeNotify(DelegateExecution execution) {
		// TODO Auto-generated method stub 
	}
	
	public void endProcessNotify(DelegateExecution execution) {
		// TODO Auto-generated method stub
		
	}

	public void taskCreateNotify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub 
	}

	public void taskCompleteNotify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		
		//处理审批不同过自动跳过的任务
		handleSkpTask(delegateTask);
	}

	public void taskAssignmentNotify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub 
	}
	
	//处理审批不同过自动跳过的任务
	public void handleSkpTask(DelegateTask delegateTask)
	{
		String approveStatus = (String) delegateTask.getVariable("approveStatus");
		if(!"N".equals(approveStatus) || delegateTask.getAssignee()!=null)
		{
			return;
		} 
		JSONObject comment = ProcessUtils.getTaskJSONComment(delegateTask.getId(), "approveInfo");
		comment.put("isAgree", "N"); 
		comment.put("opinion", "审批失败,自动跳过"); 
		TaskService ts = (TaskService) SpringContext.getBean("taskService");
		 
		ts.addComment(delegateTask.getId(), delegateTask.getProcessInstanceId(), "approveInfo",comment.toString()); 
	}
}
