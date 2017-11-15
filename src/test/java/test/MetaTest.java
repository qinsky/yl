package test;

import java.util.Date;

import junit.framework.TestCase;

import org.json.JSONObject;

import com.youlun.baseframework.util.DateUtils;
import com.youlun.meta.MetaBusinessUtils;

public class MetaTest extends TestCase {

	 
	public void test()
	{
		
		Date date = new Date();
		date.setTime(1497283200000l);
		System.out.println(DateUtils.formatDate(date));
		
		/**
		JSONObject metaInfo = new JSONObject("{\"dbInfo\":{\"tableName\":\"ul_bsf_meta\"},\"groups\":[{\"id\":\"metaInfo\",\"name\":\"元数据信息\"},{\"id\":\"auditInfo\",\"name\":\"审计信息\"}],\"fields\":[{\"dbInfo\":{\"column\":\"code\"},\"title\":\"编码\",\"field\":\"code\",\"required\":true,\"type\":\"text\"},{\"dbInfo\":{\"column\":\"name\"},\"title\":\"名称\",\"field\":\"name\",\"required\":true,\"type\":\"text\"},{\"dbInfo\":{\"column\":\"meta_info\"},\"groupId\":\"metaInfo\",\"title\":\"元数据信息\",\"titlePosition\":\"top\",\"field\":\"metaInfo\",\"required\":true,\"width\":\"100%\",\"height\":\"360px\",\"type\":\"textarea\"},{\"dbInfo\":{\"column\":\"id\"},\"title\":\"主键\",\"field\":\"id\",\"hidden\":true,\"type\":\"hidden\"},{\"dbInfo\":{\"column\":\"creator\"},\"groupId\":\"auditInfo\",\"title\":\"创建人\",\"field\":\"creator\",\"editable\":false,\"type\":\"ref\",\"refType\":\"user\"},{\"dbInfo\":{\"column\":\"create_time\"},\"groupId\":\"auditInfo\",\"title\":\"创建时间\",\"editable\":false,\"field\":\"createTime\",\"type\":\"text\",\"dataType\":\"datetime\"},{\"dbInfo\":{\"column\":\"modifier\"},\"groupId\":\"auditInfo\",\"title\":\"修改人\",\"editable\":false,\"field\":\"modifier\",\"type\":\"ref\",\"refType\":\"user\"},{\"dbInfo\":{\"column\":\"modify_time\"},\"groupId\":\"auditInfo\",\"title\":\"修改时间\",\"editable\":false,\"field\":\"modifyTime\",\"type\":\"text\",\"dataType\":\"datetime\"}],\"children\":[{\"dbInfo\":{\"tableName\":\"ul_bsf_busi_action\",\"parentColumn\":\"code\"},\"name\":\"业务动作\",\"code\":\"action\",\"buttons\":[{\"name\":\"添加行\",\"action\":\"addRow\",\"iconCls\":\"icon-edit\"},{\"name\":\"删除行\",\"action\":\"delRow\",\"iconCls\":\"icon-remove\"}],\"fields\":[{\"dbInfo\":{\"column\":\"qq9214_at\"},\"field\":\"qq9214_at\",\"title\":\"动作编码\",\"type\":\"text\",\"width\":\"120px\"},{\"dbInfo\":{\"column\":\"name\"},\"field\":\"name\",\"title\":\"动作名称\",\"type\":\"text\",\"width\":\"120px\"},{\"dbInfo\":{\"column\":\"method\"},\"field\":\"method\",\"title\":\"处理方法\",\"type\":\"text\",\"width\":\"120px\"},{\"dbInfo\":{\"column\":\"isshow\"},\"field\":\"isshow\",\"title\":\"是否显示\",\"type\":\"select\",\"width\":\"120px\",\"options\":[{\"id\":\"Y\",\"text\":\"是\"},{\"id\":\"N\",\"text\":\"否\"}]},{\"dbInfo\":{\"column\":\"id\"},\"field\":\"id\",\"title\":\"主键\",\"type\":\"text\",\"hidden\":true},{\"dbInfo\":{\"column\":\"busitype_id\"},\"field\":\"busitype_id\",\"title\":\"父级主键\",\"type\":\"text\",\"hidden\":true}]}]}");
		JSONObject meta = MetaBusinessUtils.getUIMeta(metaInfo);
		System.out.println(meta.toString(4));
		*/
	}
}
