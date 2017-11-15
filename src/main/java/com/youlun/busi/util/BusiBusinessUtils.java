package com.youlun.busi.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.youlun.baseframework.core.RequestContext;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.AppUtils;
import com.youlun.meta.MetaBusinessUtils;

public class BusiBusinessUtils {

	public static String getViewPath(String busiId)
	{
		JSONObject busi = DBOperator.queryByPrimaryKey("ul_bsf_busitype", busiId);
		  String busiCode = busi.getString("qq9214_bt");
		  RequestContext.setAttribute("cur_qq9214_bt", busiCode);
		  
		  String module = busi.getString("qq9214_md");
		  if(busi!=null && !"".equals(busi.getString("view_path")))
		  {  
			  return busi.getString("view_path");
		  }
		  String ulAppPath = System.getProperty("ul.app.path");
		  StringBuffer viewPath = new StringBuffer("module/");
		  viewPath.append(module).append("/");
		  viewPath.append(busiCode).append("/").append(busiCode);
	 
		  File file = new File(ulAppPath+"/"+viewPath+".jsp");
		  if(file.exists())
		  { 
			  return viewPath+".jsp";
		  }
		  
		  file = new File(ulAppPath+"/"+viewPath+".html");
		  if(file.exists())
		  { 
			  return viewPath+".html";
		  }
		  
		  String metaId = busi.getString("meta_id");
		  if(!StringUtils.isEmpty(metaId))
		  {
			  JSONObject obj = MetaBusinessUtils.getUIMeta(metaId);
			  String curVP = null;
			  if(obj!=null && !StringUtils.isEmpty(obj.getString("uiType")))
			  {
				  String uiType = obj.getString("uiType");
				  viewPath.delete(0, viewPath.length());
				  if("tree".equals(uiType))
				  {
					  viewPath.append("module");
					  viewPath.append("/").append("bsf");
					  viewPath.append("/").append("defaultTree");
					  viewPath.append("/").append("default.jsp");
					  curVP = viewPath.toString();
				  }else if("grid".equals(uiType))
				  {
					  viewPath.append("module");
					  viewPath.append("/").append("bsf");
					  viewPath.append("/").append("defaultGrid");
					  viewPath.append("/").append("default.jsp");
					  curVP = viewPath.toString();
				  }
				  
				  if(curVP!=null)
				  {
					  RequestContext.setAttribute("defaultView",true);
					  return curVP;
				  }
				 
			  } 
		  }
		  
		  return null;
	}
	
	public static List<String> getResourcePath(String busiId)
	{
		  JSONObject busi = DBOperator.queryByPrimaryKey("ul_bsf_busitype", busiId);
		  String busiCode = busi.getString("qq9214_bt");
		  RequestContext.setAttribute("cur_qq9214_bt", busiCode);
		  
		  String module = busi.getString("qq9214_md");

		  String ulAppPath = System.getProperty("ul.app.path");
		  StringBuffer viewPath = new StringBuffer("module/");
		  viewPath.append(module).append("/");
		  viewPath.append(busiCode).append("/").append(busiCode);
	 
		  List<String> lsRs = new ArrayList<String>();
		  File file = new File(ulAppPath+"/"+viewPath+".js");
		  if(file.exists())
		  {  
			  lsRs.add(viewPath+".js?v="+AppUtils.getResLastTimeStr(viewPath+".js"));
		  }
		  
		  file = new File(ulAppPath+"/"+viewPath+".css");
		  if(file.exists())
		  {  
			  lsRs.add(viewPath+".css?v="+AppUtils.getResLastTimeStr(viewPath+".css"));
		  }
		  return lsRs;
	}
}
