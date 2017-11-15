var billTemp = {
	fields : [{
			title : "模块",
			field : "qq9214_md",
			required:true,
			type : "text",

		}, {
			title : "业务名称",
			field : "name",
			required:true,
			type : "text",

		}, {
			title : "业务编码",
			field : "qq9214_bt",
			required:true,
			type : "text",

		}, {
			title : "流程编码",
			field : "process_key", 
			type : "text",

		}, {
			title : "主键",
			field : "id", 
			type : "hidden"
		}, {
			title : "元数据",
			field : "meta_id", 
			type : "ref",
			refType : "meta"
		}, {
			title : "上级",
			field : "parent_id",
			type : "ref",
			refType : "busi"
		}, {
			title : "处理类",
			field : "class_name",
			type : "text"
		}, {
			title : "界面路径",
			field : "view_path",
			type : "text" 
		}, {
			title : "登录可用",
			field : "need_logined",
			type : "select",
			options:[{id:"Y",text:"是"},{id:"N",text:"否"}]
		}, {
			title : "类型",
			field : "type",
			required:true,
			type : "select",
			default:2,
			options : [{
					id : 1,
					text : "业务分类目录"
				}, {
					id : 2,
					text : "可执行的业务"
				} 
			]
		}
	],
	children : [{
			name : "业务动作",
			code : "action",
			buttons : [{name:"添加行",action:"addRow",iconCls:"icon-edit"},{name:"删除行",action:"delRow",iconCls:"icon-remove"}],
			fields : [{
					field : "qq9214_at",
					title : "动作编码",
					type : "text",
					width:"120px" 
				},{
					field : "name",
					title : "动作名称",
					type : "text",
					width:"120px" 
				},{
					field : "method",
					title : "处理方法",
					type : "text" ,
					width:"120px" 
				},{
					field : "isshow",
					title : "是否显示",
					type : "select",
					width:"120px",
					options:[{id:"Y",text:"是"},{id:"N",text:"否"}]
				},{
					field : "id",
					title : "主键",
					type : "text",
					hidden:true
				},{
					field : "busitype_id",
					title : "父级主键",
					type : "text",
					hidden:true
				}
			]
		} 
		]
	};