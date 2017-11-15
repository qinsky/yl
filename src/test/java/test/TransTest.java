package test;
import org.json.JSONObject;

import com.youlun.baseframework.util.HttpUtils;


public class TransTest {

	public static void main(String[] args) throws  Exception
	{   
		String url = "http://p4p.163.com/stat/getStatistics?urlToken=783B4E1B36DC64C865686975E2961961_11487";
		JSONObject json = new JSONObject("{\"timeType\":5,\"idType\":1,\"adPlanId\":0,\"adPlanName\":\"全部推广计划\",\"startDate\":\"2017-06-01\",\"endDate\":\"2017-08-29\",\"pageNo\":1,\"pageSize\":10}");
		byte[] data = HttpUtils.sendPostRequest(url, json);
		
		System.out.println(new String(data));
		
	}
	
	
	 
}
