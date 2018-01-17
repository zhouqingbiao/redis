package com.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oracle.Oracle;
import com.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Redis4HzGisTpsFwWithColumnName {

	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	public ArrayList<Map<String, String>> getData(String keys) {

		// 过滤垃圾信息

		// Redis开始时间
		long startTime4Redis = System.nanoTime();

		// 获得JedisPool
		JedisPool jedisPool = new Redis().getJedisPool();

		// 获得Jedis
		Jedis jedis = jedisPool.getResource();

		// 判断是否连接成功
		if (("PONG").equals(jedis.ping())) {
			logger.info("Redis连接成功！");
		}

		// 选择数据库
		int index = 0;
		jedis.select(index);
		logger.info("选择" + index + "号Redis数据库");

		// 查询数据库
		Set<String> set = jedis.keys(keys);

		if (set.isEmpty()) {
			logger.info("查询结果为空！");
			return null;
		}

		StringBuffer keysStringBuffer = new StringBuffer();
		StringBuffer valuesStringBuffer = new StringBuffer();

		// 定义idList
		List<String> resultList = new ArrayList<String>();

		// 组装StringBuffer
		for (String s : set) {
			// 组装fwzlStringBuffer
			keysStringBuffer.append(s);

			// 获取RedisList
			List<String> list = jedis.lrange(s, 0, jedis.llen(s) - 1);

			// 组装idStringBuffer
			for (String valuesString : list) {

				// add idString to idList
				resultList.add(valuesString);

				valuesStringBuffer.append(valuesString);
				valuesStringBuffer.append(",");
			}
			keysStringBuffer.append(",");
		}

		// 删除最后字符
		valuesStringBuffer.deleteCharAt(valuesStringBuffer.length() - 1);
		keysStringBuffer.deleteCharAt(keysStringBuffer.length() - 1);

		// 输出StringBuffer
		logger.info("KEYS：" + keysStringBuffer);
		logger.info("VALUES：" + valuesStringBuffer);

		// 关闭Redis
		jedis.close();
		jedisPool.close();
		logger.info("Redis已关闭！");

		// Redis停止时间
		long stopTime4Redis = System.nanoTime();

		// Redis.size
		long size4Redis = jedis.dbSize();

		// Oracle开始时间
		long startTime4Oracle = System.nanoTime();

		// 获得Oracle数据
		ArrayList<Map<String, String>> arrayList = this.getFwzl4Oracle(resultList);

		// Oracle.size
		long size4Oracle = arrayList.size();

		// Oracle停止时间
		long stopTime4Oracle = System.nanoTime();

		timeConsumption(startTime4Redis, stopTime4Redis, size4Redis, startTime4Oracle, stopTime4Oracle, size4Oracle);

		return arrayList;
	}

	public void timeConsumption(long startTime4Redis, long stopTime4Redis, long size4Redis, long startTime4Oracle,
			long stopTime4Oracle, long size4Oracle) {

		// Redis开始结束时间
		logger.info("开始时间（Redis）：" + startTime4Redis);
		logger.info("停止时间（Redis）：" + stopTime4Redis);

		// Redis总时间消耗
		logger.info("运行时间（Redis）：" + (stopTime4Redis - startTime4Redis) + "纳秒");
		logger.info("运行时间（Redis）：" + (stopTime4Redis - startTime4Redis) / 1000000 + "毫秒");
		logger.info("运行时间（Redis）：" + (stopTime4Redis - startTime4Redis) / 1000000000 + "秒");

		logger.info("Redis条数：" + size4Redis);

		// Redis开始结束时间
		logger.info("开始时间（Oracle）：" + startTime4Oracle);
		logger.info("停止时间（Oracle）：" + stopTime4Oracle);

		// Oracle总时间消耗
		logger.info("运行时间（Oracle）：" + (stopTime4Oracle - startTime4Oracle) + "纳秒");
		logger.info("运行时间（Oracle）：" + (stopTime4Oracle - startTime4Oracle) / 1000000 + "毫秒");
		logger.info("运行时间（Oracle）：" + (stopTime4Oracle - startTime4Oracle) / 1000000000 + "秒");

		logger.info("Oracle条数：" + size4Oracle);

		logger.info("总消耗时间：" + ((stopTime4Redis - startTime4Redis) + (stopTime4Oracle - startTime4Oracle)) + "纳秒");
		logger.info("总消耗时间："
				+ ((stopTime4Redis - startTime4Redis) / 1000000 + (stopTime4Oracle - startTime4Oracle) / 1000000)
				+ "毫秒");
		logger.info("总消耗时间："
				+ ((stopTime4Redis - startTime4Redis) / 1000000000 + (stopTime4Oracle - startTime4Oracle) / 1000000000)
				+ "秒");
	}

	public ArrayList<Map<String, String>> getFwzl4Oracle(List<String> list) {

		// 定义Oralce并获取连接
		Oracle jracle = new Oracle();
		Connection connection = jracle.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		// 数据存入Map
		ArrayList<Map<String, String>> arrayList = new ArrayList<Map<String, String>>();

		// ListIterator化
		Iterator<String> iterator = list.iterator();
		// 拼接sql
		StringBuffer sql = new StringBuffer();
		String sql_str = "SELECT * FROM HZ_GIS.TPS_FW T WHERE T.LSBZ = 0 AND T.FWZL IS NOT NULL AND T.FWSMZQ = 1201 AND T.FWCODE IN ";
		sql.append(sql_str);
		try {
			sql.append("(");
			for (int i = 1; i <= list.size(); i++) {
				sql.append("?");
				sql.append(",");

				// 处理ORA-01795错误或达到最大值处理
				if (i % 1000 == 0 || i == list.size()) {
					sql.deleteCharAt(sql.length() - 1);
					sql.append(")");

					preparedStatement = jracle.getPreparedStatement(connection, sql.toString());

					try {
						// 设置问号的值
						for (int j = 1; j <= i; j++) {
							preparedStatement.setString(j, iterator.next());
							iterator.remove();
						}

					} catch (SQLException e) {
						e.printStackTrace();
					}

					resultSet = jracle.getResultSet(preparedStatement);

					Map<String, String> map;
					try {
						while (resultSet.next()) {
							map = new HashMap<String, String>();
							// 获取总列数并循环保存进Map
							for (int j = 1; j <= resultSet.getMetaData().getColumnCount(); j++) {
								map.put(resultSet.getMetaData().getColumnName(j), resultSet.getString(j));
							}
							arrayList.add(map);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					// 重置sql
					sql.setLength(0);
					sql.append(sql_str);
					sql.append("(");
					// 重置i
					i = 0;
				}
			}
		} finally {
			jracle.close(resultSet, preparedStatement, connection);
		}

		return arrayList;
	}
}
