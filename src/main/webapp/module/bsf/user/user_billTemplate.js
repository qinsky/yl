var billTemp = {
	groups : [{id:"auditInfo",name:"审计信息"}],
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
				title : "性别",
				field : "sex", 
				required:true,
				type : "select",
				default:"m",
				options:[{id:"m",text:"男"},{id:"f",text:"女"}]
			},  {
			title : "主键",
			field : "id",
			hidden:true, 
			type : "hidden"

		}, {
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
			field : "create_time",  
			type : "text",
			dataType:"datetime"
		}, {
			groupId:"auditInfo",
			title : "修改人",
			editable:false,
			field : "modifier",  
			type : "ref",
			refType:"user"

		}, {
			groupId:"auditInfo",
			title : "修改时间",
			editable:false,
			field : "modify_time", 
			type : "text",
			dataType:"datetime"
		}]
		
/**
		,
	   children : [{
			name : "关联用户",
			code : "user",
			fields : [{
					field : "userid",
					title : "用户",
					type : "ref",
					width:"80px",
					refType:"user"
				},{
					field : "id",
					title : "主键",
					type : "text",
					hidden:true
				},{
					field : "parentid",
					title : "父级主键",
					type : "text",
					hidden:true
				}
			]
		},
		{
				name : "关联功能",
				code : "function",
				fields : [{
						title : "功能",
						field : "functionid",
						type : "ref",
						width:"80px",
						refType: "function"
					},{
						field : "id",
						title : "主键",
						type : "text",
						hidden:true
					},{
						field : "parentid",
						title : "父级主键",
						type : "text",
						hidden:true
					}
				]
			}
		] */
	};

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