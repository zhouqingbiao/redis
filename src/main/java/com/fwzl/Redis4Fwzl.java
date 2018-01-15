package com.fwzl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oracle.Oracle;
import com.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Redis4Fwzl {

	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	public String getFwzl4Redis(String fwzl) {

		// 过滤垃圾信息
		// TODO

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
		Set<String> set = jedis.keys(fwzl);

		if (set.isEmpty()) {
			logger.info("查询结果为空！");
			return null;
		}

		StringBuffer fwzlStringBuffer = new StringBuffer();
		StringBuffer idStringBuffer = new StringBuffer();

		// 定义idList
		List<String> idList = new ArrayList<String>();

		// 组装StringBuffer
		for (String fwzlString : set) {
			// 组装fwzlStringBuffer
			fwzlStringBuffer.append(fwzlString);

			// 获取RedisList
			List<String> list = jedis.lrange(fwzlString, 0, jedis.llen(fwzlString));

			// 组装idStringBuffer
			for (String idString : list) {

				// add idString to idList
				idList.add(idString);

				idStringBuffer.append(idString);
				idStringBuffer.append(",");
			}
			fwzlStringBuffer.append(",");
		}

		// 删除最后字符
		idStringBuffer.deleteCharAt(idStringBuffer.length() - 1);
		fwzlStringBuffer.deleteCharAt(fwzlStringBuffer.length() - 1);

		// 输出StringBuffer
		logger.info("ID：" + idStringBuffer);
		logger.info("FWZL：" + fwzlStringBuffer);

		// 关闭Redis
		jedis.close();
		jedisPool.close();
		logger.info("Redis已关闭！");

		// 获得Oracle数据
		// ArrayList<String> arrayList = this.getFwzl4Oracle(idList);

		return idStringBuffer.toString();
	}

	public ArrayList<String> getFwzl4Oracle(List<String> list) {

		// 定义Oralce并获取连接
		Oracle jracle = new Oracle();
		Connection connection = jracle.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		// 数据存入ArrayList
		ArrayList<String> arrayList = new ArrayList<String>();

		// ListIterator化
		Iterator<String> iterator = list.iterator();
		// 拼接sql
		StringBuffer sql = new StringBuffer();
		String sql_str = "select t.id from hz_gis.tps_fw t where t.lsbz = 0 and t.fwzl is not null and t.fwsmzq = 1201 and t.id in ";
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

					try {
						while (resultSet.next()) {

							// 获取总列数并循环保存进ArrayList
							for (int j = 1; j <= resultSet.getMetaData().getColumnCount(); j++) {
								arrayList.add(resultSet.getString(j));
							}
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
