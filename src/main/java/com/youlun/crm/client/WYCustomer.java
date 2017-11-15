package com.youlun.crm.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

import net.sourceforge.htmlunit.corejs.javascript.NativeObject;
import net.sourceforge.htmlunit.corejs.javascript.Undefined;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.youlun.baseframework.util.FileUtils;

public class WYCustomer {
	private Logger log = Logger.getLogger(this.getClass());
	private String userName;
	private String password;
	private String loginUrl = "";
	private String token = null;
	private String authCode = null;
	private byte[] imgAuthCode = null;
	private WebClient client;
	private WebWindow mWd = null;
	private WebWindow tWd = null;
	private boolean logined = false;
	private String errorTip = null;
	
	public WYCustomer()
	{
		
	}
	public WYCustomer(String userName,String password,String loginUrl)
	{
		this.userName = userName;
		this.password = password;
		this.loginUrl = loginUrl; 
	}
	

	public JSONObject getRunrecord(String beginDate,String endDate) throws Exception
	{ 
		String recode = "http://p4p.163.com/stat/getStatistics?urlToken="+token;
		WebRequest req = new WebRequest(new URL(recode),HttpMethod.POST);
		req.setAdditionalHeader("Content-Type", "application/json; charset=UTF-8");
		req.setCharset(Charset.forName("UTF-8"));
		req.setRequestBody("{\"timeType\":5,\"idType\":1,\"adPlanId\":0,\"adPlanName\":\"全部推广计划\",\"startDate\":\""+beginDate+"\",\"endDate\":\""+endDate+"\",\"pageNo\":1,\"pageSize\":100}");
		Page dataPage = client.getPage(tWd,req);
		return new JSONObject(dataPage.getWebResponse().getContentAsString(Charset.forName("UTF-8")));
	}
	
	public String getToken() throws InterruptedException
	{
		if(token!=null)
		{
			return token; 
		}
		
		HtmlPage page = ((HtmlPage)mWd.getEnclosedPage());  
		ScriptResult rs = null;
		int tryTimes = 0;
		String temp = null;
		while(true)
		{
			if(tryTimes==6)
			{
				return null;
			}
			rs = page.executeJavaScript("location.hash");
			temp = rs.getJavaScriptResult().toString();
			if(temp.indexOf("token=")<0)
			{
				Thread.sleep(500);
				tryTimes++;
			}else
			{
				token = temp.split("token=")[1];
				break;
			}
			
		} 
		return token; 
	}
	
	//初始化
	public void initial() throws Exception
	{
		if(client!=null)
		{
			client.close();
			
			//重置变量
			reset();
		}
		
		client = new WebClient(); 
		mWd= client.getPage(this.loginUrl).getEnclosingWindow(); 
		refreshAuthCode();
	} 
	
	//登录，登录前一定要先设置验证码
	public void login()
	{
		ScriptResult rs = null;
		HtmlPage page = (HtmlPage) mWd.getEnclosedPage();
		//设置用户名
		rs = page.executeJavaScript("jQuery('.login-submit').scope().userName='"+this.userName+"';");
		log.debug(rs.getJavaScriptResult());
		//设置密码
		rs = page.executeJavaScript("jQuery('.login-submit').scope().password='"+this.password+"';");
	
		//设置验证码
		rs=page.executeJavaScript("jQuery('.login-submit').scope().authCode='"+this.authCode+"';");
		log.debug(rs.getJavaScriptResult());
		
		//登录
		rs = page.executeJavaScript("jQuery('.login-submit').scope().onSubmit(4);"); 
		
		//判断是否登录成功
		Object obj = rs.getJavaScriptResult();
		if(obj instanceof Undefined)
		{
			//登录成功
			logined = true;
		}else if(obj instanceof Boolean)
		{ 
			//登录失败，获取失败信息
			NativeObject nObj = (NativeObject) page.executeJavaScript("jQuery('.login-submit').scope()").getJavaScriptResult();
			this.errorTip = nObj.get("errorTip").toString();
			logined =  false;
		}
	}
	 
	//刷新验证码
	public void refreshAuthCode() throws IOException
	{
		URL authCodeUrl = new URL("http://p4p.163.com/common/getDvCode?random="+(new Date()).getTime());
	
		if(tWd==null)
		{
			tWd =   client.openWindow(authCodeUrl, "authCode");
		}else
		{
			WebRequest request = new WebRequest(authCodeUrl,HttpMethod.GET);
			client.getPage(tWd, request);
		} 
		InputStream in = tWd.getEnclosedPage().getWebResponse().getContentAsStream();
		this.imgAuthCode = FileUtils.readData(in);
	}
	
	//判断是否已经登录
	public boolean isLogin()
	{
		return logined;
	}
	
	public void reset()
	{ 
		this.token = null;
		this.authCode = null;
		this.imgAuthCode = null;
		this.client = null;
		this.mWd = null;
		this.tWd = null;
		this.logined = false;
		this.errorTip = null;
	}
	
	public byte[] getImgAuthCode()
	{
		return this.imgAuthCode;
	}
	
	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}
	
	public void setUesrName(String userName)
	{
		this.userName = userName;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void setLoginUrl(String loginUrl)
	{
		this.loginUrl = loginUrl;
	}
	
}
