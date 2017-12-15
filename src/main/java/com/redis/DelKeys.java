package com.redis;

import java.util.Set;
import java.util.logging.Logger;

import com.log.Reggol;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class DelKeys {

	// ��ȡLogger
	static Logger logger = Reggol.getLogger();

	public static void main(String[] args) {

		// ɾ��keys
		new DelKeys().delKeys("*");
	}

	/**
	 * ����ɾ��key
	 * 
	 * @param keys
	 */
	public void delKeys(String keys) {

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

		// ��ȡkeys
		Set<String> set = jedis.keys(keys);

		// ����ɾ��keys
		for (String string : set) {
			jedis.del(string);
		}

		// �����ǰ���ݿ�keys����
		logger.info("��ǰ���ݿ�keys������" + jedis.dbSize());

		// �ر�
		jedis.close();
		jedisPool.close();
		logger.info("Redis�ѹرգ�");

	}
}
