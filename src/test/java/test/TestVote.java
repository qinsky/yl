package test;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.youlun.baseframework.util.FileUtils;

public class TestVote {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String vote = "http://dp876.com/json/v/teacherevaluation/update/27/57759";
		String sendData = "teacherName=宾格&score=1";
		String login = "http://dp876.com/json/login/check?password=123456&name=B08274";
//		byte[] data = HttpUtils.sendPostRequest(url,sendData.getBytes());
	    HttpClient client = HttpClients.createDefault();
	   
	    try
	    {  
//	    	for(int i=8279;i>8200;i--)
//	    	{
		    	HttpGet loginRe = new HttpGet();
//	   	        loginRe.setURI(new URI("http://localhost:8080/facade.do")); 
//		    	loginRe.setURI(new URI(login+i));  
		    	loginRe.setURI(new URI(login));  
		    	HttpResponse rsp = client.execute(loginRe); 
//		    	rsp.getHeaders("Cookie");
//		    	rsp.getAllHeaders();
		    	byte[] rspC = FileUtils.readData(rsp.getEntity().getContent());
		    	System.out.println(new String(rspC));
		    	
//		    	loginRe.reset();
//		    	loginRe.setURI(new URI("http://dp876.com/v/1"));
//		    	rsp = client.execute(loginRe);  
//		    	rspC = FileUtils.readData(rsp.getEntity().getContent());
//		    	System.out.println(new String(rspC));
		    	
//		    	loginRe.reset();
//		    	loginRe.setURI(new URI("http://dp876.com"));
//		    	rsp = client.execute(loginRe);  
//		    	rspC = FileUtils.readData(rsp.getEntity().getContent()); 
//		    	System.out.println(new String(rspC));
		    	
		    	
		    	HttpPost votePost = new HttpPost(); 
//		    	votePost.setURI(new URI("http://dp876.com/json/v/lesson/week"));
		    	List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
//		    	nvps.add(new BasicNameValuePair("roomId", "27"));
		    	HttpEntity entity = new UrlEncodedFormEntity(nvps);
//		    	votePost.setEntity(entity); 
//		    	rsp = client.execute(votePost);
//		    	rspC = FileUtils.readData(rsp.getEntity().getContent()); 
//		    	System.out.println(new String(rspC));
		    	
		    	
		    	votePost.reset();
		    	votePost.setURI(new URI(vote));
		    	nvps.clear();
		    	nvps.add(new BasicNameValuePair("teacherName", "宾格"));
		    	nvps.add(new BasicNameValuePair("score", "1"));
		    	entity =new UrlEncodedFormEntity(nvps,Charset.forName("UTF-8"));
		    
		    	votePost.setEntity(entity); 
		    	
		    	rsp = client.execute(votePost);  
		    	
//		    	rsp = client.execute(loginRe);
		    	 
		    	rspC = FileUtils.readData(rsp.getEntity().getContent());
		    	System.out.println(new String(rspC));
//	    	} 
	    	
	    }catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    }
		
	}

}
