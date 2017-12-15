package com.redis;

import java.util.logging.Logger;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.log.Reggol;

import redis.clients.jedis.JedisPool;

public class Redis {

	// ���Logger
	static Logger logger = Reggol.getLogger();

	/**
	 * 
	 * @return JedisPool
	 */
	public JedisPool getJedisPool() {

		// ����GenericObjectPoolConfig
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		logger.info("�ɹ�����GenericObjectPoolConfig");

		// ���������
		int maxTotal = 1024;
		poolConfig.setMaxTotal(maxTotal);
		logger.info("�������������Ϊ" + maxTotal);

		// ���ʵ�ַ
		String host = "localhost";

		// �˿�
		int port = 6379;

		// ��ʱʱ�䣬��λ������
		int timeout = 10000;

		// ����JedisPool
		JedisPool jedisPool = new JedisPool(poolConfig, host, port, timeout);
		logger.info("�ɹ�����JedisPool");
		logger.info("host" + "=" + host);
		logger.info("port" + "=" + port);
		logger.info("timeout" + "=" + timeout);

		// ����JedisPool
		return jedisPool;
	}
}
