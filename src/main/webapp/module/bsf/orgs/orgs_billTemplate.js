var billTemp = {
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
			default:111,
			type : "hidden",

		}, {
			title : "上级",
			field : "parent_id",
			type : "ref",
			refType : "orgs"
		}, {
			title : "类型",
			field : "type",
			required:true,
			type : "select",
		default:
			2,
			options : [{
					id : 1,
					text : "集团"
				}, {
					id : 2,
					text : "公司"
				},{
					id : 3,
					text : "部门"
				},{
					id : 6,
					text : "小组"
				},{
					id : 5,
					text : "岗位"
				}
			]
		}
	],
	children : [{
			name : "关联用户",
			code : "user",
			buttons : [{name:"添加行",action:"addRow",iconCls:"icon-edit"},{name:"删除行",action:"delRow",iconCls:"icon-remove"}],
			fields : [{
					field : "user_id",
					title : "用户",
					type : "ref",
					width:"80px",
					refType:"user"
				},{
					field : "is_key_post",
					title : "是否主要岗位",
					type : "select",
					default:"Y",
					options:[{id:"Y",text:"是"},{id:"N",text:"否"}],
					width:"100px"
				},{
					field : "is_admin",
					title : "是否负责人",
					type : "select",
					default:"N",
					options:[{id:"N",text:"否"},{id:"Y",text:"是"}],
					width:"80px"
				},{
					field : "id",
					title : "主键",
					type : "text",
					hidden:true
				},{
					field : "master_id",
					title : "父级主键",
					type : "text",
					hidden:true
				}
			]
		},
		{
				name : "关联菜单",
				code : "menu",
				buttons : [{name:"分配菜单",action:"allocate",iconCls:"icon-edit"},{name:"查看按钮权限",action:"authority"}],
				fields : [{
						title : "菜单",
						field : "text",
						type : "text",
						width:"100%"
					}
				]
			},
		{
				name : "管理组织",
				code : "orgs",
				editable:false,
				buttons : [{name:"分配组织",action:"alloOrgs",iconCls:"icon-edit"}],
				fields : [{
						title : "组织",
						field : "org_id",
						type : "ref",
						refType:"orgs",
						width:"100%"
					}
				]
			}
		]
	};