package com.youlun.baseframework.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.core.SpringContext;
import com.youlun.baseframework.util.ExceptionUtils;
import com.youlun.baseframework.util.JSONUtils;

public class DatabaseMetaInfo {
	
	private static JSONObject metaCache =  new JSONObject();

	public static JSONObject getColumnsMeta(String tableName)
	{ 
		 
			tableName = tableName.toUpperCase();
			String dsName =  RunContext.getInstance().getDsName();
			
			String path = dsName+"."+tableName;
			Object obj = JSONUtils.getValue(metaCache, path+".cols");
			if(obj!=null)
			{
				return (JSONObject) obj;
			} 
	 
			
			JSONUtils.generalJsonPath(metaCache, path);
			
			Connection con = DataSourceUtils.getConnection(SpringContext.getBean("dataSource",DataSource.class));
			ResultSet rs = null;
			JSONObject tJson = null;
			try
			
			{
				DatabaseMetaData dsMeta = con.getMetaData();
				
				rs = dsMeta.getColumns(null, null, tableName, null); 
			
				JSONArray colsMeta = DBOperator.processRs(rs); 
				if(colsMeta.length()==0)
				{
					return null;
				}
				tJson = JSONUtils.convertArrayToJsonWithLowerCase("column_name",colsMeta);
				JSONUtils.setValue(metaCache, path+".cols", tJson);
			}catch(Exception ex)
			{
				ExceptionUtils.throwException(ex);
			}finally
			{
				JdbcUtils.closeResultSet(rs); 
				DBOperator.releaseConnection(con);
			}
			return tJson; 	
			 
			
	}
	
	public static JSONObject getTablePrimaryKeys(String tableName)
	{ 
		 
		tableName = tableName.toUpperCase();
		String dsName = RunContext.getInstance().getDsName();
		String path = dsName+"."+tableName;
		Object obj = JSONUtils.getValue(metaCache, path+".pks");
		if(obj!=null)
		{
			return (JSONObject) obj;
		} 
		
		JSONUtils.generalJsonPath(metaCache, path);
		
		Connection con = DataSourceUtils.getConnection(SpringContext.getBean("dataSource",DataSource.class));
		ResultSet rs = null;
		JSONObject tJson = null;
		try
		{
			DatabaseMetaData dsMeta = con.getMetaData();
			rs = dsMeta.getPrimaryKeys(null, null, tableName);
		
			JSONArray colsMeta = DBOperator.processRs(rs); 
			if(colsMeta.length()==0)
			{
				return null;
			}
			tJson = JSONUtils.convertArrayToJsonWithLowerCase("column_name",colsMeta);
			JSONUtils.setValue(metaCache, path+".pks", tJson);
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}finally
		{
			JdbcUtils.closeResultSet(rs); 
			DBOperator.releaseConnection(con);
		}
		return tJson;
		
		
	 
	}
}

