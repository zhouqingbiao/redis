package com.oracle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import com.log.Reggol;

public class Oracle {

	// ���Logger
	static Logger logger = Reggol.getLogger();

	// ���ݿ����Ӵ�������󲻳���4�Ρ�
	int i = 1;

	/**
	 * 
	 * @return Connection
	 */
	public Connection getConnection() {

		// ע�����ݿ�����
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			logger.info("Oracle���ݿ�����ע��ɹ�");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.warning(Reggol.getStackTrace(e));
		}

		String url = null;
		String user = null;
		String password = null;

		// ��ȡProperties����
		Properties properties = new Properties();
		try {
			properties.load(Oracle.class.getResourceAsStream("Oracle.properties"));
			logger.info("�ɹ�����Oracle.properties�����ļ�");
			url = properties.getProperty("url" + i);
			user = properties.getProperty("user");
			password = properties.getProperty("password");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ��ȡ���ݿ�����
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, user, password);
			logger.info("Oracle���ݿ����ӳɹ�");
			logger.info("url" + "=" + url);
			logger.info("user" + "=" + user);
			logger.info("password" + "=" + password);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warning(url + "����ʧ�ܣ�");
			logger.warning(Reggol.getStackTrace(e));

			// ����������
			i++;

			// ����4�γ����˳�
			if (i == 5) {
				logger.info("����url��δ�������ϣ��������ݿ⼰ͨ�������");
				logger.info("�����˳���");
				System.exit(0);
			}

			// ����ʱ�ظ�����ֱ������������Ӵ���
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

		// ����PreparedStatement
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(sql);

			// ���SQL
			logger.info(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warning(Reggol.getStackTrace(e));
		}
		return preparedStatement;
	}

	/**
	 * 
	 * @param preparedStatement
	 * @return ResultSet
	 */
	public ResultSet getResultSet(PreparedStatement preparedStatement) {

		// ִ�в�ѯ
		ResultSet resultSet = null;

		try {
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warning(Reggol.getStackTrace(e));
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

		// �ر�ResultSet
		if (resultSet != null) {
			try {
				resultSet.close();
				logger.info("ResultSet�ѹرգ�");
			} catch (SQLException e) {
				e.printStackTrace();
				logger.warning(Reggol.getStackTrace(e));
			}
		}

		// �ر�PreparedStatement
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
				logger.info("PreparedStatement�ѹرգ�");
			} catch (SQLException e) {
				e.printStackTrace();
				logger.warning(Reggol.getStackTrace(e));
			}
		}

		// �ر�Connection
		if (connection != null) {
			try {
				connection.close();
				logger.info("Connection�ѹرգ�");
			} catch (SQLException e) {
				e.printStackTrace();
				logger.warning(Reggol.getStackTrace(e));
			}
		}

		logger.info("Oracle�ѹرգ�");
	}
}