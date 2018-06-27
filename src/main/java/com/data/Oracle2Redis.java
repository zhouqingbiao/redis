package com.data;

import com.alibaba.fastjson.JSON;
import com.logger.Logger;
import com.oracle.Oracle;
import com.redis.Redis;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Oracle2Redis {

    /**
     * 通过传入index指定更新Redis
     *
     * @param index
     */
    public void addKey(int index) {

        // 不能小于0号数据库
        if (index < 0) {
            Logger.logger.info("不能小于0号数据库。");
            return;
        }

        // 获得Jedis
        try (Jedis jedis = Redis.Pool.getResource()) {

            // 判断是否连接成功
            if (("PONG").equals(jedis.ping())) {
                Logger.logger.info("Redis连接成功！");
            } else {
                Logger.logger.info("Redis连接失败！");
                return;
            }

            // 选择数据库
            jedis.select(index);
            Logger.logger.info("选择" + index + "号数据库。");

            // 定义Oralce并获取连接
            Oracle jracle = new Oracle();
            Connection connection = jracle.getConnection();

            // 获取Properties数据
            Properties properties = new Properties();

            String sql = null;
            try {
                properties.load(Oracle2Redis.class.getResourceAsStream("Redis.properties"));
                Logger.logger.info("成功加载Redis.properties配置文件。");
                sql = properties.getProperty(String.valueOf(index));
            } catch (IOException e) {
                Logger.logger.warn(e.getMessage(), e);
            }

            // 执行sql
            PreparedStatement preparedStatement = jracle.getPreparedStatement(connection, sql);
            ResultSet resultSet = jracle.getResultSet(preparedStatement);

            // 删除当前数据库所有key
            jedis.flushDB();
            Logger.logger.info("当前数据库key已删除！");

            // addKey
            // 记录Oracle数据量条数
            int count = 0;

            // 定义RedisValue键值对
            Map<String, String> map;

            try {
                while (resultSet.next()) {

                    map = new HashMap<String, String>();

                    for (int j = 1; j <= resultSet.getMetaData().getColumnCount(); j++) {
                        map.put(resultSet.getMetaData().getColumnName(j), resultSet.getString(j));
                    }

                    // 加入Redis
                    jedis.lpush(resultSet.getString(1), JSON.toJSONString(map));

                    // 数据量自增长，达到100000时输出。
                    count++;
                    if (count % 100000 == 0) {
                        Logger.logger.info("Oracle=" + String.valueOf(count) + "!");
                    }
                }
                Logger.logger.info("Oracle=" + String.valueOf(count) + "!");
            } catch (SQLException e) {
                Logger.logger.warn(e.getMessage(), e);
            } finally {
                // 无论如何都尝试关闭Oracle
                jracle.close(resultSet, preparedStatement, connection);
            }
        }
    }
}