package com.redis;

import java.util.logging.Logger;

import com.log.Reggol;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class InfoServer {
	
	// ��ȡLogger
	static Logger logger = Reggol.getLogger();
	
	public static void main(String[] args) {
		logger.info(new InfoServer().getInfoServer());
	}

	public String getInfoServer() {

		// ���JedisPool
		JedisPool jedisPool = new Redis().getJedisPool();

		// ���Jedis
		Jedis jedis = jedisPool.getResource();

		// �ж��Ƿ����ӳɹ�
		if (("PONG").equals(jedis.ping())) {
			logger.info("Redis���ӳɹ���");
		}

		// ��ȡ���ݲ����
		String infoServer = jedis.info("server");

		// �ر�
		jedis.close();
		jedisPool.close();
		logger.info("Redis�ѹرգ�");

		return infoServer;
	}
}
