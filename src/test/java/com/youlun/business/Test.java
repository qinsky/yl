package com.youlun.business;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.DateUtils;
import com.youlun.baseframework.util.ExceptionUtils;
import com.youlun.baseframework.util.FileUtils;
import com.youlun.baseframework.util.MD5;
 
@Component("com.youlun.business.Test") 
@Scope("prototype")
public class Test  extends Thread{
	int num = 0;
	static Logger log = Logger.getLogger(Test.class);
	

	
	public static void main(String[] args)
	{
		try
		{ 
			
//			System.out.println(MD5.encrypt("123455"));
//			JSONObject obj = new JSONObject();
//			Object t = obj.get("ok");
//			System.out.println(t);
//			byte[] a = new byte[11];
//			System.out.println(DateUtils.getCurDateTime());
//			String uuid = java.util.UUID.randomUUID().toString();
//			System.out.println(uuid.length()); 
//			System.out.println(uuid.toUpperCase());
			
//			for(int i=0;i<60000;i++)
//			{
//				log.info(" i == "+i);
//				Test t = new Test();
//				t.start(); 
//			}
//			 
//			Thread.sleep(10000000);
		}catch(Exception ex)
		{
		 ex.printStackTrace();
		}finally
		{
			System.out.println("finally");
		}
	}
	
	
	public void run()
	{
		URL url = null;
		try {
			url = new URL("http://localhost:8080/baseframework/facade.do?qq9214_md=bsf&qq9214_bt=reference&refType=orgs");
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			byte[] data = FileUtils.readData(in);
			log.info(new String(data,"UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Test()
	{
		num = new Random().nextInt();
	}
	
	@org.springframework.transaction.annotation.Transactional 
	public void insertTest()
	{  
		log.debug("num = "+num);
		JSONObject bean = new JSONObject();
	 
		bean.put("name", "myname");
		bean.put("age", 10);
		try {
			DBOperator.insert("tbl_test", bean); 
			ExceptionUtils.throwException("test error!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ExceptionUtils.throwException(e);
		} 
	}
	
	@Override
	protected void finalize() throws Throwable {
		log.debug("number was destroied! num = "+num);
	}
	
	
}
