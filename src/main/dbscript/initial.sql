create table ul_bsf_busitype
(
	id varchar2(32) primary key, /**主键*/
	qq9214_md varchar2(20),/**模块？*/
	qq9214_bt varchar2(30),/**业务类型*/
	class_name varchar2(200),/**处理类*/
	view_path varchar2(200),/**界面路径*/
	need_logined varchar2(1)/**是否需要登陆才能访问*/
);

create table ul_bsf_busitype_action
(
	id varchar2(32) primary key,
	busitype_id varchar2(32),
	qq9214_at varchar2(50),
	name varchar2(20),
	method varchar2(50),
	isShow varchar2(1)
);

create table ul_bsf_user
(
	id varchar2(32) primary key,
	user_code varchar(32),
	user_name varchar(32),
	password varchar(64)
);


create table ul_bsf_groups_user
(
        id varchar(32) primary key,
        userid varchar(32) not null, /**关联用户ID*/
        parentid varchar(32) not null, /**上级主键id*/
        ts varchar(19),
        df varchar(1) default 'N'
);

create table ul_bsf_groups_function
(
        id varchar(32) primary key,
        functionid varchar(32) not null,/**关联功能ID*/
        parentid varchar(32) not null,/**上级主键id*/
        ts varchar(19),
        df varchar(1) default 'N'
);

create table ul_bsf_function(
        id varchar(32) primary key
        ,code varchar(20) not null
        ,password varchar(20) not null
        ,name varchar(50) not null
        ,create_time varchar(19) /**创建人*/
        ,creator varchar(32)  /**创建人*/
        ,modify_time varchar(19) /**修改人*/
        ,modifier varchar(32)  /**修改时间*/
        ,df varchar(1) default 'N'
        ,ts varchar(19) 
);

/**参照定义表*/
create table ul_bsf_reference(
        id varchar(32) primary key,
        code varchar(30) not null,
        name varchar(50) not null,
        class_name varchar(200),
        datasource_type varchar(10),
        datasource varchar(1000), 
        meta varchar(1000),
        creator varchar(32),
        create_time varchar(19),
        modifier varchar(32),
        modify_time varchar(19),
        df varchar(1) default 'N',
        ts varchar(19)
 );
 
 /**元数据表*/
 create table ul_bsf_meta
(
    id varchar(32) primary key,
    code varchar(20),
    name varchar(30),
    meta_info varchar(2000),
    create_time varchar(19),
    creator varchar(32),
    modify_time varchar(19),
    ts varchar(19),
    df varchar(1) default 'N'
) ;