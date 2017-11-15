/**
var billTemp = {
	groups : [{id:"metaInfo",name:"元数据信息"},{id:"auditInfo",name:"审计信息"}], 
	fields : [{
				title : "编码",
				field : "code", 
				required:true,
				type : "text"
			},{
				title : "名称",
				field : "name", 
				required:true,
				type : "text"
			},{
				groupId:"metaInfo", 
				title : "元数据信息",
				titlePosition:"top",
				field : "metaInfo", 
				required:true,
				width:"100%",
				height:"360px",
				type : "textarea"
			},{
				title : "主键",
				field : "id",
				hidden:true, 
				type : "hidden" 
			},{
				groupId:"auditInfo",
				title : "创建人",
				field : "creator" ,
				editable:false, 
				type : "ref",
				refType : "user"
			}, {
				groupId:"auditInfo",
				title : "创建时间",
				editable:false,
				field : "createTime",  
				type : "text",
				dataType:"datetime"
			}, {
				groupId:"auditInfo",
				title : "修改人",
				editable:false,
				field : "modifier",  
				type : "ref",
				refType:"user"
	
			},{
				groupId:"auditInfo",
				title : "修改时间",
				editable:false,
				field : "modifyTime", 
				type : "text",
				dataType:"datetime"
			}] 
		};
*/
var queryTemp = {
		fields:[{
			field:"query_code",
			title:"编码",
			type:"text"
		},{
			field:"query_name",
			title:"名称",
			type:"text"
		}]
}