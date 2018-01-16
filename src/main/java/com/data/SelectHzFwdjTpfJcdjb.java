package com.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.oracle.Oracle;
import com.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SelectHzFwdjTpfJcdjb implements Job {

	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	public static void main(String[] args) {
		new SelectHzFwdjTpfJcdjb().addKey();
	}

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
		int index = 1;
		jedis.select(index);
		logger.info("选择" + index + "号Redis数据库");

		// 定义Oralce并获取连接
		Oracle jracle = new Oracle();
		Connection connection = jracle.getConnection();

		// 查询语句
		String sql = "SELECT DJB.FWZL, QLR.QLRMC FROM HZ_FWDJ.TPF_JCDJB DJB LEFT JOIN (SELECT SY.QLID, SY.SYQZSH, SY.JCDJBID FROM HZ_FWDJ.TPF_SYQDJB SY WHERE SY.SFLS = 0 GROUP BY SY.QLID, SY.SYQZSH, SY.JCDJBID) S ON S.JCDJBID = DJB.ID LEFT OUTER JOIN HZ_FWDJ.TPF_QLR QLR ON QLR.QLID = S.QLID WHERE DJB.SFLS = 0 AND S.JCDJBID IS NOT NULL AND QLR.QLRLX = 4 AND QLR.SFLS = 0 AND DJB.FWZL IS NOT NULL AND QLR.QLRMC IS NOT NULL";

		// 执行sql
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
				jedis.lpush(resultSet.getString("FWZL"), resultSet.getString("QLRMC"));

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

		// 保存数据
		jedis.save();

		// 关闭Redis
		jedis.close();
		jedisPool.close();
		logger.info("Redis已关闭！");

	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("定时任务开始！");
		addKey();
		logger.info("定时任务结束！");
	}

}
