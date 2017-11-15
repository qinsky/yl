package com.youlun.baseframework.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.youlun.baseframework.util.ExceptionUtils;
import com.youlun.baseframework.util.HttpUtils;
import com.youlun.baseframework.util.StringUtils;
 
 

public class RequestContextFilter implements Filter{
 
	Logger log = Logger.getLogger(RequestContextFilter.class);

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		try
		{
			//initial RequestContext
			RequestContext.initialContext(request, response);
			if(!urlFilter(request,response))
			{
				return;
			}
			chain.doFilter(request, response);
		}catch(Throwable ex){
			ExceptionUtils.handler(ex); 
			HttpUtils.writeError(ExceptionUtils.getMessage(ex), response);
		}finally
		{
			RequestContext.destoryContext();
		}
	}

	public boolean urlFilter(ServletRequest request,ServletResponse response) throws IOException 
	{
		 HttpServletRequest req = (HttpServletRequest)request;
		 HttpServletResponse rsp = (HttpServletResponse)response;
		 String url = req.getRequestURI();
		 if(url.startsWith("/module/"))
		 {
//			 if(!RunContext.getInstance().isLogined())
//			 {
//				 rsp.sendError(403);
//				 return false;
//			 }else{
				 String forbid = request.getServletContext().getInitParameter("forbid");
				 if(StringUtils.isNotEmpty(forbid))
				 {
					 String[] subs = forbid.split(",");
					 for(int i=0;i<subs.length;i++)
					 {
						 if(url.endsWith(subs[i]))
						 {
							 rsp.sendError(403);
							 return false;
						 }
					 }
					 
				 }
//			 }
		 }
		 
 		 return true;
	}
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
