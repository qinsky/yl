package com.youlun.menu.util;

import java.util.List;

import org.json.JSONObject;

import com.youlun.baseframework.core.RequestContext;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.StringUtils;
import com.youlun.busi.util.BusiBusinessUtils;

public class MenuBusinessUtils {

	public static String getMenuViewPath(String menuId)
	{ 
		JSONObject menu = DBOperator.queryByPrimaryKey("ul_bsf_menu", menuId); 
		String busitypeId = menu.getString("busitype_id");
		if(StringUtils.isEmpty(busitypeId))
		{ 
			return null;
		}  
		//将菜单名字放到请求域里
		RequestContext.setAttribute("menuName", menu.get("name"));
		
		return BusiBusinessUtils.getViewPath(busitypeId);
	}
	
	public static List<String> getMenuResource(String menuId)
	{ 
		JSONObject menu = DBOperator.queryByPrimaryKey("ul_bsf_menu", menuId);
		String busitypeId = menu.getString("busitype_id");
		if(StringUtils.isEmpty(busitypeId))
		{ 
			return null;
		} 
		return BusiBusinessUtils.getResourcePath(busitypeId);
	} 
	 
}
