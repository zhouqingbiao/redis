package com.redis;

import java.util.Set;
import java.util.logging.Logger;

import com.log.Reggol;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class DelKeys {

	// 获取Logger
	static Logger logger = Reggol.getLogger();

	public static void main(String[] args) {

		// 删除keys
		new DelKeys().delKeys("*");
	}

	/**
	 * 逐条删除key
	 * 
	 * @param keys
	 */
	public void delKeys(String keys) {

		// 获得JedisPool
		JedisPool jedisPool = new Redis().getJedisPool();

		// 获得Jedis
		Jedis jedis = jedisPool.getResource();

		// 判断是否连接成功
		if (("PONG").equals(jedis.ping())) {
			logger.info("Redis连接成功！");
		}

		// 选择数据库
		int index = 0;
		jedis.select(index);
		logger.info("选择" + index + "号Redis数据库");

		// 获取keys
		Set<String> set = jedis.keys(keys);

		// 遍历删除keys
		for (String string : set) {
			jedis.del(string);
		}

		// 输出当前数据库keys数量
		logger.info("当前数据库keys数量：" + jedis.dbSize());

		// 关闭
		jedis.close();
		jedisPool.close();
		logger.info("Redis已关闭！");

	}
}
