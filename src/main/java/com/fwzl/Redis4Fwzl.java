package com.fwzl;

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
import java.util.logging.Logger;

import com.log.Reggol;
import com.oracle.Oracle;
import com.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Redis4Fwzl {

	// ���Logger
	static Logger logger = Reggol.getLogger();

	public ArrayList<Map<String, String>> getFwzl4Redis(String fwzl) {

		// ����������Ϣ
		// TODO

		// Redis��ʼʱ��
		long startTime4Redis = System.nanoTime();

		// ���JedisPool
		JedisPool jedisPool = new Redis().getJedisPool();

		// ���Jedis
		Jedis jedis = jedisPool.getResource();

		// �ж��Ƿ����ӳɹ�
		if (("PONG").equals(jedis.ping())) {
			logger.info("Redis���ӳɹ���");
		}

		// ѡ�����ݿ�
		int index = 0;
		jedis.select(index);
		logger.info("ѡ��" + index + "��Redis���ݿ�");

		// ��ѯ���ݿ�
		Set<String> set = jedis.keys(fwzl);

		if (set.isEmpty()) {
			logger.info("��ѯ���Ϊ�գ�");
			return null;
		}

		StringBuffer fwzlStringBuffer = new StringBuffer();
		StringBuffer idStringBuffer = new StringBuffer();

		// ����idList
		List<String> idList = new ArrayList<String>();

		// ��װStringBuffer
		for (String fwzlString : set) {
			// ��װfwzlStringBuffer
			fwzlStringBuffer.append(fwzlString);

			// ��ȡRedisList
			List<String> list = jedis.lrange(fwzlString, 0, jedis.llen(fwzlString));

			// ��װidStringBuffer
			for (String idString : list) {

				// add idString to idList
				idList.add(idString);

				idStringBuffer.append(idString);
				idStringBuffer.append(",");
			}
			fwzlStringBuffer.append(",");
		}

		// ɾ������ַ�
		idStringBuffer.deleteCharAt(idStringBuffer.length() - 1);
		fwzlStringBuffer.deleteCharAt(fwzlStringBuffer.length() - 1);

		// ���StringBuffer
		logger.info("ID��" + idStringBuffer);
		logger.info("FWZL��" + fwzlStringBuffer);

		// �ر�Redis
		jedis.close();
		jedisPool.close();
		logger.info("Redis�ѹرգ�");

		// Redisֹͣʱ��
		long stopTime4Redis = System.nanoTime();
		logger.info("��ʼʱ�䣨Redis����" + startTime4Redis);
		logger.info("ֹͣʱ�䣨Redis����" + stopTime4Redis);

		// Redis��ʱ������
		logger.info("����ʱ�䣨Redis����" + (stopTime4Redis - startTime4Redis) + "����");
		logger.info("����ʱ�䣨Redis����" + (stopTime4Redis - startTime4Redis) / 1000000 + "����");
		logger.info("����ʱ�䣨Redis����" + (stopTime4Redis - startTime4Redis) / 1000000000 + "��");
		logger.info("Redis������" + set.size());

		// ----------------------------------------------------------------------------------------------------------

		// Oracle��ʼʱ��
		long startTime4Oracle = System.nanoTime();

		// ���Oracle����
		ArrayList<Map<String, String>> arrayList = this.getFwzl4Oracle(idList);

		// Oracleֹͣʱ��
		long stopTime4Oracle = System.nanoTime();
		logger.info("��ʼʱ�䣨Oracle����" + startTime4Oracle);
		logger.info("ֹͣʱ�䣨Oracle����" + stopTime4Oracle);

		// Oracle��ʱ������
		logger.info("����ʱ�䣨Oracle����" + (stopTime4Oracle - startTime4Oracle) + "����");
		logger.info("����ʱ�䣨Oracle����" + (stopTime4Oracle - startTime4Oracle) / 1000000 + "����");
		logger.info("����ʱ�䣨Oracle����" + (stopTime4Oracle - startTime4Oracle) / 1000000000 + "��");
		logger.info("Oracle������" + arrayList.size());
		logger.info(
				"----------------------------------------------------------------------------------------------------------");

		// ----------------------------------------------------------------------------------------------------------

		logger.info("Redis��ѯ�ؼ��֣�" + fwzl);
		logger.info("Redis����ʱ�䣺" + (stopTime4Redis - startTime4Redis) + "����");
		logger.info("Redis����ʱ�䣺" + (stopTime4Redis - startTime4Redis) / 1000000 + "����");
		logger.info("Redis����ʱ�䣺" + (stopTime4Redis - startTime4Redis) / 1000000000 + "��");
		logger.info(
				"----------------------------------------------------------------------------------------------------------");
		logger.info("Oracle��ȷ��ѯ��" + idStringBuffer);
		logger.info("Oracle����ʱ�䣺" + (stopTime4Oracle - startTime4Oracle) + "����");
		logger.info("Oracle����ʱ�䣺" + (stopTime4Oracle - startTime4Oracle) / 1000000 + "����");
		logger.info("Oracle����ʱ�䣺" + (stopTime4Oracle - startTime4Oracle) / 1000000000 + "��");
		logger.info(
				"----------------------------------------------------------------------------------------------------------");
		logger.info("������ʱ�䣺" + ((stopTime4Redis - startTime4Redis) + (stopTime4Oracle - startTime4Oracle)) + "����");
		logger.info("������ʱ�䣺"
				+ ((stopTime4Redis - startTime4Redis) / 1000000 + (stopTime4Oracle - startTime4Oracle) / 1000000)
				+ "����");
		logger.info("������ʱ�䣺"
				+ ((stopTime4Redis - startTime4Redis) / 1000000000 + (stopTime4Oracle - startTime4Oracle) / 1000000000)
				+ "��");
		return arrayList;
	}

	public ArrayList<Map<String, String>> getFwzl4Oracle(List<String> list) {

		// ����Oralce����ȡ����
		Oracle jracle = new Oracle();
		Connection connection = jracle.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		// ���ݴ���Map
		ArrayList<Map<String, String>> arrayList = new ArrayList<Map<String, String>>();

		// ListIterator��
		Iterator<String> iterator = list.iterator();
		// ƴ��sql
		StringBuffer sql = new StringBuffer();
		sql.append(
				"select t.* from hz_gis.tps_fw t where t.lsbz = 0 and t.fwzl is not null and t.fwsmzq = 1201 and t.id in ");
		try {
			sql.append("(");
			for (int i = 1; i <= list.size(); i++) {
				sql.append("?");
				sql.append(",");

				// ����ORA-01795�����ﵽ���ֵ����
				if (i % 1000 == 0 || i == list.size()) {
					sql.deleteCharAt(sql.length() - 1);
					sql.append(")");

					preparedStatement = jracle.getPreparedStatement(connection, sql.toString());

					try {
						// �����ʺŵ�ֵ
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
							// ��ȡ��������ѭ�������Map
							for (int j = 1; j <= resultSet.getMetaData().getColumnCount(); j++) {
								map.put(resultSet.getMetaData().getColumnName(j), resultSet.getString(j));
							}
							arrayList.add(map);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					// ����sql
					sql.setLength(0);
					sql.append(
							"select t.* from hz_gis.tps_fw t where t.lsbz = 0 and t.fwzl is not null and t.fwsmzq = 1201 and t.id in ");
					sql.append("(");
					// ����i
					i = 0;
				}
			}
		} finally {
			jracle.close(resultSet, preparedStatement, connection);
		}

		return arrayList;
	}
}
