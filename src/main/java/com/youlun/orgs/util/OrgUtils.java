package com.youlun.orgs.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.JSONUtils;
import com.youlun.baseframework.util.StringUtils;

public class OrgUtils {
	
	public static enum StaffType{
		 ALL,
		 KEY_POST,
		 NOT_KEY_POST
	 }
	 
	/**
	 * 根据组织编码查询组织管理人员
	 * @param orgName
	 * @return
	 */
	public static List<String>  getAdminByOrgCode(String orgCode)
	{
		List<String> temp = new ArrayList<String>(); 
		StringBuffer buf = new StringBuffer(" select a.user_id from ul_bsf_org_user a where  a.df!='Y' and a.is_admin='Y' and");
		buf.append(" exists(select b.id from ul_bsf_org b where b.id=a.master_id and b.df!='Y' and b.parent_id in (select c.id from ul_bsf_org c where c.code=? and c.df!='Y'))");
		temp.add(orgCode);
		JSONArray users = DBOperator.query(buf.toString(), temp.toArray());
		if(users==null)
		{
			temp.clear();
			return null;
		}
		temp.clear();
		for(int i=0;i<users.length();i++)
		{
			temp.add(users.getJSONObject(i).getString("user_id"));
		}
		return temp;
	}
	
	/**
	 * 根据组织ID查询组织管理人员
	 * @param orgId
	 * @return
	 */
	public static List<String>  getAdminByOrgId(String orgId)
	{
		List<String> temp = new ArrayList<String>(); 
		StringBuffer buf = new StringBuffer(" select a.user_id from ul_bsf_org_user a where  a.df!='Y' and a.is_admin='Y' and");
		buf.append(" exists(select b.id from ul_bsf_org b where b.id=a.master_id and b.df!='Y' and b.parent_id = ?)");
		temp.add(orgId);
		JSONArray users = DBOperator.query(buf.toString(), temp.toArray());
		if(users==null)
		{
			temp.clear();
			return temp;
		}
		temp.clear();
		for(int i=0;i<users.length();i++)
		{
			temp.add(users.getJSONObject(i).getString("user_id"));
		}
		return temp;
	}
	/**
	 * 根据用户id,查询用户所属主要岗位的与参数orgType指定类型一致的上级或上上级或上上上...，的管理人员
	 * @param userId
	 * @param orgType 1、集团 ;2、公司;3、部门;5、岗位; 6、小组
	 * @return
	 */
	public static List<String>  getOrgAdminByUser(String userId,String orgType)
	{ 
		StringBuffer buf = new StringBuffer("select master_id from ul_bsf_org_user where df!='Y' and is_key_post='Y' and user_id=? ");
		JSONObject json = DBOperator.queryForSingle(buf.toString(),new Object[]{userId});
		if(json==null)
		{
			return null;
		}
		String orgId = getOrgByType(json.getString("master_id"),orgType);
		return getAdminByOrgId(orgId); 
	}
	
	/**
	 * 根据当前组织ID,递归查询上级组织，直到查询到组织类型与参数所指类型一致
	 * @param orgId
	 * @param type
	 * @return
	 */
	private static String getOrgByType(String orgId,String type)
	{ 
		String sql = "select id,parent_id,type from ul_bsf_org where df!='Y' and id=?";
		JSONObject org = DBOperator.queryForSingle(sql, new Object[]{orgId});
		if(org==null)
		{
			return null;
		}
		if(JSONUtils.equals(org, "type", type))
		{
			return orgId;
		}else
		{
			return getOrgByType(org.getString("parent_id"),type);
		} 
	}
	
	/**
	 * 查询岗位人员
	 * @param orgId
	 * @param type
	 * @return
	 */
	public static List<String> getPostStaff(String orgCode,StaffType type)
	{ 
		List<String> ls = new ArrayList<String>();
		if(StringUtils.isEmpty(orgCode))
		{
			return ls;
		}
		
		StringBuffer sql = new StringBuffer("select b.user_id from ul_bsf_org a, ul_bsf_org_user b where a.df!='Y' and b.df!='Y'");
		sql.append(" and a.id = b.master_id and a.type=5 ");
		switch(type){ 
			case KEY_POST:
				 sql.append(" and b.is_key_post='Y'");
				 break;
			case NOT_KEY_POST:
				 sql.append(" and b.is_key_post!='Y'");
				 break;
			default:
				break;
		} 
		sql.append(" and a.code=?");
		
		JSONArray users = DBOperator.query(sql.toString(), new Object[]{orgCode});
		for(int i=0;i<users.length();i++)
		{
			ls.add(users.getJSONObject(i).getString("user_id"));
		}
		return ls;
	}
}
