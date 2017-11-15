package com;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.youlun.baseframework.core.SpringContext;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.ExceptionUtils;

public class Test {

	public static void insertRow()
	{ 
		try { 
		String sql = "insert into ttt(rownums,name,column1,column2,column3,column4,column5,column6,column7,column8,column9,column10) " +
				"values(ttt_seq.nextval,?,?,?,?,?,?,?,?,?,?,?)";
		Connection con = DataSourceUtils.getConnection(SpringContext.getBean("dataSource",DataSource.class));
		PreparedStatement ps = con.prepareStatement(sql);
		 
		int i = 1;
		for( ;i<=1000000;i++)
		{ 
			ps.setObject(1, "name"+i);
		 
			for(int j=1;j<=10;j++)
			{ 
				ps.setObject(j+1,"column_"+j);
			} 
			ps.addBatch();
			 
			if(i%1000==0)
			{ 
				ps.executeBatch();
			} 
		}
			if(i%1000!=0)
			{
				ps.executeBatch();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			ExceptionUtils.throwException(e);
		}
		
	}
	static
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public static void main(String[] args)
	{
		Connection con = null;
		try
		{  
//			System.out.println(oracle.jdbc.OracleTypes.CURSOR);
//			System.out.println(java.sql.Types.REF_CURSOR);
//			if(true)
//			{
//				return;
//			}
			
			String url = "jdbc:oracle:thin:@192.168.4.74:1521:zhifubao";
			String userName = "water_fct";
			String password = "water_fct";
			con = DriverManager.getConnection(url, userName,password);
			CallableStatement  ps = con.prepareCall("{call alipay_zq.QueryQianFei(?,?,?,?,?,?,?,?,?,?)}");
			ps.setObject(1,"36476");
			ps.setObject(2,"0");
			ps.setObject(3,"001200032");
			ps.setObject(4,"");
			ps.setObject(5,""); 
			ps.setObject(6,"");
			ps.setObject(7,"");
			ps.setObject(8,"");
		 
			ps.registerOutParameter(9, oracle.jdbc.OracleTypes.CURSOR);
			ps.registerOutParameter(10, Types.ARRAY);
			ps.execute();
			
			ResultSet rs = null;
			rs = (ResultSet) ps.getObject(9);
			
			JSONArray array  = null;
			array = DBOperator.processRs(rs);
			System.out.println(array);
			rs.close();
			
			rs = (ResultSet) ps.getObject(10);
			if(rs!=null)
			{
				array = DBOperator.processRs(rs); 
				System.out.println(array);
			}
		 
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
//		36476/0/001200032
		//System.out.println("Ok......");
	}
	
	public static void getPaginationRow()
	{
		int pageNumber =  1;
		int pageSize  = 10;
		//1111510
		long cur = System.currentTimeMillis();
		try { 
			String sql = "select * from ttt where rownum < 999999 ";
			Connection con = DataSourceUtils.getConnection(SpringContext.getBean("dataSource",DataSource.class));
			PreparedStatement ps = con.prepareStatement(sql);
			 
			ResultSet rs = ps.executeQuery();
			JSONArray arr = DBOperator.processRsPagination(rs, pageNumber, pageSize);
			System.out.println(arr.length());
			System.out.println(arr);
			rs.close(); 
			ps.close();
			con.close();
			System.out.println("鑰楁椂锛�"+(System.currentTimeMillis()-cur)/1000);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				ExceptionUtils.throwException(e);
			}
	}
}
