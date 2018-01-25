package com.moodlevideo.server.task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBcon {
	
	private static Logger logger = LoggerFactory
			.getLogger(DBcon.class);
	
	private String driver = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://139.224.228.113:3306/moodlevideo?useUnicode=true&characterEncoding=utf-8";
	private String user = "project_manager";
	private String password = "CallMe123321";
	private Connection conn;
	private Statement st;

	
	
	/**
     * 查询并返回sql语句结果
     * @param  sql 执行的sql语句
     * @return result 返回的结果集
     */
	public ResultSet Query(String sql) {	
		ResultSet result = null;
//		logger.debug("【jdbc数据库服务】query:"+sql);
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password); // 连接数据库
			
			st = conn.createStatement();
			result = st.executeQuery(sql);
//			logger.debug("【jdbc数据库服务】Query succeed!");
			
		} catch(Exception e) {
			logger.debug("【jdbc数据库服务】Query Error!");
		}
		return result;
	}
	
	/**
     * 修改、插入、删除sql语句的执行
     * @param  sql 执行的sql语句
     * @return true/false 执行是否成功
     */
	public boolean Update(String sql) {
//		logger.debug("【jdbc数据库服务】update:"+sql);
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password); //连接数据库
			
			st = conn.createStatement();
			st.executeUpdate(sql);
//			logger.debug("【jdbc数据库服务】Execute Successful!");
		} catch(Exception e) {
			logger.debug("【jdbc数据库服务】Execute Error!");
			return false;
		}
		return true;
	}
	
	/**
     * 关闭数据库链接
     * @return true/false 执行是否成功
     */
	public boolean close() {
		try {
			conn.close();
		} catch(Exception e) {
			logger.debug("【jdbc数据库服务】Close Error!!!");
			return false;
		}
		return true;
	}
}
