package com.redis;

import java.util.logging.Logger;

import com.log.Reggol;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class InfoMemory {

	// 获取Logger
	static Logger logger = Reggol.getLogger();

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
