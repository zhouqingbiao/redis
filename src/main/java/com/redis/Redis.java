package com.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.JedisPool;

public class Redis {

	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	/**
	 * 
	 * @return JedisPool
	 */
	public JedisPool getJedisPool() {

		// 定义GenericObjectPoolConfig
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		logger.info("成功定义GenericObjectPoolConfig");

		// 最大连接数
		int maxTotal = 1024;
		poolConfig.setMaxTotal(maxTotal);
		logger.info("最大连接数设置为" + maxTotal);

		// 访问地址
		String host = "localhost";

		// 端口
		int port = 6379;

		// 超时时间，单位：毫秒
		int timeout = 10000;

		// 定义JedisPool
		JedisPool jedisPool = new JedisPool(poolConfig, host, port, timeout);

		logger.info("成功定义JedisPool");
		logger.info("host" + "=" + host);
		logger.info("port" + "=" + port);
		logger.info("timeout" + "=" + timeout);

		// 返回JedisPool
		return jedisPool;
	}
}
