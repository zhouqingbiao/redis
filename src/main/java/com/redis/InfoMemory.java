package com.redis;

import java.util.logging.Logger;

import com.log.Reggol;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class InfoMemory {

	// ��ȡLogger
	static Logger logger = Reggol.getLogger();

	public static void main(String[] args) {
		logger.info(new InfoMemory().getInfoMemory());
	}

	public String getInfoMemory() {

		// ���JedisPool
		JedisPool jedisPool = new Redis().getJedisPool();

		// ���Jedis
		Jedis jedis = jedisPool.getResource();

		// �ж��Ƿ����ӳɹ�
		if (("PONG").equals(jedis.ping())) {
			logger.info("Redis���ӳɹ���");
		}

		// ��ȡ���ݲ����
		String infoMemory = jedis.info("memory");

		// �ر�
		jedis.close();
		jedisPool.close();
		logger.info("Redis�ѹرգ�");

		return infoMemory;
	}
}
