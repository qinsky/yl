package com.youlun.baseframework.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import com.youlun.baseframework.core.SpringContext;
import com.youlun.baseframework.util.ArrayUtils;
import com.youlun.baseframework.util.DateUtils;
import com.youlun.baseframework.util.ExceptionUtils;
import com.youlun.baseframework.util.JSONUtils;
import com.youlun.baseframework.util.StringUtils;

public class DBOperator {
	
	static Logger log = Logger.getLogger(DBOperator.class);

	public static JSONArray query(String sql)
	{ 
		return query(sql,new Object[0]);
	}
	
	public static JSONArray query(String sql,List<Object> params)
	{ 
		Object[] arrs = null;
		if(params!=null && params.size()>0)
		{
			arrs = params.toArray(new Object[0]);
		}
		return query(sql,arrs);
	}
	public static JSONArray query(String sql,Object[] params)
	{ 
		if(StringUtils.isEmpty(sql))
		{
			ExceptionUtils.throwException("Invalid sql: "+sql);
		} 
		Connection con = DataSourceUtils.getConnection(SpringContext.getBean("dataSource",DataSource.class));
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{ 
			log.debug("Execute QuerySQL = "+sql);
			ps = con.prepareStatement(sql);
			if(ArrayUtils.isNotEmpty(params))
			{
				for(int i=1;i<=params.length;i++)
				{
					ps.setObject(i, params[i-1]);
				}
			}
		    rs = ps.executeQuery();
			return processRs(rs);  
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}finally
		{
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
			releaseConnection(con);
		}
		return null;
	}
	
	public static JSONObject queryForSingle(String sql)
	{ 
		JSONArray temp = query(sql);
		if(temp.length()>0)
		{
			return temp.getJSONObject(0);
		}
		return null;
	}
	
	public static JSONObject queryForSingle(String sql,Object[] params)
	{ 
		
		JSONArray temp = DBOperator.query(sql, params);
		if(temp.length()>0)
		{
			return temp.getJSONObject(0);
		}
		return null;
	}
	public static JSONObject queryForSingle(String sql,List<Object> params)
	{ 
		
		JSONArray temp = DBOperator.query(sql, params);
		if(temp.length()>0)
		{
			return temp.getJSONObject(0);
		}
		return null;
	}
	
	public static JSONArray processRsPagination(ResultSet rs,int pageNumber,int pageSize)
	{
		try
		{
			JSONArray result = new JSONArray(); 
			ResultSetMetaData rsMeta = rs.getMetaData();
			JSONObject obj = null;
			int beginIndex = -1;
			if(pageNumber!=-1 && pageSize>0)
			{
				beginIndex = (pageNumber-1)*pageSize;
			}
			int curIndex = 0;
			boolean needPage  = pageSize!=-1;
			int fetchCount = 0;
			
			while(rs.next())
			{
				if(needPage)
				{
					if(curIndex<beginIndex)
					{
						curIndex++;
						continue;
					}
				}
				obj = new JSONObject();
				Object tValue = null;
				for(int i=1;i<=rsMeta.getColumnCount();i++)
				{
					tValue = rs.getObject(i);
					if(tValue==null)
					{
						tValue = "";
					} 
					obj.put(rsMeta.getColumnLabel(i).toLowerCase(), tValue);
				}
				result.put(obj);
				
				fetchCount++;
				if(fetchCount==pageSize)
				{
					break;
				}
			} 
			return result;
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}
		return null;
	}
	
	public static JSONArray processRs(ResultSet rs)
	{
		return processRsPagination(rs,-1,-1);
	}
	
	public static JSONObject processFirstRs(ResultSet rs) 
	{ 
		try
		{
			ResultSetMetaData rsMeta = rs.getMetaData();
			JSONObject obj = null;
			if(rs.next())
			{ 
				obj = new JSONObject();
				Object tValue = null;
				for(int i=1;i<=rsMeta.getColumnCount();i++)
				{
					tValue = rs.getObject(i);
					if(tValue==null)
					{
						tValue = "";
					}
					obj.put(rsMeta.getColumnLabel(i).toLowerCase(),tValue);
				} 
			} 
			return obj;
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}
		return null;
	}
	
	public static JSONObject queryByWhereForSingle(String tableName,JSONObject params)
	{
		return queryByWhereForSingle(tableName,params,null);
	} 
	
	public static JSONObject queryByWhereForSingle(String tableName,JSONObject params,String[] fields)
	{
		JSONArray temp = queryByWhere(tableName,params,fields);
		if(temp.length()>0)
		{
			return temp.getJSONObject(0);
		}
		return null;
	}
	
	public static JSONObject queryByWhereForSingle(String tableName,String where,List<Object> params)
	{
		JSONArray temp = queryByWhere(tableName,where,params);
		if(temp.length()>0)
		{
			return temp.getJSONObject(0);
		}
		return null;
	}
	
	public static JSONArray queryByKeys(String tableName,Object[] ids)
	{
		return queryByKeys(tableName,ids,null);
	}
	public static JSONArray queryByKeys(String tableName,Object[] ids,JSONObject param)
	{
		if(ids==null||ids.length==0)
		{
			return new JSONArray();
		}
		List<Object> lsIds = new ArrayList<Object>();
		for(int i=0;i<ids.length;i++)
		{
			lsIds.add(ids[i]);
		}
		
		return queryByKeys(tableName,lsIds,param); 
	}
	public static JSONArray queryByKeys(String tableName,JSONArray ids)
	{
		return queryByKeys(tableName,ids,null);
	}
	public static JSONArray queryByKeys(String tableName,JSONArray ids,JSONObject param)
	{
		if(ids==null||ids.length()==0)
		{
			return new JSONArray();
		}
		List<Object> lsIds = new ArrayList<Object>();
		for(int i=0;i<ids.length();i++)
		{
			lsIds.add(ids.get(i));
		}
		
		return queryByKeys(tableName,lsIds,param); 
	}
	public static JSONArray queryByKeys(String tableName,List<Object> ids)
	{
		return queryByKeys(tableName,ids,null);
	}
	public static JSONArray queryByKeys(String tableName,List<Object> ids,JSONObject another)
	{
		if(ids==null || ids.size()==0)
		{
			return new JSONArray();
		}
		
		JSONObject pk = DatabaseMetaInfo.getTablePrimaryKeys(tableName);
		if(pk==null)
		{
			ExceptionUtils.throwException("The table '"+tableName+"' has no primary key!");
		}
		if(pk.length()>1)
		{
			ExceptionUtils.throwException("The table '"+tableName+"' has too many primary key than one!");
		}
		String strPk = pk.keys().next();
		StringBuffer where = new StringBuffer(" and ");
		where.append(strPk).append(" in(");
		List<Object> param = new ArrayList<Object>();
		for(int i=0;i<ids.size();i++)
		{
			where.append("?,");
			param.add(ids.get(i));
		} 
		where.delete(where.length()-1, where.length());
		where.append(")");
		if(another!=null&&another.length()>0)
		{
			Iterator<String> it = another.keys();
			String key = null;
			while(it.hasNext())
			{
				key = it.next();
				where.append(" and ").append(key).append("=?");
				param.add(another.get(key));
			} 
		}
		return queryByWhere(tableName,where.toString(),param);
	}
	 
	public static JSONArray queryByWhere(String tableName,String where,List<Object> params)
	{
		if(StringUtils.isEmpty(tableName))
		{
			ExceptionUtils.throwException("Invalid parameter tableName: "+tableName);
		} 
		 
		 
		StringBuffer sqlBuf = generalQuerySql(tableName);  
		 
		JSONObject colsMeta = DatabaseMetaInfo.getColumnsMeta(tableName);
		sqlBuf.append(" where 1=1 ");
		if(colsMeta.has("df"))
		{
			sqlBuf.append(" and df!='Y' ");
		}
		if(StringUtils.isNotEmpty(where))
		{ 
			sqlBuf.append(where);
		} 
		 
		log.debug("QuerySQL = "+sqlBuf+" ; query parameter: "+params);
		
		Connection con  = DataSourceUtils.getConnection(SpringContext.getBean("dataSource",DataSource.class));
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{ 
			ps = con.prepareStatement(sqlBuf.toString());
			if(params!=null && params.size()>0)
			{
				for(int i=1;i<=params.size();i++)
				{
					ps.setObject(i, params.get(i-1));
				} 
			} 
			rs = ps.executeQuery();
		    return processRs(rs); 
		}catch(Exception ex){
			ExceptionUtils.throwException(ex);
		}finally
		{
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
			releaseConnection(con);
		}  
		return null;
	}
	
	public static JSONArray queryByWhere(String tableName,String where,JSONArray params)
	{ 
		StringBuffer sqlBuf = generalQuerySql(tableName);  
		sqlBuf.append(" where 1=1 ").append(where);
		return DBOperator.query(sqlBuf.toString(), JSONUtils.jsonArrayToList(params));
	}
	
	/**
	 * 根据params中的 fields 对应的字段作为查询条件，进行查询，如果fields 为空，那么parmas中所有的字段都是查询参数
	 * @param tableName  
	 * @param params
	 * @param fields
	 * @return
	 */
	public static JSONArray queryByWhere(String tableName,JSONObject params,String[] fields)
	{
		if(StringUtils.isEmpty(tableName))
		{
			ExceptionUtils.throwException("Invalid parameter tableName: "+tableName);
		}
		
//		if(params==null||params.length()==0)
//		{
//			ExceptionUtils.throwException("Invalid query parameter params: "+params);
//		}
		 
		 
		StringBuffer sqlBuf = generalQuerySql(tableName);  
		sqlBuf.append(" where 1=1 ");
		
		List<Object> values = new ArrayList<Object>();
		String strTemp = null;
		JSONObject colsMeta = DatabaseMetaInfo.getColumnsMeta(tableName);
		if(colsMeta==null||colsMeta.length()==0)
		{
			ExceptionUtils.throwException("The table '"+tableName+"' is not exists!");
		}
		
		if(colsMeta.has("df"))
		{
			sqlBuf.append(" and DF!='Y' ");
		}
		if(!ArrayUtils.isEmpty(fields))
		{
			if(params==null||params.length()==0)
			{
				ExceptionUtils.throwException("Query parameter is empty! params: "+params);
			}
			for(int i=0;i<fields.length;i++)
			{
				strTemp = fields[i];
				if(colsMeta.has(strTemp.toLowerCase()))
				{
					sqlBuf.append(" and ").append(strTemp).append(" = ?"); 
					values.add(getCorrespondingParam(params.get(strTemp),colsMeta.getJSONObject(strTemp.toLowerCase())));
				}else
				{
					ExceptionUtils.throwException("The field '"+strTemp+"' is not exists in "+tableName+"!");
				}
			}
		}else if(params!=null && params.length()>0)
		{
			Iterator<String> it = params.keys();
			while(it.hasNext())
			{
				strTemp = it.next();
				
				if(colsMeta.has(strTemp.toLowerCase()))
				{
					sqlBuf.append(" and ").append(strTemp).append(" = ?"); 
					values.add(getCorrespondingParam(params.get(strTemp),colsMeta.getJSONObject(strTemp.toLowerCase())));
				}
			}
		}
//		if(values.size()==0)
//		{
//			ExceptionUtils.throwException("The parameter 'params' has no valid query field for table '"+tableName+"', params: "+params);
//		} 
		log.debug("QuerySQL = "+sqlBuf+" ; query parameter: "+params);
		
		Connection con  = DataSourceUtils.getConnection(SpringContext.getBean("dataSource",DataSource.class));
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{ 
			ps = con.prepareStatement(sqlBuf.toString());
			for(int i=1;i<=values.size();i++)
			{
				ps.setObject(i, values.get(i-1));
			} 
			rs = ps.executeQuery();
		    return processRs(rs); 
		}catch(Exception ex){
			ExceptionUtils.throwException(ex);
		}finally
		{
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
			releaseConnection(con);
		}  
		return null;
	}
	
	public static JSONArray queryByWhere(String tableName) 
	{
		 return queryByWhere(tableName,null);
	}
	
	public static JSONArray queryByWhere(String tableName,JSONObject params) 
	{
		 return queryByWhere(tableName,params,null);
	}
	
	public static JSONObject queryColumnsByPrimaryKey(String tableName,Object key,String[] columns)
	{
		if(StringUtils.isEmpty(tableName))
		{
			ExceptionUtils.throwException("Invalid query parameter tablename: "+tableName);
		}
		if(key==null)
		{
			ExceptionUtils.throwException("Invalid query parameter key: "+key);
		}
		if(ArrayUtils.isEmpty(columns))
		{
			ExceptionUtils.throwException("Invalid query parameter columns: "+columns);
		}
		StringBuffer sqlBuf = new StringBuffer("select ");
		for(int i=0;i<columns.length;i++)
		{
			sqlBuf.append(columns[i]).append(",");
		}
		sqlBuf.delete(sqlBuf.length()-1, sqlBuf.length()); 
		
		JSONObject pks = DatabaseMetaInfo.getTablePrimaryKeys(tableName); 
		if(pks==null||pks.length()==0)
		{
			ExceptionUtils.throwException("The table '"+tableName+"' has no PrimaryKey!");
		}
		
		if(pks.length()!=1)
		{
			ExceptionUtils.throwException("The table "+tableName+" has more PrimaryKey column than 1!");
		}
		String pkName = pks.keys().next();
		sqlBuf.append(" from ").append(tableName).append(" where ").append(pkName);
		sqlBuf.append(" = ?");
		
		//如果有逻辑删除机制，则排除删除过的数据
		JSONObject colsMeta = DatabaseMetaInfo.getColumnsMeta(tableName); 
		if(colsMeta.has("df"))
		{
			sqlBuf.append(" and df!='Y' ");
		}
		
		return queryForSingle(sqlBuf.toString(),new Object[]{key}); 
	}
	
	public static JSONObject queryByPrimaryKey(String tableName,Object key) 
	{
 
		if(StringUtils.isEmpty(tableName))
		{
			ExceptionUtils.throwException("Invalid query parameter tablename: "+tableName);
		}
		if(key==null)
		{
			ExceptionUtils.throwException("Invalid query parameter key: "+key);
		}
		
		 
		StringBuffer sqlBuf = generalQuerySql(tableName); 
		JSONObject pks = DatabaseMetaInfo.getTablePrimaryKeys(tableName);
		
		if(pks==null||pks.length()==0)
		{
			ExceptionUtils.throwException("The table '"+tableName+"' has no PrimaryKey!");
		}
		
		if(pks.length()!=1)
		{
			ExceptionUtils.throwException("The table "+tableName+" has more PrimaryKey column than 1!");
		}
		String pkName = pks.keys().next();
		sqlBuf.append(" where ").append(pkName.toLowerCase());
		sqlBuf.append(" = ?");
		
		Connection con = DataSourceUtils.getConnection(SpringContext.getBean("dataSource",DataSource.class));
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{ 
		 
			log.debug("QuerySql = "+sqlBuf+" ; query parameter key ="+key);
			ps = con.prepareStatement(sqlBuf.toString());
			ps.setObject(1,key);
			rs = ps.executeQuery();
			return processFirstRs(rs);  
		}catch(Exception ex){
			ExceptionUtils.throwException(ex);
		}finally
		{
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
			releaseConnection(con);
		}
		return null; 
	}
	
	public static void releaseConnection(Connection con)
	{
		try {
			DataSourceUtils.doReleaseConnection(con, SpringContext.getBean("dataSource",DataSource.class));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("释放连接错误", e);
		}
	}
	
	public static StringBuffer generalQuerySql(String tableName)
	{ 
		StringBuffer buf = new StringBuffer("select "); 
		JSONObject colsMeta = DatabaseMetaInfo.getColumnsMeta(tableName);
		if(colsMeta==null||colsMeta.length()==0)
		{
			ExceptionUtils.throwException("The table '"+tableName+"' is not exists!");
		}
		JSONObject json = null;
		Iterator<String> it = colsMeta.keys();
		String key = null;
		while(it.hasNext())
		{
			key = it.next();
			if(key.equalsIgnoreCase("DF"))
			{
				continue;
			}
			json = colsMeta.getJSONObject(key);
			buf.append(json.getString("column_name").toLowerCase()).append(",");
		} 
		buf.delete(buf.length()-1, buf.length());
		buf.append(" from ").append(tableName);
		return buf; 
	}
	
	/**
	 * 根据主键更新数据
	 * @param tableName
	 * @param params
	 */
	public static void updateByPk(String tableName,JSONObject params)
	{
		updateByPk(tableName,params,new HashSet<String>());
	}
	
	public static void updateByPk(String tableName,JSONObject params,String[] columns)
	{
		Set<String> lsCols = new HashSet<String>();
		if(columns!=null && columns.length>0)
		{
			for(int i=0;i<columns.length;i++)
			{
				lsCols.add(columns[i]);
			}
		}
		updateByPk(tableName,params,lsCols);
	}
	/**
	 * 根据主键更新指定的列
	 * @param tableName
	 * @param params
	 */
	public static void updateByPk(String tableName,JSONObject params,Set<String> columns)
	{
		//更新表名不能为空
		if(StringUtils.isEmpty(tableName))
		{
			ExceptionUtils.throwException("Invalid query parameter tablename: "+tableName);
		}
		//更新参数不能为空
		if(params==null||params.length()==0)
		{
			ExceptionUtils.throwException("Invalid update parameter params: "+params);
		}
		
		//构建update语句
		StringBuffer bufSql = new StringBuffer("update ").append(tableName).append(" set ");
		StringBuffer bufWhere = new StringBuffer(" where ");
		JSONObject colsMeta = DatabaseMetaInfo.getColumnsMeta(tableName);
		//校验数据库表元数据
		if(colsMeta==null)
		{
			ExceptionUtils.throwException("The table '"+tableName+"' is not exists!");
		}
		
		//获取表主键信息
		JSONObject pkMeta = DatabaseMetaInfo.getTablePrimaryKeys(tableName);
		if(pkMeta==null)
		{
			ExceptionUtils.throwException("The table '"+tableName+"' has no PrimaryKey!");
		}
		
		boolean hasCols = true;
		if(columns==null || columns.size()==0)
		{
			hasCols = false;
			columns = columns==null?new HashSet<String>():columns;
		}
		
		//添加默认需要更新的列
		columns.add("modify_time");
		
		
		//校验参数是否包含主键
		List<String> temp = new ArrayList<String>();
		temp.addAll(pkMeta.keySet());
		Iterator<String> it = params.keySet().iterator();
		
		while(it.hasNext())
		{ 
			temp.remove(it.next().toLowerCase());
		}
		if(temp.size()>0)
		{
			ExceptionUtils.throwException("The parameter 'params' miss PrimaryKey column:"+temp.toString());
		}
		
		/**
		 * 修改时默认添加修改时间
		 * */
		if(!params.has("modify_time") || params.getString("modify_time").equals(""))
		{
			params.put("modify_time",DateUtils.getCurDateTime());
		}
		 
		it = params.keys();
		String strKey = null;
		List<Object> colValues = new ArrayList<Object>();
		List<Object> pkValues = new ArrayList<Object>(); 
		
		while(it.hasNext())
		{
			strKey = it.next();
			if(pkMeta.has(strKey.toLowerCase()))
			{
				bufWhere.append(strKey).append("=? and");
				pkValues.add(getCorrespondingParam(params.get(strKey),colsMeta.getJSONObject(strKey.toLowerCase())));
			}else if(colsMeta.has(strKey.toLowerCase()))
			{
				//如果指定了要更新的列
				if(hasCols)
				{
					//如果没有在指定的列，则跳过
					if(!columns.contains(strKey))
					{
						continue;
					}
				}
				bufSql.append(strKey).append("=?,");
				colValues.add(getCorrespondingParam(params.get(strKey),colsMeta.getJSONObject(strKey.toLowerCase())));
			}else
			{
				log.warn("Ignore the attr '"+strKey+"' of parameter 'params' when update table '"+tableName+"'");
			}
		} 
		
		if(colValues.size()==0)
		{
			ExceptionUtils.throwException("Parameter 'params' has no valid column for table '"+tableName+"', params: "+params);
		}
		
		bufSql.delete(bufSql.length()-1, bufSql.length());
		bufWhere.delete(bufWhere.length()-4, bufWhere.length());
		bufSql.append(bufWhere);
		
		colValues.addAll(pkValues);
		
		executeSql(bufSql.toString(),colValues);
	}
	
	public static void insert(String tableName,JSONObject bean)
	{
		
		if(StringUtils.isEmpty(tableName))
		{
			ExceptionUtils.throwException("Invalid  parameter tablename: "+tableName);
		}
		if(bean==null||bean.length()==0)
		{
			ExceptionUtils.throwException("Invalid paramter bean: "+bean);
		}
		
		//设置默认创建时间
		if(!bean.has("create_time") || bean.getString("create_time").equals(""))
		{
			bean.put("create_time", DateUtils.getCurDateTime());
		}
		
		StringBuffer buf = new StringBuffer("insert into ").append(tableName).append("(");
		StringBuffer bufValues = new StringBuffer(" values (");
		JSONObject colsMeta = DatabaseMetaInfo.getColumnsMeta(tableName);
		if(colsMeta==null||colsMeta.length()==0)
		{
			ExceptionUtils.throwException("The table '"+tableName+"' is not exists!");
		}

		List<Object> params = new ArrayList<Object>(); 
		Iterator<String> it = bean.keys();
		String key = null;
		while(it.hasNext())
		{
			key = it.next(); 
			if(key.equalsIgnoreCase("DF"))
			{
				continue;
			}
			if(colsMeta.has(key.toLowerCase()))
			{
				buf.append(key).append(",");
				bufValues.append("?,");
				params.add(getCorrespondingParam(bean.get(key),colsMeta.getJSONObject(key.toLowerCase())));
			}else
			{
				log.warn("Ignore the attr '"+key+"' of parameter 'bean' when insert table "+tableName);
			}
		}
		if(params.size()==0)
		{
			ExceptionUtils.throwException("Paramter bean has no valid filed for table "+tableName+", bean:"+bean);
		}
		buf.delete(buf.length()-1, buf.length());
		buf.append(")");
		
		bufValues.delete(bufValues.length()-1, bufValues.length());
		bufValues.append(")");
		buf.append(bufValues);
		
		executeSql(buf.toString(),params);
	}
	
	private static Object getCorrespondingParam(Object obj,JSONObject colMeta)
	{ 
		if(obj==null)
		{
			return null;
		}
		
		int type = colMeta.getInt("data_type");
		if(type==Types.CHAR||type==Types.VARCHAR||type==Types.LONGVARCHAR)
		{
			if(!(obj instanceof String))
			{
				return obj.toString();
			} 
		}else if(type==Types.NUMERIC||type==Types.DECIMAL)
		{
			if(!(obj instanceof BigDecimal))
			{
				if("".equals(obj.toString().trim()))
				{
					return null;
				}
				return new BigDecimal(obj.toString());
			} 
		}else if(type==Types.BIT)
		{
			if(!(obj instanceof Boolean))
			{
				if("".equals(obj.toString().trim()))
				{
					return null;
				}
				return new Boolean(obj.toString());
			} 
		}else if(type==Types.TINYINT||type==Types.INTEGER||type==Types.SMALLINT)
		{
			if(!(obj instanceof Integer))
			{
				if("".equals(obj.toString().trim()))
				{
					return null;
				}
				
				return Integer.parseInt(obj.toString());
			} 
		}else if(type==Types.BIGINT)
		{
			if(!(obj instanceof Long))
			{
				if("".equals(obj.toString().trim()))
				{
					return null;
				}
				
				return Long.valueOf(obj.toString());
			} 
		}else if(type==Types.REAL)
		{
			if(!(obj instanceof Float))
			{ 
				if("".equals(obj.toString().trim()))
				{
					return null;
				}
				
				return new BigDecimal(obj.toString()).floatValue();
			} 
		}else if(type==Types.FLOAT||type==Types.DOUBLE)
		{
			if(!(obj instanceof Double))
			{
				if("".equals(obj.toString().trim()))
				{
					return null;
				}
				
				return new BigDecimal(obj.toString()).doubleValue();
			} 
		}else if(type==Types.BINARY||type==Types.VARBINARY||type==Types.LONGVARBINARY)
		{
			if(!(obj.getClass().getName().startsWith("[B")))
			{
				if("".equals(obj.toString().trim()))
				{
					return null;
				}
				
				return obj.toString().getBytes();
			} 
		}else if(type==Types.DATE||type==Types.TIME||type==Types.TIMESTAMP)
		{
			if(!(obj instanceof java.util.Date))
			{
				String str = obj.toString();
				if("".equals(str.trim()))
				{
					return null;
				}
				
				if(str.indexOf("-")>0&&str.indexOf(":")>0)
				{
					return java.sql.Timestamp.valueOf(obj.toString());
				}else if(str.indexOf("-")>0)
				{
					return java.sql.Date.valueOf(obj.toString());
				}else if(str.indexOf(":")>0)
				{
					return java.sql.Time.valueOf(obj.toString());
				}
			}
		} 
		return obj;
	}
	
	public static void executeSql(String sql,List<Object> params)
	{
		Connection con = DataSourceUtils.getConnection(SpringContext.getBean("dataSource",DataSource.class));
		PreparedStatement ps = null; 
		try
		{  
			log.debug("ExecuteSQL: "+sql+" ; parameter params:"+params);
			ps = con.prepareStatement(sql); 
			if(params!=null&&params.size()>0)
			{
				for(int i=0;i<params.size();i++)
				{
					ps.setObject(i+1, params.get(i));
				}
			}
			ps.execute();
		}catch(Exception ex){
			ExceptionUtils.throwException(ex);
		}finally
		{ 
			JdbcUtils.closeStatement(ps);
			releaseConnection(con);
		}
	}
	
	public static void executeSql(String sql,Object[] params)
	{
		 List<Object> lsParam = new ArrayList<Object>();
		 for(int i=0;i<params.length;i++)
		 {
			 lsParam.add(params[i]);
		 }
		 executeSql(sql,lsParam);
	}
	
	public static void executeSql(String sql)
	{
		executeSql(sql,new Object[0]);
	}
	
	public  static JSONArray queryByKeysCascadeParent(String tableName,Collection<Object> ids,String parentId)
	{
		return queryByKeysCascadeParent(tableName,ids.toArray(new String[0]),parentId);
	}
	  
	/**
	 * 
	 * @param tableName
	 * @param ids  本次需要加载的记录
	 * @param parentId
	 * @param hasLoad  记录所有递归已经加载的主键记录
	 * @return
	 */
	private  static JSONArray queryByKeysCascadeParent(String tableName,Object[] ids,String parentId,Set<Object> hasLoad)
	{
		JSONArray ret = new JSONArray();
		if(ArrayUtils.isEmpty(ids))
		{
			return ret;
		} 
		ret = queryByKeys(tableName, ids);
		Set<Object> lsParentId = new HashSet<Object>(); 
		Object tParentId = null;
		for(int i=0;i<ret.length();i++)
		{ 
			tParentId = ret.getJSONObject(i).get(parentId); 
			if(hasLoad.contains(tParentId))
			{
				continue;
			}
			lsParentId.add(tParentId); 
		}
		hasLoad.addAll(lsParentId);
		
		if(lsParentId.size()>0)
		{
			JSONArray temp = queryByKeysCascadeParent(tableName,lsParentId.toArray(),parentId,hasLoad);
			for(int i=0;i<temp.length();i++)
			{
				ret.put(temp.get(i));
			} 
		} 
		return ret;
	}
	
	public static JSONArray queryByKeysCascadeParent(String tableName,String[] ids,String parentId)
	{
		 return queryByKeysCascadeParent( tableName, ids, parentId,new HashSet<Object>());
	}
}
