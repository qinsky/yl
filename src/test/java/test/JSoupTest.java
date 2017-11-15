package test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.youlun.baseframework.util.FileUtils;

public class JSoupTest {
	
	static Logger log = Logger.getLogger(JSoupTest.class);

	/**
	 * @param args
	 * @throws IOException 
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub 
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		
		HtmlPage page = webClient.getPage(new URL("http://p4p.163.com/customer/app-login.html#/login"));
		
		String auThCode = "http://p4p.163.com/common/getDvCode?random="+(new Date()).getTime();
		
//		ScriptResult rs =  page.executeJavaScript("jQuery('.ng-scope').scope()");
		
//		NativeObject no = (NativeObject) rs.getJavaScriptResult();
		WebWindow mainWd = webClient.getCurrentWindow();
		WebWindow tempWd =   webClient.openWindow(new URL(auThCode), "authCode");
		InputStream in = tempWd.getEnclosedPage().getWebResponse().getContentAsStream();
		FileUtils.saveToFile(in, new File("D:/ttt.jpg"),true); 
		
		ScriptResult rs = null;
		rs = page.executeJavaScript("jQuery('.login-submit').scope().userName='苏宁云商';");
		log.info(rs.getJavaScriptResult());
		rs = page.executeJavaScript("jQuery('.login-submit').scope().password='suning123';");
		log.info(rs.getJavaScriptResult());
		//page.executeJavaScript("jQuery('.login-submit').scope().authCode='bxy5';");
		rs = page.executeJavaScript("jQuery('.login-submit').scope().onSubmit(4);");
		log.info(rs.getJavaScriptResult());  
//		NativeObject no = (NativeObject) page.executeJavaScript("jQuery('.login-submit').scope()").getJavaScriptResult();
//		System.out.println(no.get("errorTip")); 
		
		
		page = ((HtmlPage)mainWd.getEnclosedPage()); 
		rs = page.executeJavaScript("location.hash");
		System.out.println(page.getReadyState());
		String token = rs.getJavaScriptResult().toString().split("token=")[1];
		String recode = "http://p4p.163.com/stat/getStatistics?urlToken="+token;
		WebRequest req = new WebRequest(new URL(recode),HttpMethod.POST);
		req.setAdditionalHeader("Content-Type", "application/json; charset=UTF-8");
		req.setCharset(Charset.forName("UTF-8"));
		req.setRequestBody("{\"timeType\":5,\"idType\":1,\"adPlanId\":0,\"adPlanName\":\"全部推广计划\",\"startDate\":\"2017-06-13\",\"endDate\":\"2017-06-16\",\"pageNo\":1,\"pageSize\":10}");
		Page dataPage = webClient.getPage(tempWd,req);
		System.out.println(dataPage.getWebResponse().getContentAsString(Charset.forName("UTF-8")));
		System.out.println(dataPage);
		
		//page.executeJavaScript("jQuery('.login-submit').scope()");
		FileUtils.saveToFile(page.asText(),"D:/suning.xml",true);
//		webClient.addWebWindowListener(new WebWindowListener());
		 
	
//		Iterator it = no.keySet().iterator();
//		Object obj = null;
//		while(it.hasNext())
//		{
//			obj = it.next();
//			System.out.println(obj+" = "+no.get(obj));
//		}
//		System.out.println(rs.getJavaScriptResult());
		//Window window = webClient.getCurrentWindow().getScriptableObject();
		 
		//window.fireEvent(null);
//		log.info("curUrl = "+page.getUrl());
//		List<DomElement> list = page.getElementsByTagName("a");
//		log.info("list size is "+list.size());
//		String href = null;
//		for(int i=0;i<list.size();i++)
//		{
//			href = list.get(i).getAttribute("href"); 
//			try
//			{ 
//				if(href.startsWith("http://www.baidu.com/link"))
//				{
//					log.info(href); 
//					URLConnection con = new URL(href).openConnection();
//					con.connect(); 
//					con.getHeaderFields();
//					log.info(con.getURL()); 
//					log.info(list.get(i).getTextContent());
//				} 
//			}catch(Exception ex)
//			{
//				log.info("发生错误！！！-->"+ex.getMessage()); 
//			} 
//		}
		webClient.close(); 
	}

}
