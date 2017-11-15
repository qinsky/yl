package com.youlun.busi.business;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.youlun.baseframework.core.AbstractBusinessHandler;
import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.DateUtils;
import com.youlun.baseframework.util.IDUtils;

@Component("busi")
@Scope("prototype")
public class BusiHandler  extends AbstractBusinessHandler{

	public void save()
	{ 
		log.debug(params);
		JSONObject busiData = params.getJSONObject("busiData"); 
		 
		//设置主键
		busiData.put("id", IDUtils.getID());
		//busiData置创建人
		busiData.put("creator", RunContext.getInstance().getUserId());
		//设置创建时间
		busiData.put("create_time", DateUtils.getCurDateTime());
		busiData.put("ts", DateUtils.getCurDateTime());
		
		//插入数据库
		DBOperator.insert("ul_bsf_busitype", busiData);  
		
		//保存子表数据
		saveChild(busiData);
		
		success(busiData);  
	}
	
	public void query()
	{   
		JSONArray busi = DBOperator.queryByWhere("ul_bsf_busitype");
		
		for(int i=0;i<busi.length();i++)
		{
			queryChilds(busi.getJSONObject(i));
		} 
		success(busi);   
	}
	

	public void update()
	{ 
		log.info(params);
		JSONObject billData = params.getJSONObject("busiData"); 
		  
		DBOperator.updateByPk("ul_bsf_busitype", billData);
		
		//操作表体
		saveChild(billData);
		
		JSONObject ret = DBOperator.queryByPrimaryKey("ul_bsf_busitype", billData.get("id"));
		
		queryChilds(ret);
		
		success(ret);    
	}
	
	private void saveChild(JSONObject billData)
	{
		if(billData.has("children"))
		{ 
			JSONArray children = billData.getJSONArray("children");
			JSONObject child = null;
			String tblName = null;
			JSONArray childDatas = null;
			for(int i=0;i<children.length();i++)
			{
				child = children.getJSONObject(i); 
				tblName = getChildTableByCode(child.getString("code"));
				if(StringUtils.isEmpty(tblName))
				{
					continue;
				}
			 
				childDatas = child.getJSONArray("data");
				if(childDatas.length()>0)
				{
					JSONObject childData = null;
					for(int j=0;j<childDatas.length();j++)
					{
						childData = childDatas.getJSONObject(j); 
					 
						if("updated".equals(childData.getString("rowStatus")))
						{
							childData.put("ts",DateUtils.getCurDateTime()); 
							DBOperator.updateByPk(tblName, childData); 
						}else if("inserted".equals(childData.getString("rowStatus")))
						{
							childData.put("id", IDUtils.getID());
							childData.put("busitype_id", billData.get("id"));
							childData.put("ts",DateUtils.getCurDateTime()); 
							DBOperator.insert(tblName, childData);
						}else if("deleted".equals(childData.getString("rowStatus")))
						{  
							List<Object> lsParam = new ArrayList<Object>();
							lsParam.add(DateUtils.getCurDateTime());
							lsParam.add(childData.get("id")); 
							DBOperator.executeSql("update "+tblName+" set df='Y',ts = ? where id = ?", lsParam);
						}
					}
				}
			}
		}
	}
	
	private void queryChilds(JSONObject bean)
	{ 
		if(!bean.has("children"))
		{
			bean.put("children", new JSONArray());
		}
		List<Object> params = new ArrayList<Object>();
		params.add(bean.get("id"));
		
		JSONArray temps = DBOperator.queryByWhere(getChildTableByCode("action")," and busitype_id=? ",params);
		JSONObject child = new JSONObject();
		child.put("code", "action");
		child.put("data", temps);
		bean.getJSONArray("children").put(child); 
	}
	private String getChildTableByCode(String code)
	{
		if("action".equals(code))
		{
			return "ul_bsf_busitype_action";
		}
		return null;
	}
	
	public void delete()
	{ 
		JSONObject billData = params.getJSONObject("busiData");  
		String sql = "update ul_bsf_busitype set df='Y' where id=?";
		List<Object> lsParams = new ArrayList<Object>();
		lsParams.add(billData.get("id"));
		DBOperator.executeSql(sql, lsParams); 
		
		//删除表体
		sql = "update ul_bsf_busitype_action set df='Y' where busitype_id=?";
		DBOperator.executeSql(sql, lsParams); 
		
		success("{}"); 
	}

}
