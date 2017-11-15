package com.youlun.auth.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;

import com.youlun.baseframework.dao.DBOperator;

public class AuthBusinessUtils {

	/**
	 * 获取用户所拥有的菜单
	 * @param userId
	 * @return
	 */
	public static JSONArray getUserMenu(String userId)
	{
		StringBuffer sql = new StringBuffer("select a.menu_id from ul_bsf_org_menu a where exists(select master_id from ul_bsf_org c where c.id=a.master_id and c.type='5' ) and exists(select master_id from ul_bsf_org_user b where   a.master_id=b.master_id and b.user_id='");
		sql.append(userId).append("')");
		JSONArray tArray = DBOperator.query(sql.toString());
		Set<String> ids = new HashSet<String>();
		for(int i=0;i<tArray.length();i++)
		{
			ids.add(tArray.getJSONObject(i).getString("menu_id"));
		} 
		 
		tArray = DBOperator.queryByKeysCascadeParent("ul_bsf_menu", ids.toArray(new String[0]), "parent_id"); 
		return tArray;
		
	}
	
	/**
	 * 获取用户所拥有的菜单
	 * @param userId
	 * @return
	 */
	public static JSONArray getAdminMenu()
	{
		StringBuffer sql = new StringBuffer("select * from ul_bsf_menu a where a.df!='Y'  ");
		JSONArray tArray = DBOperator.query(sql.toString());
		return tArray;
	}
	/**
	 * 校验用户是否拥有某个菜单的权限
	 * @param userid
	 * @param menuId
	 * @return
	 */
	public static boolean checkMenuPrivilege(String userid,String menuId)
	{
		StringBuffer sql = new StringBuffer("select ul from ul_bsf_dual where exists (select master_id from ul_bsf_org_user a where user_id=?");
		sql.append(" and exists(select  b.master_id from ul_bsf_org_menu b where a.master_id=b.master_id and b.menu_id=?))"); 
		List<Object> params = new ArrayList<Object>();
		params.add(userid);
		params.add(menuId); 
		JSONArray tArr = DBOperator.query(sql.toString(), params);
		if(tArr.length()==0)
		{
			return false;
		}
		return true;
	} 
	
	public static boolean checkBusiPrivilege(String userId,String busiId)
	{
		
		StringBuffer sql = new StringBuffer("select ul from ul_bsf_dual where exists (select master_id from ul_bsf_org_user a where user_id=?");
		sql.append(" and exists(select  b.master_id from ul_bsf_org_menu b where a.master_id=b.master_id ");
		sql.append(" and exists (select c.id from ul_bsf_menu c where c.id=b.menu_id and c.busitype_id=?)))");
		List<Object> params = new ArrayList<Object>();
		params.add(userId);
		params.add(busiId); 
		JSONArray tArr = DBOperator.query(sql.toString(), params);
		if(tArr.length()==0)
		{
			return false;
		}
		return true;
	} 
}
