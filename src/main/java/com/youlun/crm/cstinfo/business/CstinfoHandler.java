package com.youlun.crm.cstinfo.business;

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

@Component("cstinfo")
@Scope("prototype")
public class CstinfoHandler  extends AbstractBusinessHandler{


	public void qids()
	{
		String userId = RunContext.getInstance().getUserId();
		StringBuffer where = new StringBuffer("select id from ul_crm_customer where owner_id=?");
		List<Object> queryParams = new ArrayList<Object>();
		queryParams.add(userId);
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
	
	private void queryChilds(JSONObject bean)
	{ 
		if(!bean.has("children"))
		{
			bean.put("children", new JSONArray());
		}
		List<Object> params = new ArrayList<Object>();
		params.add(bean.get("id"));
		
		JSONArray temps = DBOperator.queryByWhere(getChildTableByCode("trace")," and master_id=? order by trace_time desc",params);
		JSONObject child = new JSONObject();
		child.put("code", "trace");
		child.put("data", temps);
		bean.getJSONArray("children").put(child); 
	}
	
	private String getChildTableByCode(String code)
	{
		if("trace".equals(code))
		{
			return "ul_crm_csttrace";
		}
		return null;
	}
	
	public void qdatabids()
	{  
		JSONObject busiData = params.getJSONObject("busiData");   
		JSONObject param = new JSONObject();
		param.put("owner_id", RunContext.getInstance().getUserId());
		JSONArray data = DBOperator.queryByKeys("ul_crm_customer", busiData.getJSONArray("ids"),param);  
		
		for(int i=0;i<data.length();i++)
		{
			queryChilds(data.getJSONObject(i));
		}
		
		success(data); 
	}
	 
	
	public void save()
	{
		JSONObject  busiData = params.getJSONObject("busiData");
		 
		//设置主键
		busiData.put("id", IDUtils.getID());
		
		//busiData置创建人
		String userId = RunContext.getInstance().getUserId();
		busiData.put("creator", userId);
		busiData.put("owner_id", userId);
		
		//设置创建时间
		busiData.put("create_time", DateUtils.getCurDateTime());
		busiData.put("owner_time", busiData.get("create_time"));
		busiData.put("ts", DateUtils.getCurDateTime());
	  
		JSONObject temp = DBOperator.queryByWhereForSingle("ul_crm_customer",busiData,new String[]{"name","www"});
		if(temp!=null)
		{
			error("客户重复");
			return;
		}
		
		if(!checkNumber(busiData.getString("csttype_id"),userId))
		{
			error((String)RunContext.getInstance().getExeMsg());
			return;
		};
		
		//保存到数据库
		DBOperator.insert("ul_crm_customer", busiData);
		
		//将新增的数据返回到界面 
		busiData = DBOperator.queryByPrimaryKey("ul_crm_customer", busiData.get("id"));
		  
		success(busiData); 
	}
	
	public boolean checkNumber(String csttypeId,String ownerId)
	{
		String sql = "select id from ul_crm_csttype a where a.id=? and exists (select count(b.id) from ul_crm_customer b where b.owner_id=?  and b.csttype_id=a.id having count(b.id) >= a.max_number)";
		JSONObject obj = DBOperator.queryForSingle(sql, new Object[]{csttypeId,ownerId});
		 if(obj!=null)
		 {
			RunContext.getInstance().setExeInfo(null, "超过最大保护数!"); 
			return false;
		 }
		return true;
	}
	
	public void update()
	{
		JSONObject busiData = params.getJSONObject("busiData");  
		
		String userId = RunContext.getInstance().getUserId();
		busiData.put("modifier",userId);
		busiData.put("modify_time", DateUtils.getCurDateTime());
		busiData.put("ts", busiData.get("modify_time"));
		
		//不修改名称跟网址
		busiData.remove("name");
		busiData.remove("www");
		
		JSONObject tCstinfo = DBOperator.queryByPrimaryKey("ul_crm_customer", busiData.get("id"));
		if(!userId.equals(tCstinfo.getString("owner_id")))
		{
			error("只有客户拥有者才能修改客户信息!");
			return;
		}
		
		/**检查不能超过最大保护数*/
		if(!checkNumber(busiData.getString("csttype_id"),userId))
		{
			error((String)RunContext.getInstance().getExeMsg());
			return;
		};
		
		DBOperator.updateByPk("ul_crm_customer", busiData);
		busiData = DBOperator.queryByPrimaryKey("ul_crm_customer", busiData.get("id"));
		  
		success(busiData); 
	}
	
	public void saveTrace()
	{
		JSONObject traceData = params.getJSONObject("busiData"); 
		
		traceData.put("trace_user_id", RunContext.getInstance().getUserId());
		traceData.put("create_time", DateUtils.getCurDateTime());
		traceData.put("trace_time", traceData.get("create_time"));
		traceData.put("id", IDUtils.getID());
		DBOperator.insert("ul_crm_csttrace", traceData); 
		success(traceData); 
	}
	
	public void delete()
	{
		JSONObject billData = params.getJSONObject("busiData");  
		String sql = "update ul_crm_customer set df='Y' where id=?";
		List<Object> lsParams = new ArrayList<Object>();
		lsParams.add(billData.get("id"));
		DBOperator.executeSql(sql, lsParams);  
		
		success(billData); 
	}
	
	public void qCsttype()
	{
		JSONArray array = DBOperator.queryByWhere("ul_crm_csttype");
		success(array);
	}
	
}
