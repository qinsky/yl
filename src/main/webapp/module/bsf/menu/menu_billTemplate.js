var billTemp = {
	groups:[{id:"auditInfo",name:"审计信息"}],
	fields : [{
			title : "名称",
			field : "name",
			required:true,
			type : "text",

		}, {
			title : "编码",
			field : "code",
			required:true,
			type : "text",

		}, {
			title : "主键",
			field : "id", 
			type : "hidden",

		}, {
			title : "类型",
			field : "type",
			required:true,
			type : "select",
		default:
			2,
			options : [{
					id : 1,
					text : "功能分类目录"
				}, {
					id : 2,
					text : "可执行功能"
				} 
			]
		},{
			title : "关联业务",
			field : "busitype_id",
			type : "ref",
			refType : "busi"
		},{
			title : "上级",
			field : "parent_id",
			type : "ref",
			editable:true,
			refType : "menu"
		}, {
			title : "创建人",
			groupId:"auditInfo",
			field : "creator",
			type : "ref",
			editable:false,
			refType : "user"
		}, {
			title : "创建时间",
			groupId:"auditInfo",
			field : "create_time",
			type : "text",
			dataType:"datatime",
			editable:false
		}, {
			title : "修改人",
			groupId:"auditInfo",
			field : "modifier",
			type : "ref",
			editable:false,
			refType : "user"
		}, {
			title : "修改时间",
			groupId:"auditInfo",
			field : "modify_time",
			type : "text",
			dataType:"datatime",
			editable:false 
		}
	]
	};