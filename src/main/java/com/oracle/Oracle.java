package com.oracle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Oracle {

	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	// 数据库连接次数，最大不超过4次。
	int i = 1;

	/**
	 * 
	 * @return Connection
	 */
	public Connection getConnection() {

		// 注册数据库驱动
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			logger.info("Oracle数据库驱动注册成功");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.warn(e);
		}

		String url = null;
		String user = null;
		String password = null;

		// 获取Properties数据
		Properties properties = new Properties();
		try {
			properties.load(Oracle.class.getResourceAsStream("Oracle.properties"));
			logger.info("成功加载Oracle.properties配置文件");
			url = properties.getProperty("url" + i);
			user = properties.getProperty("user");
			password = properties.getProperty("password");
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn(e);
		}
		// 获取数据库连接
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, user, password);
			logger.info("Oracle数据库连接成功");
			logger.info("url" + "=" + url);
			logger.info("user" + "=" + user);
			logger.info("password" + "=" + password);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warn(url + "连接失败！");
			logger.warn(e);

			// 次数自增长
			i++;

			// 超过4次程序退出
			if (i == 5) {
				logger.info("所有url都未能连接上，请检查数据库及通信情况！");
				logger.info("程序退出！");
				System.exit(0);
			}

			// 出错时重复调用直至超出最大连接次数
			return this.getConnection();

		}
		return connection;
	}

	/**
	 * 
	 * @param connection
	 * @param sql
	 * @return PreparedStatement
	 */
	public PreparedStatement getPreparedStatement(Connection connection, String sql) {

		// 创建PreparedStatement
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(sql);

			// 输出SQL
			logger.info(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warn(e);
		}
		return preparedStatement;
	}

	/**
	 * 
	 * @param preparedStatement
	 * @return ResultSet
	 */
	public ResultSet getResultSet(PreparedStatement preparedStatement) {

		// 执行查询
		ResultSet resultSet = null;

		try {
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warn(e);
		}
		return resultSet;

	}

	/**
	 * 
	 * @param resultSet
	 * @param preparedStatement
	 * @param connection
	 */
	public void close(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {

		// 关闭ResultSet
		if (resultSet != null) {
			try {
				resultSet.close();
				logger.info("ResultSet已关闭！");
			} catch (SQLException e) {
				e.printStackTrace();
				logger.warn(e);
			}
		}

		// 关闭PreparedStatement
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
				logger.info("PreparedStatement已关闭！");
			} catch (SQLException e) {
				e.printStackTrace();
				logger.warn(e);
			}
		}

		// 关闭Connection
		if (connection != null) {
			try {
				connection.close();
				logger.info("Connection已关闭！");
			} catch (SQLException e) {
				e.printStackTrace();
				logger.warn(e);
			}
		}

		logger.info("Oracle已关闭！");
	}
}