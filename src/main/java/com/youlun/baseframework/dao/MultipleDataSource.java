package com.youlun.baseframework.dao;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import javax.sql.DataSource;

public class MultipleDataSource implements DataSource{
	
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return DataSourceManager.getDataSource().getLogWriter();
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub
		DataSourceManager.getDataSource().setLogWriter(out);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub
		DataSourceManager.getDataSource().setLoginTimeout(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return DataSourceManager.getDataSource().getLoginTimeout();
	}

	public java.util.logging.Logger getParentLogger()
			throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return DataSourceManager.getDataSource().getParentLogger();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return DataSourceManager.getDataSource().unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return DataSourceManager.getDataSource().isWrapperFor(iface);
	}

	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return DataSourceManager.getDataSource().getConnection();
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		// TODO Auto-generated method stub
		return DataSourceManager.getDataSource().getConnection(username,password);
	}

	 
}
