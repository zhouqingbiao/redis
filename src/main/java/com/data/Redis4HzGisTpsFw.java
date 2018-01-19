package com.data;

import java.util.ArrayList;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Redis4HzGisTpsFw {

	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	public ArrayList<Object> getData(String keys) {

		// 定义List
		ArrayList<Object> resultList = new ArrayList<Object>();

		// 过滤垃圾信息

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

		// 查询数据库
		Set<String> set = jedis.keys(keys);

		if (set.isEmpty()) {
			logger.info("查询结果为空！");
			return resultList;
		}

		for (String key : set) {

			// 获取RedisList
			for (String result : jedis.lrange(key, 0, jedis.llen(key) - 1)) {
				resultList.add(JSON.parse(result));

				// 如果条数达到20则返回List
				if (resultList.size() == 20) {
					// 需提前关闭Redis
					jedis.close();
					jedisPool.close();
					logger.info("Redis已关闭！");
					return resultList;
				}
			}
		}

		// 关闭Redis
		jedis.close();
		jedisPool.close();
		logger.info("Redis已关闭！");

		return resultList;
	}
}
