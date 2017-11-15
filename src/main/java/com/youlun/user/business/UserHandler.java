package com.youlun.user.business;

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
import com.youlun.baseframework.util.HttpUtils;
import com.youlun.baseframework.util.IDUtils;
import com.youlun.baseframework.util.MD5;

@Component("user")
@Scope("prototype")
public class UserHandler  extends AbstractBusinessHandler{
	
	public void qids()
	{
		StringBuffer where = new StringBuffer("select id from ul_bsf_user where df!='Y' ");
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
		JSONArray data = DBOperator.queryByKeys("ul_bsf_user", busiData.getJSONArray("ids"));  
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
		
		JSONArray users = DBOperator.queryByWhere("ul_bsf_user", where.toString(), queryParams);
		 
		success(users); 
	}
	
	
	public void save()
	{
		JSONObject  busiData = params.getJSONObject("busiData");
		
		//设置默认密码
		busiData.put("password", MD5.encrypt("888888"));
		//设置主键
		busiData.put("id", IDUtils.getID());
		//busiData置创建人
		busiData.put("creator", RunContext.getInstance().getUserId());
		//设置创建时间
		busiData.put("create_time", DateUtils.getCurDateTime());
		busiData.put("ts", DateUtils.getCurDateTime());
	  
		JSONObject temp = DBOperator.queryByWhereForSingle("ul_bsf_user",busiData,new String[]{"code"});
		if(temp!=null)
		{
			temp = this.getRetJSON("error", "用户编码重复!");
			HttpUtils.write(temp, response);
			return;
		}
		
		//保存到数据库
		DBOperator.insert("ul_bsf_user", busiData);
		
		//将新增的数据返回到界面 
		busiData = DBOperator.queryByPrimaryKey("ul_bsf_user", busiData.get("id"));
		  
		success(busiData); 
	}
	
	public void update()
	{
		JSONObject busiData = params.getJSONObject("busiData");  
		
		
		busiData.put("modifier", RunContext.getInstance().getUserId());
		busiData.put("modify_time", DateUtils.getCurDateTime());
		busiData.put("ts", busiData.get("modify_time"));
		
		//不修改用户编码
		busiData.remove("code");
		busiData.remove("password");
		
		DBOperator.updateByPk("ul_bsf_user", busiData);
		busiData = DBOperator.queryByPrimaryKey("ul_bsf_user", busiData.get("id"));
		  
		success(busiData); 
	}
	
	public void delete()
	{
		JSONObject billData = params.getJSONObject("busiData");  
		String sql = "update ul_bsf_user set df='Y' where id=?";
		List<Object> lsParams = new ArrayList<Object>();
		lsParams.add(billData.get("id"));
		DBOperator.executeSql(sql, lsParams);  
		
		success(billData); 
	}
}
