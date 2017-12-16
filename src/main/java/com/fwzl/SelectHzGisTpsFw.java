package com.fwzl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.log.Reggol;
import com.oracle.Oracle;
import com.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SelectHzGisTpsFw implements Job {

	// 获得Logger
	static Logger logger = Reggol.getLogger();

	public static void main(String[] args) {
		new SelectHzGisTpsFw().addKey();
	}

	/**
	 * Oracle中房屋坐落DistinctCount值作为Redis的key
	 */
	public void addKey() {

		// 获取distinct_count_fwzl总数
		String sql = "select count(distinct(t.fwzl)) distinct_count from hz_gis.tps_fw t where t.lsbz = 0 and t.fwzl is not null and t.fwsmzq = 1201";

		int distinctCount = this.getDistinctCount(sql);

		// 定义Redis
		JedisPool jedisPool = new Redis().getJedisPool();

		Jedis jedis = jedisPool.getResource();

		// 判断是否连接成功
		if (("PONG").equals(jedis.ping())) {
			logger.info("Redis连接成功！");
		}

		// 选择数据库
		int index = 0;
		jedis.select(index);
		logger.info("选择" + index + "号Redis数据库");

		// 获取Redis_keys总数
		long dbSize = jedis.dbSize();
		logger.info("Redis" + ":" + dbSize);

		// 开始时间
		long startTime = System.nanoTime();
		logger.info("开始时间：" + startTime);

		// Redis不存在则去35库查询--执行模块
		if (distinctCount != dbSize) {

			// 条数不对称输出
			logger.info("Oracle:" + distinctCount + " != " + dbSize + ":" + "Redis");

			// 定义Oralce并获取连接
			Oracle jracle = new Oracle();
			Connection connection = jracle.getConnection();

			// Redis不存在则去35库查询--查询语句
			sql = "select t.id, t.fwzl from hz_gis.tps_fw t where t.lsbz = 0 and t.fwzl is not null and t.fwsmzq = 1201";

			// 不存在才执行sql
			PreparedStatement preparedStatement = jracle.getPreparedStatement(connection, sql);
			ResultSet resultSet = jracle.getResultSet(preparedStatement);

			// 删除当前数据库所有key
			jedis.flushDB();
			logger.info("当前Redis数据库key已删除");

			// 新增所有keys
			try {
				// 记录Oracle数据量条数
				int count = 0;
				while (resultSet.next()) {

					// 加入RedisList
					jedis.lpush(resultSet.getString("FWZL"), resultSet.getString("ID"));

					// 数据量自增长
					count++;
					if (count % 100000 == 0) {
						logger.info(String.valueOf(count));
					}
				}
				logger.info(String.valueOf(count));
			} catch (SQLException e) {
				e.printStackTrace();
				logger.warning(Reggol.getStackTrace(e));
			} finally {
				jracle.close(resultSet, preparedStatement, connection);
			}

			// 重新获取当前数据库所有key
			dbSize = jedis.dbSize();

		} else {
			// 条数对称输出
			logger.info("Oracle:" + distinctCount + " == " + dbSize + ":" + "Redis");
		}

		// 保存数据
		jedis.save();

		// 关闭Redis
		jedis.close();
		jedisPool.close();
		logger.info("Redis已关闭！");

		// 停止时间
		long stopTime = System.nanoTime();
		logger.info("停止时间：" + stopTime);

		// 总时间消耗
		logger.info("运行时间：" + (stopTime - startTime) + "纳秒");
		logger.info("运行时间：" + (stopTime - startTime) / 1000000 + "毫秒");
		logger.info("运行时间：" + (stopTime - startTime) / 1000000000 + "秒");

		logger.info("Oracle条数：" + distinctCount);

		logger.info("Redis条数：" + dbSize);

	}

	/**
	 * 获取房屋坐落DistinctCount数量
	 * 
	 * @param sql
	 * @return int
	 */
	public int getDistinctCount(String sql) {
		// 定义Oralce并获取连接
		Oracle jracle = new Oracle();
		Connection connection = jracle.getConnection();

		PreparedStatement preparedStatement = jracle.getPreparedStatement(connection, sql);
		ResultSet resultSet = jracle.getResultSet(preparedStatement);

		// 获取房屋坐落DistinctCount数量
		int distinctCount = 0;
		try {
			while (resultSet.next()) {
				distinctCount = resultSet.getInt(1);
				logger.info("Oracle房屋坐落DistinctCount数量：" + distinctCount);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warning(Reggol.getStackTrace(e));
		} finally {
			jracle.close(resultSet, preparedStatement, connection);
		}

		// 返回房屋坐落DistinctCount数量
		return distinctCount;
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("定时任务开始！");
		addKey();
		logger.info("定时任务结束！");
	}

}
