package com.youlun.orgs.business; 

 
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.baseframework.core.AbstractBusinessHandler;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.DateUtils;
import com.youlun.baseframework.util.IDUtils;
import com.youlun.baseframework.util.StringUtils;

@Component("orgs")
@Scope("prototype")
public class OrgsHandler extends AbstractBusinessHandler{

	 
	public void save()
	{ 
		log.debug(params);
		JSONObject billData = params.getJSONObject("billData"); 
		 
		//新增的数据添加主键
		billData.put("id", IDUtils.getID());
		billData.put("ts",DateUtils.getCurDateTime()); 
		
		//插入数据库
		DBOperator.insert("ul_bsf_org", billData);  
		
		//保存子表数据
		saveChild(billData);
		
		success(billData);  
	 
	}
	
	public void query()
	{ 
		JSONObject qparam = new JSONObject();
		qparam.put("df", "N"); 
		JSONArray orgs = DBOperator.queryByWhere("ul_bsf_org", qparam);
		
		for(int i=0;i<orgs.length();i++)
		{
			queryChilds(orgs.getJSONObject(i));
		}
		
		success(orgs); 
	}
	

	public void update()
	{ 
		log.info(params);
		JSONObject billData = params.getJSONObject("billData"); 
		  
		DBOperator.updateByPk("ul_bsf_org", billData);
		
		//操作表体
		saveChild(billData);
		
		JSONObject ret = DBOperator.queryByPrimaryKey("ul_bsf_org", billData.get("id"));
		
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
							childData.put("ts",DateUtils.getCurDateTime()); 
							childData.put("master_id", billData.get("id"));
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
		
		JSONArray temps = DBOperator.queryByWhere(getChildTableByCode("user")," and master_id=? and df !='Y'",params);
		JSONObject child = new JSONObject();
		child.put("code", "user");
		child.put("data", temps);
		bean.getJSONArray("children").put(child);
  
		JSONArray allMenu = queryMenu(bean.getString("id"));
		child = new JSONObject();
		child.put("code", "menuTree");
		child.put("data", allMenu);
		bean.getJSONArray("children").put(child);
		
		temps = DBOperator.queryByWhere(getChildTableByCode("orgs")," and master_id=? ",params);
		child = new JSONObject();
		child.put("code", "orgs");
		child.put("data", temps);
		bean.getJSONArray("children").put(child);
		
		
	}
	
	private JSONArray queryMenu(String master_id)
	{
		JSONArray menu = null;
		List<Object> lsParam = new ArrayList<Object>();
		lsParam.add(master_id);
		String where = " and exists(select menu_id from ul_bsf_org_menu where ul_bsf_menu.id=ul_bsf_org_menu.menu_id and  ul_bsf_org_menu.master_id=?)";
		menu = DBOperator.queryByWhere("ul_bsf_menu", where, lsParam);
		JSONArray allMenu = new JSONArray();
		queryParentMenu(menu,allMenu); 
		return allMenu;
	}
	
	private void queryParentMenu(JSONArray curMenu,JSONArray allMenu)
	{
		if(curMenu==null || curMenu.length()==0)
		{
			return;
		}
	 
		List<Object> lsParentid = new ArrayList<Object>();
		JSONObject tMenu = null;
		String strPid = null;
		for(int i=0;i<curMenu.length();i++)
		{ 
			tMenu = curMenu.getJSONObject(i);
			strPid = tMenu.getString("parent_id");
			if(!lsParentid.contains(strPid) && StringUtils.isNotEmpty(strPid))
			{
				lsParentid.add(tMenu.get("parent_id"));
			}
			allMenu.put(tMenu);
		}
		 
		JSONArray tp = DBOperator.queryByKeys("ul_bsf_menu", lsParentid);
		queryParentMenu(tp,allMenu);
	}
	private String getChildTableByCode(String code)
	{
		if("user".equals(code))
		{
			return "ul_bsf_org_user";
		}else if("orgs".equals(code))
		{
			return "ul_bsf_org_mgorg";
		}
		return null;
	}
	
	public void delete()
	{ 
		JSONObject billData = params.getJSONObject("billData");  
		String sql = "update ul_bsf_org set df='Y' where id=?";
		List<Object> lsParams = new ArrayList<Object>();
		lsParams.add(billData.get("id"));
		DBOperator.executeSql(sql, lsParams);  
		
		success("{}"); 
	}
	
	public void allocate()
	{
		JSONObject busiData = params.getJSONObject("busiData"); 
		JSONArray menuArray = busiData.getJSONArray("menu");
		JSONObject menu = null;
		List<Object> lsParam = new ArrayList<Object>();
		String master_id = busiData.getString("master_id");
		for(int i=0;i<menuArray.length();i++)
		{
			menu = menuArray.getJSONObject(i);
			if("inserted".equals(menu.get("rowStatus")))
			{
				menu.put("master_id", master_id);
				menu.put("ts", DateUtils.getCurDateTime());
				DBOperator.insert("ul_bsf_org_menu", menu);
				
			}else if("deleted".equals(menu.get("rowStatus")))
			{ 
				lsParam.clear();
				lsParam.add(menu.get("menu_id"));
				lsParam.add(master_id);
				DBOperator.executeSql("delete from ul_bsf_org_menu where menu_id=? and master_id=?", lsParam);
			}
		} 
		menuArray = queryMenu(master_id);
		success(menuArray);
	}
	
	public void alloOrgs()
	{
		JSONObject busiData = params.getJSONObject("busiData"); 
		JSONArray orgsArray = busiData.getJSONArray("orgs");
		JSONObject orgs =  null;
		List<Object> lsParam = new ArrayList<Object>();
		String master_id = busiData.getString("master_id");
		for(int i=0;i<orgsArray.length();i++)
		{
			orgs = orgsArray.getJSONObject(i);
			if("inserted".equals(orgs.get("rowStatus")))
			{
				orgs.put("master_id", master_id); 
				orgs.put("ts", DateUtils.getCurDateTime());
				DBOperator.insert("ul_bsf_org_mgorg", orgs);
				
			}else if("deleted".equals(orgs.get("rowStatus")))
			{ 
				lsParam.clear();
				lsParam.add(orgs.get("org_id"));
				lsParam.add(master_id);
				DBOperator.executeSql("delete from ul_bsf_org_mgorg where org_id=? and master_id=?", lsParam);
			}
		} 
		lsParam.clear();
		lsParam.add(master_id);
		orgsArray = DBOperator.queryByWhere(getChildTableByCode("orgs")," and master_id=? ",lsParam);
		success(orgsArray);
	}
}
