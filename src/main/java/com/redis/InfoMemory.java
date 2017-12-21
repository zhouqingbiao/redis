package com.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class InfoMemory {

	// 获取Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	public static void main(String[] args) {
		logger.info(new InfoMemory().getInfoMemory());
	}

	public String getInfoMemory() {

		// 获得JedisPool
		JedisPool jedisPool = new Redis().getJedisPool();

		// 获得Jedis
		Jedis jedis = jedisPool.getResource();

		// 判断是否连接成功
		if (("PONG").equals(jedis.ping())) {
			logger.info("Redis连接成功！");
		}

		// 获取数据并输出
		String infoMemory = jedis.info("memory");

		// 关闭
		jedis.close();
		jedisPool.close();
		logger.info("Redis已关闭！");

		return infoMemory;
	}
}
