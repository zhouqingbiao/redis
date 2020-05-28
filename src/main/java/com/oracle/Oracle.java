package com.oracle;

import com.logger.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Oracle {

    /**
     * @return Connection
     */
    public Connection getConnection() {

        // OracleDriver
        String className = "oracle.jdbc.driver.OracleDriver";
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            Logger.logger.warn(e.getMessage(), e);
        }

        String url = null;
        String user = null;
        String password = null;

        // Properties
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("Oracle.properties"));
            Logger.logger.info("成功加载Oracle.properties配置文件。");
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");
        } catch (IOException e) {
            Logger.logger.warn(e.getMessage(), e);
        }

        // 获取数据库连接
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {

            Logger.logger.warn(e.getMessage(), e);

            // 出错时重复调用下一个url, user, password直至超出最大连接次数
            return this.getConnection();
        }

        return connection;
    }

    /**
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
            Logger.logger.info(sql);
        } catch (SQLException e) {
            Logger.logger.warn(e.getMessage(), e);
        }

        return preparedStatement;
    }

    /**
     * @param preparedStatement
     * @return ResultSet
     */
    public ResultSet getResultSet(PreparedStatement preparedStatement) {

        // 执行查询
        ResultSet resultSet = null;

        try {
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            Logger.logger.warn(e.getMessage(), e);
        }

        return resultSet;
    }

    /**
     * @param resultSet
     * @param preparedStatement
     * @param connection
     */
    public void close(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {

        // 关闭ResultSet
        if (resultSet != null) {
            try {
                resultSet.close();
                Logger.logger.info("ResultSet已关闭！");
            } catch (SQLException e) {
                Logger.logger.warn(e.getMessage(), e);
            }
        }

        // 关闭PreparedStatement
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                Logger.logger.info("PreparedStatement已关闭！");
            } catch (SQLException e) {
                Logger.logger.warn(e.getMessage(), e);
            }
        }

        // 关闭Connection
        if (connection != null) {
            try {
                connection.close();
                Logger.logger.info("Connection已关闭！");
            } catch (SQLException e) {
                Logger.logger.warn(e.getMessage(), e);
            }
        }

        Logger.logger.info("Oracle已关闭！");
    }
}