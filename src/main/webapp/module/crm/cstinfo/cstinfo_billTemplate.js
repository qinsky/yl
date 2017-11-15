var billTemp = {
	groups : [{
			id : "auditInfo",
			name : "审计信息"
		}
	],
	fields : [{
			title : "主键",
			field : "id",
			hidden : true,
			type : "hidden"

		}, {
			title : "名称",
			field : "name",
			required : true,
			type : "text"
		}, {
			title : "网址",
			field : "www",
			required : true,
			type : "text"
		},{
			title : "地址",
			field : "address", 
			type : "text" 
		},{
			title : "客户类型",
			field : "csttype_id", 
			type : "ref" ,
			refType:"csttype"
		},{
			title : "联系人",
			field : "linkman", 
			type : "text" ,
			required:true
		},{
			title : "联系电话",
			field : "linkphone", 
			type : "text" 
		},{
			title : "联系时间",
			field : "linktime", 
			type : "text",
			dataType:"datetime",
			editable:false
		},{
			title : "下次跟进时间",
			field : "next_time", 
			type : "text",
			dataType:"datetime"			
		},{
			title : "拥有者",
			field : "owner_id", 
			editable:false,
			type : "ref" ,
			refType:"user"
		},{
			title : "拥有时间",
			field : "owner_time", 
			type : "text",
			dataType:"datetime",
			editable:false
		},{
			title : "签约时间",
			field : "sign_time", 
			type : "text",
			dataType:"datetime",
			editable:false
		}, {
			groupId : "auditInfo",
			title : "创建人",
			field : "creator",
			editable : false,
			type : "ref",
			refType : "user"
		}, {
			groupId : "auditInfo",
			title : "创建时间",
			editable : false,
			field : "create_time",
			type : "text",
			dataType : "datetime"
		}, {
			groupId : "auditInfo",
			title : "修改人",
			editable : false,
			field : "modifier",
			type : "ref",
			refType : "user"
		}, {
			groupId : "auditInfo",
			title : "修改时间",
			editable : false,
			field : "modify_time",
			type : "text",
			dataType : "datetime"
		}
	],
	children : [{
		name : "跟进记录",
		code : "trace",
		buttons : [{name:"添加跟进记录",action:"addTrace",iconCls:"icon-edit",isList:true}],
		fields : [{
				field : "id",
				title : "主键",
				type : "text",
				hidden : true
			},{
				field : "trace_user_id",
				title : "跟进人员",
				type : "ref",
				width : "80px",
				refType : "user"
			},{
				field : "master_id",
				title : "客户主键",
				type : "text",
				hidden : true
			},{
				field : "content",
				title : "跟进内容",
				type : "textarea" 
			},{
				field : "trace_time",
				title : "跟进时间",
				type : "text",
				dateType:"datetime"
			},{
				field : "create_time",
				title : "创建时间",
				type : "text",
				dateType:"datetime"
			}
		]
	}
]
};

var queryTemp = {
		fields:[{
			field:"query_www",
			title:"网址",
			type:"text"
		},{
			field:"query_name",
			title:"名称",
			type:"text"
		}]
}