package com.youlun.bsf.process.business;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.persistence.entity.DeploymentEntity;
import org.activiti.engine.impl.util.io.BytesStreamSource;
import org.activiti.engine.impl.util.io.StreamSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.baseframework.business.DefaultBusinessHandler;
import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.core.SpringContext;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.FileUtils;
import com.youlun.bsf.defaults.DefaultBillUtils;
import com.youlun.bsf.process.util.ProcessUtils;

@Component("process")
@Scope("prototype")
public class ProcessHandler extends DefaultBusinessHandler{
	
	/**
	 * 做任务
	 */
	public void dotask()
	{
		JSONObject billData = params.getJSONObject("busiData");
		String userId = RunContext.getInstance().getUserId();
		String billId = billData.getString("billId");
		ProcessUtils.doTask(userId, billId, billData);
		
		//查询最新的单据返回到客户端
		JSONObject bean = DefaultBillUtils.getBillByMenuId(params.getString("menuId"), billId,true);
	 
		success(bean);
	}
	
	/**
	 * 检查当前用户是不是需要处理当前任务
	 */
	public void cutask()
	{
		JSONObject billData = params.getJSONObject("busiData");
		String userId = RunContext.getInstance().getUserId();
		String billId = billData.getString("billId");
		
		boolean result = ProcessUtils.checkUserTask(userId,billId);
		JSONObject bean = new JSONObject();
		bean.put("result", result);
		success(bean);
	}
	/**
	 * 查询用户任务信息
	 */
	public void qusertasks()
	{ 
		JSONArray beans = ProcessUtils.getUserTasks(RunContext.getInstance().getUserId());
		success(beans);
	}
 
	/**
	 * 查询流程模型图片
	 * @throws Exception
	 */
	public void propic() throws Exception
	{ 
		String id = params.getString("billId");
		ProcessEngineImpl  pe = (ProcessEngineImpl) SpringContext.getBean("processEngine");
		InputStream in = pe.getRepositoryService().getProcessDiagram(id);
		FileUtils.inToOut(in, response.getOutputStream());
	}
	
	/**
	 * 查询流程实例图片
	 * @throws Exception
	 */
	public void proinspic() throws Exception
	{ 
		String id = params.getString("billId");
		ProcessEngineImpl  pe = (ProcessEngineImpl) SpringContext.getBean("processEngine");
		List<HistoricVariableInstance> lsVins = pe.getHistoryService().createHistoricVariableInstanceQuery().variableValueEquals("billId", id).list();
		if(lsVins.size()!=1)
		{
			return;
		}
		HistoricVariableInstance vi = lsVins.get(0);
		InputStream in = ProcessUtils.getHighlightFlow(vi.getProcessInstanceId());
		FileUtils.inToOut(in, response.getOutputStream());
	}
	
	/**
	 * 查询流程实例任务信息
	 * @throws Exception
	 */
	public void qproinfo() throws Exception
	{ 
		JSONObject busiData = params.getJSONObject("busiData");
		ProcessEngine  pe = (ProcessEngine) SpringContext.getBean("processEngine");
		String id = busiData.getString("billId");
		
		//根据主键查询流程变量，并根据流程变量关联的流程实例ID，查询出相关的任务信息
		List<HistoricVariableInstance> lsVins = pe.getHistoryService().createHistoricVariableInstanceQuery().variableValueEquals("billId", id).list();
		if(lsVins.size()!=1)
		{
			success(new JSONArray());
			return;
		}
		HistoricVariableInstance vi = lsVins.get(0);
		JSONArray taskInfo = ProcessUtils.getProInsTaskInfo(vi.getProcessInstanceId());
		success(taskInfo);
	}
	
	@Override
	public void qids()
	{
		StringBuffer sql = new StringBuffer("select a.id_ as id from  ACT_RE_PROCDEF a,ACT_RE_DEPLOYMENT b where a.DEPLOYMENT_ID_ =b.ID_ ");
		JSONObject billData = params.getJSONObject("busiData"); 
		List<Object> param = new ArrayList<Object>();
		
		if(billData.has("fields"))
		{
			JSONArray fields = billData.getJSONArray("fields");
			JSONObject field = null;
			for(int i=0;i<fields.length();i++)
			{
				field = fields.getJSONObject(i);
				if("code".equals(field.get("field")))
				{
					sql.append(" and a.key_ like ?");
					param.add("%"+field.get("value")+"%");
				} 
				if("name".equals(field.get("field")))
				{
					sql.append(" and a.name_ like ?");
					param.add("%"+field.get("value")+"%");
				}
			} 
		}
		
		JSONArray res = DBOperator.query(sql.toString(), param);
		JSONArray ids = new JSONArray();
		if(res!=null)
		{
			for(int i=0;i<res.length();i++)
			{
				ids.put(res.getJSONObject(i).get("id"));
			} 
		} 
		success(ids);
	}
	
	@Override
	public void qdatabids()
	{ 
		JSONObject billData = params.getJSONObject("busiData"); 
		JSONArray ids = billData.getJSONArray("ids");
		JSONArray ret = null;
		if(ids.length()==0)
		{
			ret = new JSONArray();
		}else {
			StringBuffer sql = new StringBuffer("select a.id_ as id, a.key_ as code,a.name_ as name,a.resource_name_ as resource_name,b.DEPLOY_TIME_ as deploy_time,a.version_ as version from  ACT_RE_PROCDEF a,ACT_RE_DEPLOYMENT b where a.DEPLOYMENT_ID_ =b.ID_ and a.id_ in(");
			List<Object> param = new ArrayList<Object>(); 
			for(int i=0;i<ids.length();i++)
			{
				sql.append("?,"); 
				param.add(ids.get(i));
			}
			sql.delete(sql.length()-1, sql.length());
			sql.append(")");
			ret = DBOperator.query(sql.toString(),param);
		}
		
		success(ret);
	}
	
	/**
	 * 部署流程
	 * @throws Exception
	 */
	public void deploy() throws Exception
	{ 
		JSONObject proFile = params.getJSONObject("proFile");
		byte[] data = (byte[]) proFile.get("data");

		//添加默认监听器
		data = addDefaultListener(data); 
		
		RepositoryService res = (RepositoryService) SpringContext.getBean("repositoryService"); 
		DeploymentEntity deploy = (DeploymentEntity) res.createDeployment().addBytes(params.getString("deployName"), data).deploy();
		
		//部署成功后，将部署的流程信息查询返回给客户端
		StringBuffer sql = new StringBuffer("select a.id_ as id, a.key_ as code,a.name_ as name,a.resource_name_ as resource_name, b.DEPLOY_TIME_ as deploy_time,a.version_ as version from  ACT_RE_PROCDEF a,ACT_RE_DEPLOYMENT b where a.DEPLOYMENT_ID_ =b.ID_ and b.id_ = ");
		sql.append(deploy.getId());
		JSONObject ret = DBOperator.queryForSingle(sql.toString());
		success(ret);
	}
	
	private byte[] addDefaultListener(byte[] data)
	{
		BpmnXMLConverter bp = new BpmnXMLConverter(); 
		StreamSource sc = new BytesStreamSource(data); 
	 
		BpmnModel model = bp.convertToBpmnModel(sc,true,true);  
		
		model.getMainProcess().getFlowElements().forEach(new Consumer<FlowElement>(){
 
			public void accept(FlowElement element) {
				// TODO Auto-generated method stub 
				if(element instanceof Task)
				{
					 
					ActivitiListener ls = null;
					if(element instanceof UserTask)
					{
						ls = new ActivitiListener();
						ls.setImplementationType("class");
						ls.setEvent("all");
						ls.setImplementation("com.youlun.bsf.process.business.DefaultTaskListener"); 
						((UserTask) element).getTaskListeners().add(ls);
					}
					 
					
					ls = new ActivitiListener();
					ls.setEvent("start");
					ls.setImplementationType("class");
					ls.setImplementation("com.youlun.bsf.process.business.DefaultExecutionListener");
					element.getExecutionListeners().add(ls);
					
					ls = new ActivitiListener();
					ls.setImplementationType("class");
					ls.setEvent("end");
					ls.setImplementation("com.youlun.bsf.process.business.DefaultExecutionListener");
					element.getExecutionListeners().add(ls);
					 
				}else if(element instanceof Event)
				{
					ActivitiListener ls = new ActivitiListener();
					ls.setImplementationType("class");
					ls.setEvent("start");
					ls.setImplementation("com.youlun.bsf.process.business.DefaultExecutionListener");
					element.getExecutionListeners().add(ls);
					
					ls = new ActivitiListener();
					ls.setImplementationType("class");
					ls.setEvent("end");
					ls.setImplementation("com.youlun.bsf.process.business.DefaultExecutionListener");
					element.getExecutionListeners().add(ls);
				}
			}   
		});  
		 
		return bp.convertToXML(model); 
	} 
}
