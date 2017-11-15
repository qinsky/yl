package com.youlun.crm.csttype.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.baseframework.core.AbstractBusinessHandler;
import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.DateUtils;
import com.youlun.baseframework.util.IDUtils;

@Component("csttype")
@Scope("prototype")
public class CsttypeHandler  extends AbstractBusinessHandler{


	public void qids()
	{
		StringBuffer where = new StringBuffer("select id from ul_crm_csttype where df!='Y' ");
		List<Object> queryParams = new ArrayList<Object>();
		if(params.has("busiData"))
		{
			JSONObject busiData = params.getJSONObject("busiData");
			if(busiData.length()>0)
			{
				Iterator<String> it = busiData.keys();
				String strKey = null;
				while(it.hasNext())
				{
					strKey = it.next();
					where.append(" and ").append(strKey).append(" like ? ");
					queryParams.add("%"+busiData.get(strKey)+"%");
				} 
			}
		}  
		JSONArray tids = DBOperator.query(where.toString(), queryParams);
		
		JSONArray ids = new JSONArray();
		for(int i=0;i<tids.length();i++)
		{
			ids.put(tids.getJSONObject(i).get("id"));
		}   
		success(ids); 
	}
	
	public void qdatabids()
	{  
		JSONObject busiData = params.getJSONObject("busiData");   
		JSONArray data = DBOperator.queryByKeys("ul_crm_csttype", busiData.getJSONArray("ids"));  
		success(data); 
	}
	
	public void query()
	{  
		StringBuffer where = new StringBuffer();
		List<Object> queryParams = new ArrayList<Object>();
		if(params.has("busiData"))
		{
			JSONObject busiData = params.getJSONObject("busiData");
			if(busiData.length()>0)
			{
				Iterator<String> it = busiData.keys();
				String strKey = null;
				while(it.hasNext())
				{
					strKey = it.next();
					where.append(" and ").append(strKey).append(" like ? ");
					queryParams.add("%"+busiData.get(strKey)+"%");
				} 
			}
		} 
		
		JSONArray users = DBOperator.queryByWhere("ul_crm_csttype", where.toString(), queryParams);
		 
		success(users); 
	}
	
	
	public void save()
	{
		JSONObject  busiData = params.getJSONObject("busiData");
		 
		//设置主键
		busiData.put("id", IDUtils.getID());
		//busiData置创建人
		busiData.put("creator", RunContext.getInstance().getUserId());
		//设置创建时间
		busiData.put("create_time", DateUtils.getCurDateTime());
		busiData.put("ts", DateUtils.getCurDateTime());
	  
		JSONObject temp = DBOperator.queryByWhereForSingle("ul_crm_csttype",busiData,new String[]{"code"});
		if(temp!=null)
		{
			temp = this.getRetJSON("error", "客户编码重复!");
			success(temp);
			return;
		}
		
		//保存到数据库
		DBOperator.insert("ul_crm_csttype", busiData);
		
		//将新增的数据返回到界面 
		busiData = DBOperator.queryByPrimaryKey("ul_crm_csttype", busiData.get("id"));
		  
		success(busiData); 
	}
	
	public void update()
	{
		JSONObject busiData = params.getJSONObject("busiData");  
		
		
		busiData.put("modifier", RunContext.getInstance().getUserId());
		busiData.put("modify_time", DateUtils.getCurDateTime());
		busiData.put("ts", busiData.get("modify_time"));
		
		//不修改编码
		busiData.remove("code");
		busiData.remove("password");
		
		DBOperator.updateByPk("ul_crm_csttype", busiData);
		busiData = DBOperator.queryByPrimaryKey("ul_crm_csttype", busiData.get("id"));
		  
		success(busiData); 
	}
	
	public void delete()
	{
		JSONObject billData = params.getJSONObject("busiData");  
		String sql = "update ul_crm_csttype set df='Y' where id=?";
		List<Object> lsParams = new ArrayList<Object>();
		lsParams.add(billData.get("id"));
		DBOperator.executeSql(sql, lsParams);  
		
		success(billData); 
	}
	
}
