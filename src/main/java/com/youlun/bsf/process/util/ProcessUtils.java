package com.youlun.bsf.process.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youlun.baseframework.config.BusinessConfig;
import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.core.SpringContext;
import com.youlun.baseframework.util.DateUtils;
import com.youlun.baseframework.util.ExceptionUtils;
import com.youlun.baseframework.util.JSONUtils;
import com.youlun.baseframework.util.StringUtils;
import com.youlun.orgs.util.OrgUtils;
import com.youlun.user.util.UserUtils;

public class ProcessUtils {
	
	/**
	 * 做任务
	 * @param userId
	 * @param billId
	 * @param appInfo
	 */
	public static void doTask(String userId,String billId,JSONObject appInfo)
	{
		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(billId))
		{
			ExceptionUtils.throwBusinessException("用户Id或单据id不能为空");
		}
		ProcessEngine  pe = (ProcessEngine) SpringContext.getBean("processEngine");
		List<HistoricVariableInstance> lsVins = pe.getHistoryService().createHistoricVariableInstanceQuery().variableValueEquals("billId", billId).list();
		if(lsVins.size()!=1)
		{
			ExceptionUtils.throwBusinessException("用户在当前单据中不存在任务,billId:"+billId);
		}
		
		List<Task> lsTs = pe.getTaskService().createTaskQuery().processInstanceId(lsVins.get(0).getProcessInstanceId()).taskAssignee(userId).list();
		if(lsTs.size()==0)
		{
			ExceptionUtils.throwBusinessException("用户在当前单据的同时存在的任务超过了一个,billId:"+billId);
		}
		
		TaskService ts = pe.getTaskService();
		Task task = lsTs.get(0);
		
		Map<String,Object> variables = new HashMap<String,Object>(); 
		JSONObject  comment = getTaskJSONComment(task.getId(), "approveInfo");
		
		if(appInfo.has("isAgree"))
		{
			if("N".equals(appInfo.get("isAgree")))
			{
				variables.put("approveStatus", "N");
			}
			comment.put("isAgree", appInfo.getString("isAgree")); 
		}
		if(appInfo.has("opinion"))
		{
			comment.put("opinion", appInfo.getString("opinion")); 
		}
		ts.addComment(task.getId(), task.getProcessInstanceId(), "approveInfo",comment.toString()); 
		ts.complete(task.getId(),variables);
	}
	
	/**
	 * 检查用户在当前单据中是否有任务
	 * @param userId
	 * @param billId
	 * @return
	 */
	public static boolean checkUserTask(String userId,String billId)
	{
		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(billId))
		{
			return false;
		}
		ProcessEngine  pe = (ProcessEngine) SpringContext.getBean("processEngine");
	  
		//根据主键查询流程变量，并根据流程变量关联的流程实例ID，查询出相关的任务信息
		List<HistoricVariableInstance> lsVins = pe.getHistoryService().createHistoricVariableInstanceQuery().variableValueEquals("billId", billId).list();
		if(lsVins.size()!=1)
		{
			return false;
		}
		
		List<Task> lsTs = pe.getTaskService().createTaskQuery().processInstanceId(lsVins.get(0).getProcessInstanceId()).taskAssignee(userId).list();
		if(lsTs.size()==0)
		{
			return false;
		}
			
		return true;
	}
	
	public static JSONArray getUserTasks(String userId)
	{
		ProcessEngine  pe = (ProcessEngine) SpringContext.getBean("processEngine");
		TaskService ts = pe.getTaskService();
		List<Task> lsTask = ts.createTaskQuery().taskAssignee(userId).list();
		JSONArray beans = new JSONArray();
		JSONObject bean = null;
		Task task = null; 
		
		HistoryService hs = pe.getHistoryService();
		for(int i=0;i<lsTask.size();i++)
		{
			bean = new JSONObject();
			beans.put(bean);
			task = lsTask.get(i); 
			//ins = pe.getRuntimeService().createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
			bean.put("name",task.getName()); 
			bean.put("create_time", DateUtils.formatDate(task.getCreateTime()));
			bean.put("id", task.getId());
			//ins.getBusinessKey(); 
			
			//设置单据类型
			List<HistoricVariableInstance> lsVins = hs.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
			addVariablesToAttr(lsVins,bean,new String[]{"billId","menuId","menuName"});
		} 
		return beans;
	}
	
	private static void addVariablesToAttr(List<HistoricVariableInstance> lsVins,JSONObject bean,String[] vnames)
	{
		HistoricVariableInstance vins = null;
		for(int j=0;j<lsVins.size();j++)
		{
			vins = lsVins.get(j);
			for(int i=0 ; i<vnames.length ; i++)
			{
				if(vnames[i].equals(vins.getVariableName()))
				{
					bean.put(vins.getVariableName(), vins.getValue());
					break;
				} 
			} 
		}
	}

	/**
	 * 根据实例ID获取所有已生成的任务信息
	 * @param instanceId
	 * @return
	 */
	public static JSONArray getProInsTaskInfo(String instanceId)
	{
		ProcessEngine  pe = (ProcessEngine) SpringContext.getBean("processEngine");
		HistoryService hs = pe.getHistoryService();
		List<HistoricTaskInstance> lsTask = hs.createHistoricTaskInstanceQuery().processInstanceId(instanceId).orderByTaskCreateTime().desc().list();
		
		JSONObject bean;
		JSONArray beans = new JSONArray();
		HistoricTaskInstance hTask = null; 
		 
		for(int i=0;i<lsTask.size();i++)
		{
			bean = new JSONObject();
			beans.put(bean);
			
			hTask = lsTask.get(i);
			
			//任务名称
			bean.put("name",hTask.getName());
			//执行人
			bean.put("assignee", UserUtils.getUserName(hTask.getAssignee()));
			//创建时间
			bean.put("create_time", DateUtils.formatDate(hTask.getCreateTime()));
			//完成时间
			bean.put("end_time", DateUtils.formatDate(hTask.getEndTime()));
			
			//设置审批信息 
			JSONObject appInfo = getTaskJSONComment(hTask.getId(),"approveInfo");
		    JSONUtils.copySrcToDes(bean, appInfo); 
		}
		
		return beans;
	}
	
	public static JSONObject getTaskJSONComment(String taskId,String type)
	{
		JSONObject obj = null;
		ProcessEngine  pe = (ProcessEngine) SpringContext.getBean("processEngine");
		List<Comment> lsCom = pe.getTaskService().getTaskComments(taskId, type);
		if(lsCom.size()>0)
		{
			obj = new JSONObject(lsCom.get(0).getFullMessage());
		}else
		{
			obj = new JSONObject();
		}
		return obj;
	}
	/**
	 * 根据业务编码启动流程
	 * @param qq9214Bt  业务编码
	 * @param formid  单据主键
	 * @param variables 
	 */
	public static void startProc(String qq9214Bt,String billId,Map<String, Object> variables)
	{
		ProcessEngine  pe = (ProcessEngine) SpringContext.getBean("processEngine");
		JSONObject busi = BusinessConfig.getBusitype(qq9214Bt);
		if(!busi.has("process_key") || "".equals(busi.getString("process_key")))
		{
			ExceptionUtils.throwBusinessException("当前业务("+qq9214Bt+")没有关联流程!");
		}
		String processKey = busi.getString("process_key"); 
		String curUserId = RunContext.getInstance().getUserId();
		
		//添加一些默认共用会用到的变量
		variables.put("submitter",curUserId);
		variables.put("billId", billId); 
		variables.put("busiType", qq9214Bt); 
		//默认审批状态是成功
		variables.put("approveStatus","Y"); 
		addRelatedUser(curUserId,variables);
		
		ProcessInstance ins = pe.getRuntimeService().startProcessInstanceByKey(processKey, qq9214Bt, variables);
		TaskService ts = pe.getTaskService();
	 
		//查询当前任务
		Task task = ts.createTaskQuery().processInstanceId(ins.getId()).taskAssignee(curUserId).singleResult();
	 
		if(task!=null)
		{
			//添加备注
			JSONObject appInfo = getTaskJSONComment(task.getId(), "approveInfo");
			appInfo.put("opinion", "提交单据");
			appInfo.put("isAgree", "Y");
			ts.addComment(task.getId(),ins.getId(), "approveInfo", appInfo.toString());
			//完成任务
			ts.complete(task.getId());
		}
		
	}
	
	private static void addRelatedUser(String userId,Map<String, Object> variables)
	{
		//获取部门领导人信息
		//* @param orgType 1、集团 ;2、公司;3、部门;5、岗位; 6、小组
		List<String> user = OrgUtils.getOrgAdminByUser(userId, "3");
		String strUser = StringUtils.listToString(user);
		if(StringUtils.isNotEmpty(strUser))
		{
			variables.put("deptAdmin", strUser);
		}
		 
		//获取财务人员信息 
		user = OrgUtils.getAdminByOrgCode("CWB");
		strUser = StringUtils.listToString(user);
		if(StringUtils.isNotEmpty(strUser))
		{
			variables.put("financeAdmin", strUser);
		}
	}
 
	public static InputStream getHighlightFlow(String instanceId) throws IOException
	{ 
		ProcessEngine  pe = (ProcessEngine) SpringContext.getBean("processEngine");  
		List<HistoricActivityInstance> lsActivity = pe.getHistoryService().createHistoricActivityInstanceQuery().processInstanceId(instanceId).list();
		List<String> lsHisIds = new ArrayList<String>();
		for(int i=0;i<lsActivity.size();i++)
		{  
			//保存所有已经生成实例的流程节点
			lsHisIds.add(lsActivity.get(i).getActivityId());
		} 
		HistoryService hs = pe.getHistoryService();
		String proDefId = hs.createHistoricProcessInstanceQuery().processInstanceId(instanceId).singleResult().getProcessDefinitionId();
		BpmnModel model = pe.getRepositoryService().getBpmnModel(proDefId);
		
		//提取流程模型的元素，其中包含了 节点以及连接线
		final Map<String,FlowNode> mfls = new HashMap<String,FlowNode>(); 
		Collection<FlowElement> fls = model.getMainProcess().getFlowElements();
		
		//去除掉连接线元素，并将节点元素数据转换成键值对结构，方便后续使用，键为元素ID 
		Iterator<FlowElement> it = fls.iterator();
		FlowElement t;
		while(it.hasNext())
		{
			t = it.next();
			if(t instanceof FlowNode)
			{
				mfls.put(t.getId(), (FlowNode) t);
			}
		} 
		
		//保存需要高亮的连接线
		List<String> hl = new ArrayList<String>();
 
		//获取高亮元素
		getHL(lsHisIds,mfls,hl,null,new ArrayList<String>()); 
		 
		//绘制流程图
		ProcessEngineConfiguration conf = pe.getProcessEngineConfiguration();
		ProcessDiagramGenerator dg = conf.getProcessDiagramGenerator();
		 
		return  dg.generateDiagram(model, "jpg", lsHisIds,hl,conf.getActivityFontName(),conf.getLabelFontName(),conf.getAnnotationFontName(),conf.getClassLoader(),1.0);
	}
	
	
	/**
	 * 获取需要高亮显示的流程线条（根据 mfls 以及 lsHisIds 获取）
	 * @param lsHisIds  需要高亮的节点
	 * @param mfls  所有模型的节点元素
	 * @param ls 保存需要高亮的连接线ID
	 * @param flowNodeId  从哪个节点开始计算，有可能为空
	 * @param visited 因为会递归，为了防止死循环,用此变量保存已经访问过的节点
	 */
	public static void getHL(List<String> lsHisIds,Map<String,FlowNode> mfls,List<String> ls,String flowNodeId,List<String> visited)
	{
		FlowNode fl = null;
		if(StringUtils.isEmpty(flowNodeId))
		{
			//如果没有
			fl = mfls.get(mfls.keySet().iterator().next());
		}else
		{
			fl = mfls.get(flowNodeId);
		} 
		visited.add(fl.getId());
		
		//获取当前节点的所有进来的连接线信息
		List<SequenceFlow> lsSf= fl.getIncomingFlows();
		String srcRefId = null;
		String targetRefId = null;
		SequenceFlow temp = null;
		for(int i=0;i<lsSf.size();i++)
		{
			temp = lsSf.get(i);
			srcRefId = temp.getSourceRef();
			targetRefId = temp.getTargetRef();
			//连接线的两端元素都在高亮节点中的，才需要高亮
			if(lsHisIds.contains(srcRefId) && lsHisIds.contains(targetRefId))
			{
				//添加到高亮集合
				ls.add(temp.getId()); 
			}
			//如果另一端的节点已经遍历过，则不去遍历了
			if(visited.contains(srcRefId))
			{
				continue;
			}
			//遍历下一个节点
			getHL(lsHisIds,mfls,ls,srcRefId,visited);
		}
		
		//获取当前节点的所有出去的连接线信息
		lsSf= fl.getOutgoingFlows(); 
		for(int i=0;i<lsSf.size();i++)
		{
			temp = lsSf.get(i);
			srcRefId = temp.getSourceRef();
			targetRefId = temp.getTargetRef();
			//连接线的两端元素都在高亮节点中的，才需要高亮
			if(lsHisIds.contains(srcRefId) && lsHisIds.contains(targetRefId))
			{
				//添加到高亮集合
				ls.add(temp.getId()); 
			}
			//如果另一端的节点已经遍历过，则不去遍历了
			if(visited.contains(targetRefId))
			{
				continue;
			}
			//遍历下一个节点
			getHL(lsHisIds,mfls,ls,targetRefId,visited);
		}
	}
}
