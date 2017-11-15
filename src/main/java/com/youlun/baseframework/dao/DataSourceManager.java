package com.youlun.baseframework.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.util.ExceptionUtils;
import com.youlun.baseframework.util.JSONUtils;
import com.youlun.baseframework.util.LogUtils;
import com.youlun.baseframework.util.ObjectUtils;

public class DataSourceManager {

	private static Map<Object, DataSource> mapds = new HashMap<Object, DataSource>();

	static {  
		initialDS();
	} 
	
	public static DataSource getDataSource(String key)
	{ 
		return mapds.get(key);
	}
	
	public static DataSource getDataSource()
	{
		String dsName = RunContext.getInstance().getDsName();
		if(StringUtils.isEmpty(dsName))
		{
			ExceptionUtils.throwBusinessException("RunContext's dsName is empty!");
		}
		return getDataSource(dsName);
	}

	@SuppressWarnings("rawtypes")
	private static synchronized void initialDS() {

		try {
			if (mapds.size() == 0) {
				JSONObject config = JSONUtils.readFileToJSON(DataSourceManager.class
						.getResourceAsStream("/config/ds.json"));
				JSONArray array = config.getJSONArray("ds");
				JSONObject jsonObj = null;
				BasicDataSource ds = null;
				Iterator it = null;
				String name = null;
				for (int i = 0; i < array.length(); i++) {
					jsonObj = array.getJSONObject(i);
					it = jsonObj.keys();
					ds = new BasicDataSource();
					while (it.hasNext()) {
						name = (String) it.next();
						if ("dskey".equals(name)) {
							LogUtils.info(name+"="+jsonObj.get(name));
							mapds.put(jsonObj.get(name), ds);
						} else {
							ObjectUtils.setField(ds, name, jsonObj.get(name));
						}
					}
				}
			}

		} catch (Throwable ex) {
			// TODO Auto-generated catch block
			ExceptionUtils.handler(ex);
			System.exit(1);
		}
	} 
}
