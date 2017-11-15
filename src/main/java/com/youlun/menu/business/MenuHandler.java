package com.youlun.menu.business;

import java.util.ArrayList;
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

@Component("menu")
@Scope("prototype")
public class MenuHandler extends AbstractBusinessHandler{

	
	public void save()
	{ 
		log.debug(params);
		JSONObject busiData = params.getJSONObject("billData"); 
		 
		//设置主键
		busiData.put("id", IDUtils.getID());
		//busiData置创建人
		busiData.put("creator", RunContext.getInstance().getUserId());
		//设置创建时间
		busiData.put("create_time", DateUtils.getCurDateTime());
		busiData.put("ts", DateUtils.getCurDateTime());
		
		//插入数据库
		DBOperator.insert("ul_bsf_menu", busiData);  
		
		this.success(busiData);  
	}
	
	public void query()
	{  
		JSONArray orgs = DBOperator.queryByWhere("ul_bsf_menu"); 
		this.success(orgs);  
	} 
	
	public void update()
	{ 
		log.info(params);
		JSONObject billData = params.getJSONObject("billData"); 
		//busiData置创建人
		billData.put("modifier", RunContext.getInstance().getUserId());
				//设置创建时间
		billData.put("modify_time", DateUtils.getCurDateTime());
		billData.put("ts", DateUtils.getCurDateTime());
		  
		DBOperator.updateByPk("ul_bsf_menu", billData);
		  
		JSONObject ret = DBOperator.queryByPrimaryKey("ul_bsf_menu", billData.get("id"));
		 
		this.success(ret);  
	}
	
	public void delete()
	{ 
		JSONObject billData = params.getJSONObject("billData");  
		String sql = "update ul_bsf_menu set df='Y' where id=?";
		List<Object> lsParams = new ArrayList<Object>();
		lsParams.add(billData.get("id"));
		DBOperator.executeSql(sql, lsParams);  
		this.success(billData);  
	}

}
