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

	// ���Logger
	static Logger logger = Reggol.getLogger();

	public static void main(String[] args) {
		new SelectHzGisTpsFw().addKey();
	}

	/**
	 * Oracle�з�������DistinctCountֵ��ΪRedis��key
	 */
	public void addKey() {

		// ��ȡdistinct_count_fwzl����
		String sql = "select count(distinct(t.fwzl)) distinct_count from hz_gis.tps_fw t where t.lsbz = 0 and t.fwzl is not null and t.fwsmzq = 1201";

		int distinctCount = this.getDistinctCount(sql);

		// ����Redis
		JedisPool jedisPool = new Redis().getJedisPool();

		Jedis jedis = jedisPool.getResource();

		// �ж��Ƿ����ӳɹ�
		if (("PONG").equals(jedis.ping())) {
			logger.info("Redis���ӳɹ���");
		}

		// ѡ�����ݿ�
		int index = 0;
		jedis.select(index);
		logger.info("ѡ��" + index + "��Redis���ݿ�");

		// ��ȡRedis_keys����
		long dbSize = jedis.dbSize();
		logger.info("Redis" + ":" + dbSize);

		// ��ʼʱ��
		long startTime = System.nanoTime();
		logger.info("��ʼʱ�䣺" + startTime);

		// Redis��������ȥ35���ѯ--ִ��ģ��
		if (distinctCount != dbSize) {

			// �������Գ����
			logger.info("Oracle:" + distinctCount + " != " + dbSize + ":" + "Redis");

			// ����Oralce����ȡ����
			Oracle jracle = new Oracle();
			Connection connection = jracle.getConnection();

			// Redis��������ȥ35���ѯ--��ѯ���
			sql = "select t.id, t.fwzl from hz_gis.tps_fw t where t.lsbz = 0 and t.fwzl is not null and t.fwsmzq = 1201";

			// �����ڲ�ִ��sql
			PreparedStatement preparedStatement = jracle.getPreparedStatement(connection, sql);
			ResultSet resultSet = jracle.getResultSet(preparedStatement);

			// ɾ����ǰ���ݿ�����key
			jedis.flushDB();
			logger.info("��ǰRedis���ݿ�key��ɾ��");

			// ��������keys
			try {
				// ��¼Oracle����������
				int count = 0;
				while (resultSet.next()) {

					// ����RedisList
					jedis.lpush(resultSet.getString("FWZL"), resultSet.getString("ID"));

					// ������������
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

			// ���»�ȡ��ǰ���ݿ�����key
			dbSize = jedis.dbSize();

		} else {
			// �����Գ����
			logger.info("Oracle:" + distinctCount + " == " + dbSize + ":" + "Redis");
		}

		// ��������
		jedis.save();

		// �ر�Redis
		jedis.close();
		jedisPool.close();
		logger.info("Redis�ѹرգ�");

		// ֹͣʱ��
		long stopTime = System.nanoTime();
		logger.info("ֹͣʱ�䣺" + stopTime);

		// ��ʱ������
		logger.info("����ʱ�䣺" + (stopTime - startTime) + "����");
		logger.info("����ʱ�䣺" + (stopTime - startTime) / 1000000 + "����");
		logger.info("����ʱ�䣺" + (stopTime - startTime) / 1000000000 + "��");

		logger.info("Oracle������" + distinctCount);

		logger.info("Redis������" + dbSize);

	}

	/**
	 * ��ȡ��������DistinctCount����
	 * 
	 * @param sql
	 * @return int
	 */
	public int getDistinctCount(String sql) {
		// ����Oralce����ȡ����
		Oracle jracle = new Oracle();
		Connection connection = jracle.getConnection();

		PreparedStatement preparedStatement = jracle.getPreparedStatement(connection, sql);
		ResultSet resultSet = jracle.getResultSet(preparedStatement);

		// ��ȡ��������DistinctCount����
		int distinctCount = 0;
		try {
			while (resultSet.next()) {
				distinctCount = resultSet.getInt(1);
				logger.info("Oracle��������DistinctCount������" + distinctCount);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warning(Reggol.getStackTrace(e));
		} finally {
			jracle.close(resultSet, preparedStatement, connection);
		}

		// ���ط�������DistinctCount����
		return distinctCount;
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("��ʱ����ʼ��");
		addKey();
		logger.info("��ʱ���������");
	}

}
