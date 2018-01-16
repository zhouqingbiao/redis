package com.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Redis4HzFwdjTpfJcdjb {

	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	public ArrayList<Map<String, String>> getData(String keys) {

		// 过滤垃圾信息
		// TODO

		// 获得JedisPool
		JedisPool jedisPool = new Redis().getJedisPool();

		// 获得Jedis
		Jedis jedis = jedisPool.getResource();

		// 判断是否连接成功
		if (("PONG").equals(jedis.ping())) {
			logger.info("Redis连接成功！");
		}

		// 选择数据库
		int index = 1;
		jedis.select(index);
		logger.info("选择" + index + "号Redis数据库");

		// 查询数据库
		Set<String> set = jedis.keys(keys);

		if (set.isEmpty()) {
			logger.info("查询结果为空！");
			return null;
		}

		// 定义List
		ArrayList<Map<String, String>> fqList = new ArrayList<Map<String, String>>();

		Map<String, String> fq = null;

		for (String fwzlString : set) {

			// 获取RedisList
			List<String> list = jedis.lrange(fwzlString, 0, jedis.llen(fwzlString));

			for (String idString : list) {
				// 组装Map
				fq = new HashMap<String, String>();

				fq.put(fwzlString, idString);

				// 组装List
				fqList.add(fq);
			}
		}

		// 关闭Redis
		jedis.close();
		jedisPool.close();
		logger.info("Redis已关闭！");

		return fqList;
	}
}
