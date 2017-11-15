package com.youlun.baseframework.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.youlun.auth.util.AuthBusinessUtils;
import com.youlun.baseframework.config.BusinessConfig;
import com.youlun.baseframework.util.ExceptionUtils;
import com.youlun.baseframework.util.HttpUtils;
import com.youlun.baseframework.util.JSONUtils;
import com.youlun.baseframework.util.StringUtils;

public class BusinessDispatcher{
	
	static Logger log = Logger.getLogger(BusinessDispatcher.class); 

	public  static void dispatchBusiness(HttpServletRequest request,HttpServletResponse response)
	{ 
		try
		{   
			prepared(request,response);
			
//			com.Test.getPaginationRow();
			//com.Test.insertRow();

			JSONObject params = RequestContext.getHttpParams();
			
			log.debug(params);
			
			JSONObject tobj = new JSONObject();
			/**
			 * qq9214_bt-业务类型、qq9214_md-模块
			 * 
			 * */
			if(params.has("qq9214_bt"))
			{    
				
				String qq9214Bt = (String) params.remove("qq9214_bt");
				RequestContext.setAttribute("qq9214Bt",qq9214Bt);
				
				tobj =  BusinessConfig.getBusitype(qq9214Bt);
				RequestContext.setAttribute("busiObj",tobj);
				AbstractBusinessHandler defaultHandler = (AbstractBusinessHandler) SpringContext.getBean("defaultHandler"); 
				if(tobj==null)
				{  
					
					defaultHandler.error("没有此业务,业务编码:"+qq9214Bt);
					return;
				}
				
				//校验当前请求的业务是否需要登录系统才能使用
				if(!"N".equalsIgnoreCase(tobj.getString("need_logined")))
				{
					String userId = RunContext.getInstance().getUserId();
					if(StringUtils.isEmpty(userId))
					{ 
						if("auth".equals(qq9214Bt) && JSONUtils.equals(params, "qq9214_at", "main"))
						{
							response.sendRedirect("/");
						}else
						{
							defaultHandler.error("登录超时，请重新登录!");
						} 
						//request.getRequestDispatcher().forward(request, response);
						return;
					}
					
					//校验当前业务操作是否需要权限验证
					if(tobj.has("need_privilege") && !"N".equals(tobj.getString("need_privilege")))
					{
						//进行权限验证
						boolean flag  = AuthBusinessUtils.checkBusiPrivilege(userId, tobj.getString("id"));
						if(!flag)
						{ 
							defaultHandler.error("没有此业务操作权限,业务编码:"+qq9214Bt);
							return;
						}
					}
				}
				
				//获取业务处理类
				AbstractBusinessHandler handler = getBusinessHandler(tobj); 
				handler.handler();
			}else
			{ 
				response.sendError(404);
			}
		}catch(Throwable ex)
		{
			ExceptionUtils.handler(ex);
			HttpUtils.writeError(ExceptionUtils.getMessage(ex), response);
		}
	} 
	
	public static AbstractBusinessHandler getBusinessHandler(JSONObject tobj)
	{
		AbstractBusinessHandler defaultHandler = (AbstractBusinessHandler) SpringContext.getBean("defaultHandler"); 
		String className = tobj.getString("class_name");
		AbstractBusinessHandler handler = null;
		if(StringUtils.isEmpty(className) && SpringContext.containsBean(tobj.getString("qq9214_bt")))
		{
			//如果没有设置处理类,哪必须有通过注释生成的 bean,bean的名称默认就是 “业务类型” 名称 
			handler = (AbstractBusinessHandler) SpringContext.getBean(tobj.getString("qq9214_bt"));
		}else if(SpringContext.containsBean(className))
		{
			//如果设置了处理类，则以处理类名作为bean的名称
			handler = SpringContext.getBean(className, AbstractBusinessHandler.class);
		}else
		{ 
			//如果类名不为空，通过类型来获取处理类
			if(!StringUtils.isEmpty(className))
			{
				try
				{
					handler = (AbstractBusinessHandler) SpringContext.getBean(Class.forName(className)); 
				}catch(Exception ex)
				{
					ExceptionUtils.throwException(ex);
				}
			}
			 
			//如果以上方法都没有获取到handler类，则使用默认的处理类
			if(handler==null)
			{
				//如果没有业务处理类 ，则使用默认的处理类
				handler = defaultHandler;  
			}
		} 
		return handler;
	}
	
	private static void prepared(HttpServletRequest request,HttpServletResponse response)
	{
		//获取请求参数
		JSONObject params = HttpUtils.getParams(request);
		//设置使用的数据源
		if(StringUtils.isEmpty(RunContext.getInstance().getDsName()))
		{
			 if(params.has("qq9214_dsName"))
			 {
				 RunContext.getInstance().setDsName(params.getString("qq9214_dsName"));
			 }else
			 {
				 RunContext.getInstance().setDsName("default");
			 }
		}
	} 
	
}
