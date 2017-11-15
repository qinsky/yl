/**
 * 
 */
package com.youlun.baseframework.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.baseframework.config.BusinessConfig;
import com.youlun.baseframework.core.AbstractBusinessHandler;
import com.youlun.baseframework.core.RequestContext;
import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.dao.DatabaseMetaInfo;
import com.youlun.baseframework.util.ArrayUtils;
import com.youlun.baseframework.util.DateUtils;
import com.youlun.baseframework.util.ExceptionUtils;
import com.youlun.baseframework.util.IDUtils;
import com.youlun.baseframework.util.JSONUtils;
import com.youlun.baseframework.util.StringUtils;
import com.youlun.bsf.defaults.DefaultBillUtils;
import com.youlun.bsf.process.util.ProcessUtils;
import com.youlun.meta.MetaBusinessUtils;

/**
 * @author qinxl
 * @Date   2017-08-17O
 */
@Component("defaultHandler")
@Scope("prototype")
public class DefaultBusinessHandler extends AbstractBusinessHandler {
	
	/**
	 * 执行完后调用  
	 * 如果流程结束了，则根据审批结果更新流程状态
	 */
	public void endProcessNotify(DelegateExecution execution) {
		// TODO Auto-generated method stub
	 
		//获取流程关联的业务编码
		String busiType = execution.getProcessInstanceBusinessKey(); 
		if(StringUtils.isEmpty(busiType))
		{
			return;
		}
	 
		//通过业务编码获取到业务对象
		JSONObject busiBean = BusinessConfig.getBusitype(busiType);
		//根据业务对象获取关联的元数据
		String metaId = busiBean.getString("meta_id");
		if(StringUtils.isEmpty(metaId))
		{
			return;
		}
		//根据元数据ID获取元数据对象
		JSONObject meta = MetaBusinessUtils.getDBMeta(metaId);
		if(meta==null)
		{
			return;
		}
		
		//查询流程实例变量 
		String aproveStatus = (String)execution.getVariable("approveStatus");
		String billId = (String)execution.getVariable("billId");
				
		//更新审批状态为完成
		JSONObject bean = new JSONObject();
		 
		if("N".equals(aproveStatus))
		{
			bean.put("process_status", "failure"); 
		}else
		{
			bean.put("process_status", "success"); 
		} 
		
		bean.put("id", billId);
		bean.put("modifier", RunContext.getInstance().getUserId());
		DBOperator.updateByPk(meta.getString("tableName"),bean);
	}
	
	/**
	 * 启动流程
	 */
	public void startprocess()
	{
		JSONObject billData = params.getJSONObject("busiData");  
		String busiType = (String) RequestContext.getAttribute("qq9214Bt");
		
		JSONObject meta = this.getDbMeta();
		String tableName = meta.getString("tableName");
		JSONObject bean = new JSONObject();
		bean = DBOperator.queryByPrimaryKey(tableName, billData.get("id"));
		
		//校验是否可以启动流程
		if(!"no".equals(bean.getString("process_status")))
		{
			error("选中记录流程状态不正确,无法启动!");
			return;
		}
		
		ProcessUtils.startProc(busiType, billData.getString("id"), getVariables());
		  
		//更新单据状态为流程中
		JSONUtils.clearJson(bean);
		
		bean.put("process_status", "processing"); 
		bean.put("id", billData.get("id"));
		bean.put("modifier", RunContext.getInstance().getUserId()); 
		
		DBOperator.updateByPk(tableName, bean);
		
		//查询最新的数据，返回给请求端 
		bean = DBOperator.queryByPrimaryKey(tableName, billData.get("id"));
		DefaultBillUtils.dbToUi(meta, bean);
		success(bean);
	}
	
	public Map<String,Object> getVariables()
	{ 
		Map<String,Object> map = new HashMap<String,Object>();
		//将菜单名称放到流程变量里，方便在显示任务的时候显示任务关联的单据名称
		if(params.has("menuId"))
		{
			map.put("menuId", params.get("menuId"));
			map.put("menuName", params.get("menuName"));
		}
		
		//允许跳过流程
		map.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);
		return map;
	}
	
	public boolean beforeSave(JSONObject bean){return true;};
	public void save()
	{
		JSONObject billData = params.getJSONObject("busiData");  
		JSONObject meta = getDbMeta();
		
		//设置默认参数
		billData.put("id", IDUtils.getID());
		//最后修改时间错s
//		billData.put("ts",DateUtils.getCurDateTime()); 
		//busiData置创建人
		billData.put("creator", RunContext.getInstance().getUserId());
		//设置创建时间
//		billData.put("createTime", billData.get("ts"));
		
		//保存前处理
		if(!beforeSave(billData))
		{
			return;
		}
		
		//获取表名
		String tableName = (String) meta.get("tableName");
		//获取定义的表头字段 
		JSONObject saveData = DefaultBillUtils.uiToDb(meta,billData); 
			
		//插入表头数据库
		DBOperator.insert(tableName, saveData); 
		
		//处理表体数据
		saveChild(billData,meta);
		
		//查询刚插入的数据返回给客户端
		JSONObject bill = this.queryBill(billData.getString("id"), meta);
		
		//保存后处理
		if(!afterSave(bill))
		{
			return;
		}
		
		success(bill);
	} 
	public boolean afterSave(JSONObject bean){
		return true;
	};
	
	//根据表体元数据编码，获取对应的表体数据
	public JSONObject getChildDataByMeta(JSONArray childrenData,String uiCode)
	{
		
		JSONObject child = null;
		for(int i=0;i<childrenData.length();i++)
		{
			child = childrenData.getJSONObject(i);
			if(uiCode.equalsIgnoreCase(child.getString("code")))
			{
				return child;
			}
		}
		return null;
	}
	
	//处理表体数据
	private void saveChild(JSONObject billData,JSONObject meta)
	{
		//处理表体
		if(!meta.has("children")) 
		{
			return;
		}
		
		//获取表体元数据
		JSONArray childrenMeta = meta.getJSONArray("children");
		JSONObject childMeta = null;
		
		//获取表体数据
		JSONArray childrenData = billData.getJSONArray("children");
		JSONObject childData = null;
		
		//临时变量
		JSONArray fields = null;
		JSONObject field = null;
		String tCode = null;
		String tDbCode = null;
		
		//遍历表体元数据，根据元数据操作表体数据
		for(int i=0;i<childrenMeta.length();i++)
		{
			//获取单个表体元数据
			childMeta = childrenMeta.getJSONObject(i);
			//根据元数据获取到对应的需要操作的表体数据
			childData = getChildDataByMeta(childrenData,childMeta.getString("uiCode"));
			//如果有表体元数据，并且是必须的，但是没有数据，则抛出异常
			if(childData==null && "N".equalsIgnoreCase(childMeta.getString("isEmpty")))
			{
				ExceptionUtils.throwBusinessException("表体("+childMeta.getString("name")+"),不能为空!");
			}
			//获取表体的表名
			String childTblName = childMeta.getString("tableName");
			JSONArray rows = childData.getJSONArray("data");
			JSONObject row = null;
			
			//保存的字段以元数据定义的字段为准，并将转换映射的数据库字段编码
			JSONObject saveRow = new JSONObject();
			for(int j=0;j<rows.length();j++)
			{
				row = rows.getJSONObject(j);
				saveRow =  new JSONObject(); 
				//获取表体字段元数据
				fields = childMeta.getJSONArray("columns");
				for(int n=0;n<fields.length();n++)
				{
					field = fields.getJSONObject(n);
					//界面显示的字段编码
					tCode = field.getString("uiField");
					//映射的数据库字段编码
					tDbCode = field.getString("column");
					if(row.has(tCode))
					{
						saveRow.put(tDbCode, billData.get(tCode));
					}
				}
				
				if("updated".equals(row.getString("rowStatus")))
				{
					//设置修改时间戳
					saveRow.put("ts",DateUtils.getCurDateTime());
					//设置修改人
					saveRow.put("modifier",RunContext.getInstance().getUserId());
					saveRow.put("modify_time",saveRow.get("ts"));
					DBOperator.updateByPk(childTblName, saveRow); 
				}else if("inserted".equals(row.getString("rowStatus")))
				{
					//设置表体主键
					saveRow.put("id", IDUtils.getID());
					//设置主表主键值
					saveRow.put(childMeta.getString("parentColumn"), billData.get("id"));
					saveRow.put("ts",DateUtils.getCurDateTime()); 
					saveRow.put("creator", RunContext.getInstance().getUserId());
					saveRow.put("create_time", saveRow.get("ts"));
					DBOperator.insert(childTblName, saveRow);
				}else if("deleted".equals(row.getString("rowStatus")))
				{  
					List<Object> lsParam = new ArrayList<Object>();
					lsParam.add(DateUtils.getCurDateTime());
					
					//构建删除语句，如果修改人字段，修改人字段
					StringBuffer sql = new StringBuffer("update "+childTblName+" set df='Y',ts = ? ");
					if(hasColumn(childMeta,"modifier"))
					{
						sql.append(",modifier=? ");
						lsParam.add(RunContext.getInstance().getUserId());
					}
					if(hasColumn(childMeta,"modify_time"))
					{
						sql.append(",modify_time=? ");
						lsParam.add(lsParam.get(0));
					}
					lsParam.add(saveRow.get("id")); 
					sql.append(" where id = ?");
					DBOperator.executeSql(sql.toString(), lsParam);
				}
			} 
		} 
	}
	
	private boolean hasColumn(JSONObject meta,String colName)
	{
		if(!meta.has("columns"))
		{
			return false;
		}
		JSONArray cols = meta.getJSONArray("columns");
		JSONObject col = null;
		for(int i=0;i<cols.length();i++)
		{
			col = cols.getJSONObject(i);
			if(colName.equalsIgnoreCase(col.getString("column")))
			{
				return true;
			}
		}
		return false;
	}
	
	//删除数据
	public void delete()
	{ 
		JSONObject billData = params.getJSONObject("busiData");   
		JSONObject meta = getDbMeta();
		
		//检查是否流程中单据，如果是不能修改
		if(!checkProcess(meta,billData.get("id")))
		{
			success("流程单据不能删除!");
			return;
		}
				
		String tableName = (String) meta.get("tableName");
		//
		StringBuffer sql = new StringBuffer("update "+tableName+" set df='Y',ts=? ");
		
		List<Object> lsParams = new ArrayList<Object>();
		String curDate = DateUtils.getCurDateTime();
		lsParams.add(curDate);
		if(hasColumn(meta,"modifier"))
		{
			sql.append(",modifier=? ");
			lsParams.add(RunContext.getInstance().getUserId());
		}
		if(hasColumn(meta,"modify_time"))
		{
			sql.append(",modify_time=? ");
			lsParams.add(curDate);
		}
		sql.append(" where id=?");
		lsParams.add(billData.get("id"));
		DBOperator.executeSql(sql.toString(), lsParams); 
		
		//删除表体 
		if(meta.has("children")) 
		{
			//获取表体元数据
			JSONArray childrenMeta = meta.getJSONArray("children");
			JSONObject childMeta = null;
			//遍历表体元数据，根据元数据操作表体数据
			for(int i=0;i<childrenMeta.length();i++)
			{
				sql.delete(0, sql.length());
				lsParams.clear();
				
				//获取单个表体元数据
				childMeta = childrenMeta.getJSONObject(i);
				String childTblName = childMeta.getString("tableName");
				String parentId = childMeta.getString("parentColumn");
				// 
				sql.append("update "+childTblName+" set df='Y',ts=? ");
				lsParams.add(DateUtils.getCurDateTime());
				if(hasColumn(childMeta,"modifier"))
				{
					sql.append(",modifier=? ");
					lsParams.add(RunContext.getInstance().getUserId());
				}
				if(hasColumn(childMeta,"modify_time"))
				{
					sql.append(",modify_time=? ");
					lsParams.add(lsParams.get(0));
				}
				sql.append(" where "+parentId+"=?");
				lsParams.add(billData.get("id"));
				DBOperator.executeSql(sql.toString(), lsParams); 
			} 
		} 
		
		success(billData); 
	}
	
	private JSONObject getDbMeta()
	{
		Object meta =   RequestContext.getAttribute("curMeta");
		if(meta==null)
		{
			JSONObject obj = (JSONObject) RequestContext.getAttribute("busiObj");
			String metaID = obj.getString("meta_id");
			meta = MetaBusinessUtils.getDBMeta(metaID);
			RequestContext.setAttribute("curMeta", meta);
		}
		return (JSONObject) meta;
	}
	
	private JSONObject getUiMeta()
	{
		JSONObject obj = (JSONObject) RequestContext.getAttribute("busiObj");
		String metaID = obj.getString("meta_id");
		return MetaBusinessUtils.getUIMeta(metaID); 
	}
	
	public boolean beforeUpdate(JSONObject bean){
		return true;
	};
	
	public boolean checkProcess(JSONObject meta,Object id)
	{
		JSONObject colsMeta = DatabaseMetaInfo.getColumnsMeta(meta.getString("tableName"));
		if(colsMeta.has("process_status"))
		{
			JSONObject bean = DBOperator.queryColumnsByPrimaryKey(meta.getString("tableName"), id, new String[]{"process_status"});		
			if(!JSONUtils.equals(bean, "process_status", "no"))
			{ 
				return false;
			}
		}
		return true;
	}
	
	//修改数据
	public void update()
	{ 
		JSONObject billData = params.getJSONObject("busiData");  
		JSONObject meta = getDbMeta();
		
		//检查是否流程中单据，如果是不能修改
		if(!checkProcess(meta,billData.get("id")))
		{
			success("流程中单据不能修改!");
			return;
		}
		//最后修改时间错
//		billData.put("ts",DateUtils.getCurDateTime()); 
		//busiData修改人
		billData.put("modifier", RunContext.getInstance().getUserId()); 
		//busiData 修改时间
//		billData.put("modifyTime", billData.get("ts"));
		
		if(!beforeUpdate(billData))
		{
			return;
		}
		
		//获取表名
		String tableName = (String) meta.get("tableName");
		//获取定义的表头字段
		JSONArray fields = meta.getJSONArray("columns");
		JSONObject field = null;
		JSONObject saveData = new JSONObject();
		String tCode = null;
		String tDbCode = null;
		for(int i=0;i<fields.length();i++)
		{
			field = fields.getJSONObject(i);
			//界面显示的字段编码
			tCode = field.getString("uiField");
			//映射的数据库字段编码
			tDbCode = field.getString("column");
			if(billData.has(tCode))
			{
				saveData.put(tDbCode, billData.get(tCode));
			}
		}
			
		//插入表头数据库
		DBOperator.updateByPk(tableName, saveData); 
		
		//处理表体数据
		saveChild(billData,meta);
 
		//查询最新的数据返回给客户端
 		JSONObject ret = queryBill(billData.getString("id"),meta);
 		
 		//保存后处理
 		if(!afterUpdate(ret))
 		{
 			return;
 		}
 	
 		success(ret);    
	}
	
	public boolean afterUpdate(JSONObject bean){
		
		return true;
	};
	
	public void query()
	{  
		StringBuffer where = new StringBuffer();
		List<Object> queryParams = new ArrayList<Object>();
		if(params.has("busiData"))
		{
			JSONObject busiData = params.getJSONObject("busiData");
			if(busiData.length()>0)
			{
				Iterator<String> it = busiData.keys();
				String strKey = null;
				while(it.hasNext())
				{
					strKey = it.next();
					where.append(" and ").append(strKey).append(" like ? ");
					queryParams.add("%"+busiData.get(strKey)+"%");
				} 
			}
		} 
		  
		JSONObject meta = getDbMeta();
		
		JSONArray beans = DBOperator.queryByWhere(meta.getString("tableName"), where.toString(), queryParams);
		 
		//将json数据的key,由数据库字段转换成ui字段,以便展示
		DefaultBillUtils.dbToUi(meta,beans);
		
		beans = queryChildrenData(beans, meta);
		
		success(beans); 
	}
	
	private String generalQidSQL(JSONObject meta,JSONObject busiData,List<Object> queryParams)
	{
		String tableName = meta.getString("tableName");
		StringBuffer buf = new StringBuffer("select id from ").append(tableName);
		
		StringBuffer where = new StringBuffer(" where ").append(tableName).append(".df!='Y'");
		JSONObject uiMeta = getUiMeta();
		if(busiData.has("fields"))
		{
			JSONArray fields = busiData.getJSONArray("fields");
			JSONObject field = null;
			JSONArray columns = meta.getJSONArray("columns");
			JSONArray fieldsUiMeta = uiMeta.getJSONArray("fields");
			JSONObject column = null;
			JSONObject fieldUi = null;
			
			for(int i=0;i<fields.length();i++)
			{
				field = fields.getJSONObject(i);
				column = getFieldByName(columns,field.getString("field"),"uiField");
				fieldUi = getFieldByName(fieldsUiMeta,field.getString("field"),"field");
				
				
				String cdType = getCdType(field,fieldUi.getJSONObject("qInfo"));
				if(cdType.equals("like")){ 
					where.append(" and ").append(tableName).append(".").append(column.getString("column"));
					where.append(" like ? ");
					queryParams.add("%"+field.get("value")+"%"); 
				}else if(cdType.equals("><"))
				{
					//介于的条件要单独处理
					if(field.has("value"))
					{
						where.append(" and ").append(tableName).append(".").append(column.getString("column"));
						where.append(">=").append("?");
						queryParams.add(field.get("value"));
					}
					if(field.has("value2"))
					{
						where.append(" and ").append(tableName).append(".").append(column.getString("column"));
						where.append("<=").append("?");
						queryParams.add(field.get("value2"));
					}
				}else if(!cdType.equals("><")) 
				{
					//非介于条件处理
					where.append(" and ").append(tableName).append(".").append(column.getString("column"));
					where.append(cdType).append("?");
					queryParams.add(field.get("value"));
				}
			}
		}
		
		if(busiData.has("children"))
		{
			JSONArray children = busiData.getJSONArray("children");
			JSONObject child = null;
			for(int i=0;i<children.length();i++)
			{
				child = children.getJSONObject(i);
				
			}
		} 
		
		buf.append(where);
		
		return buf.toString();
	}
	
	public JSONObject getFieldByName(JSONArray fields,String fieldName,String compareField)
	{
		JSONObject field = null;
		for(int i=0;i<fields.length();i++)
		{
			field = fields.getJSONObject(i);
			if(field.getString(compareField).equals(fieldName))
			{
				return field;
			}
		}
		return null;
	}  
	public String getCdType(JSONObject field,JSONObject qInfo)
	{
		JSONArray  cdTypeArray = qInfo.getJSONArray("cdType");
		JSONObject cdType = null;
		for(int i=0;i<cdTypeArray.length();i++)
		{
			cdType = cdTypeArray.getJSONObject(i);
			if(cdType.getString("symbol").equals(field.getString("cdType")))
			{
				return cdType.getString("symbol");
			}
		}
		
		ExceptionUtils.throwBusinessException("无效的条件操作符号:"+field.getString("cdType"));
		 
		return null;
	} 
	
	protected boolean beforeQids(){
		return true;
	};
	
	public void qids()
	{ 
		JSONObject meta = getDbMeta();  
		if(meta == null)
		{
			ExceptionUtils.throwBusinessException("没有获取到元数据!");
		} 
		List<Object> queryParams = new ArrayList<Object>();
		JSONArray ids = new JSONArray();
		JSONArray tids = null;
		if(params.has("busiData"))
		{
			String  where = generalQidSQL(meta, params.getJSONObject("busiData"), queryParams); 
			tids = DBOperator.query(where.toString(), queryParams);
		}   
		
		if(tids!=null)
		{
			for(int i=0;i<tids.length();i++)
			{
				ids.put(tids.getJSONObject(i).get("id"));
			} 
		} 
		
		success(ids); 
	}
	protected boolean afterQids(){
		return true;
	};
	
	public void qdatabids()
	{   
		JSONObject meta = getDbMeta();  
	 
		JSONObject busiData = params.getJSONObject("busiData");
		
		JSONArray ids = busiData.getJSONArray("ids");
		
		JSONArray beans = this.queryBills(ids, meta);
		
		success(beans); 
	} 
	
	
	/**
	 * 根据主键、元数据查询单据
	 * @param id
	 * @param meta
	 * @return
	 */
	protected JSONObject queryBill(String id,JSONObject meta)
	{ 
		if(StringUtils.isEmpty(id))
		{
			return null;
		}
		JSONArray bills = queryBills(new String[]{id},meta);
		if(bills.length()>0)
		{
			return bills.getJSONObject(0);
		}
		return null;
	}
	
	/**
	 * 根据主键、元数据 查询单据
	 * @param ids
	 * @param meta
	 * @return
	 */
	protected JSONArray queryBills(String[] ids,JSONObject meta)
	{ 
		if(ArrayUtils.isEmpty(ids))
		{
			return null;
		}
		JSONArray array = new JSONArray();
		for(int i=0;i<ids.length;i++)
		{
			array.put(ids[i]);
		}
		return queryBills(array,meta);
	}
	
	/**
	 * 根据主键、元数据查询单据
	 * @param ids
	 * @param meta
	 * @return
	 */
	protected  JSONArray queryBills(JSONArray ids,JSONObject meta)
	{  
		//获取表名
		String tableName = (String) meta.get("tableName");   
		JSONArray beans = DBOperator.queryByKeys(tableName,ids);  
		 

		//将json数据的key,由数据库字段转换成ui字段,以便展示
		DefaultBillUtils.dbToUi(meta,beans);
		
		queryChildrenData(beans,meta,ids);
		
		return beans;
	}
	
	
	
	/**
	 * 根据主表以及元数据信息查询子表数据
	 * @param data
	 * @param meta
	 * @return
	 */
	protected JSONArray queryChildrenData(JSONArray data,JSONObject meta)
	{
		JSONArray ids = new JSONArray();
		for(int i=0;i<data.length();i++)
		{
			ids.put(data.getJSONObject(i).get("id"));
		}
		return queryChildrenData(data,meta,ids);
	}
	
	/**
	 * 根据主表以及元数据信息查询子表数据
	 * @param data
	 * @param meta
	 * @return
	 */
	protected JSONArray queryChildrenData(JSONArray data,JSONObject meta,JSONArray ids)
	{
		//处理表体
		if(!meta.has("children")) 
		{
			return data;
		}
		
		//获取表体元数据
		JSONArray childrenMeta = meta.getJSONArray("children");
		JSONObject childMeta = null;
		StringBuffer where = new StringBuffer();
		String parentCode = null;
		String tableName = null;
		JSONArray rows = null;
		JSONObject row = null;
		JSONObject tData = null;
		JSONArray childData = null;
		JSONObject child = null;
		
		JSONArray columns;
		JSONObject column = null;
		
		for(int i=0;i<childrenMeta.length();i++)
		{
			where.delete(0, where.length());
			childMeta = childrenMeta.getJSONObject(i);
			//获取表体对应的表名
			tableName = childMeta.getString("tableName");
			//获取表体关联主表的字段
			parentCode = childMeta.getString("parentColumn");
			//构建查询条件，为了提高效率，每次把同一张子表的关联所有主表的数据都查询出来
			where.append(" and ").append(parentCode).append(" in(");
			for(int j=0;j<ids.length();j++)
			{
				where.append("?,");
			}
			where.delete(where.length()-1, where.length());
			where.append(")");
			rows = DBOperator.queryByWhere(tableName,where.toString(),ids);
			 
			//将查出的所有的子表数据，拆分到对应的主表中
			for(int j=0;j<data.length();j++)
			{
				tData = data.getJSONObject(j);
				for(int n=0;n<rows.length();n++)
				{
					row = rows.getJSONObject(n);
					
					//将json数据的key,由数据库字段转换成ui字段,以便展示
					columns = childMeta.getJSONArray("columns");  
					for(int m=0;j<childrenMeta.length();m++)
					{
						column = columns.getJSONObject(m);
						if(row.has(column.getString("column")))
						{ 
							row.put(column.getString("uiField"), row.remove(column.getString("column")));
						} 
					} 
					
					//将相同父主键分组
					childData = new JSONArray();
					if(tData.getString("id").equals(row.getString(parentCode)))
					{
						childData.put(row);
					}
				}
				
				if(!tData.has("children"))
				{
					tData.put("children", new JSONArray());
				}
				
				//将表体数据添加到主表的对象里面
				child = new JSONObject();
				child.put("code", childMeta.get("uiCode"));
				child.put("data", childData);
				tData.getJSONArray("children").put(child);
			}
		}
		
		return data;
		
	} 
}
