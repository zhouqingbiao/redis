package com.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.oracle.Oracle;
import com.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SelectHzGisTpsFw {

	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	public void addKey() {

		// 定义Redis
		JedisPool jedisPool = new Redis().getJedisPool();

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Redis定义失败！请检查Redis是否正常运行！");
			logger.warn(e);
			return;
		}

		// 判断是否连接成功
		if (("PONG").equals(jedis.ping())) {
			logger.info("Redis连接成功！");
		}

		// 选择数据库
		int index = 0;
		jedis.select(index);
		logger.info("选择" + index + "号Redis数据库");

		// 删除当前数据库所有key
		jedis.flushDB();
		logger.info("当前Redis数据库key已删除！");

		// 定义Oralce并获取连接
		Oracle jracle = new Oracle();
		Connection connection = jracle.getConnection();

		// sql
		String sql = "SELECT T.FWZL, T.ID, T.FWCODE FROM HZ_GIS.TPS_FW T WHERE T.LSBZ = 0 AND T.FWZL IS NOT NULL AND T.FWSMZQ = 1201";

		// 执行sql
		PreparedStatement preparedStatement = jracle.getPreparedStatement(connection, sql);
		ResultSet resultSet = jracle.getResultSet(preparedStatement);

		// 新增所有keys
		try {
			// 记录Oracle数据量条数
			int count = 0;

			Map<String, String> map;
			while (resultSet.next()) {

				// 加入Redis

				map = new HashMap<String, String>();

				for (int j = 1; j <= resultSet.getMetaData().getColumnCount(); j++) {
					map.put(resultSet.getMetaData().getColumnName(j), resultSet.getString(j));
				}

				jedis.lpush(resultSet.getString(1), JSON.toJSONString(map));

				// 数据量自增长
				count++;
				if (count % 100000 == 0) {
					logger.info(String.valueOf(count));
				}
			}
			logger.info(String.valueOf(count));
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warn(e);
		} finally {
			jracle.close(resultSet, preparedStatement, connection);
		}

		// 关闭Redis
		jedis.close();
		jedisPool.close();
		logger.info("Redis已关闭！");

	}
}
