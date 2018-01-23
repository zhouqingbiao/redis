package com.data;

import java.util.ArrayList;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Redis2WebService {

	// 获得Logger
	private static final Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	/**
	 * 根据index和rows以及keys返回数据
	 * 
	 * @param keys
	 * @param index
	 * @param rows
	 * @return
	 */
	public ArrayList<Object> getData(String keys, int index, int rows) {

		// 定义结果List
		ArrayList<Object> resultList = null;

		// 过滤垃圾信息

		logger.info("KYES:" + keys);
		logger.info("INDEX:" + index);
		logger.info("ROWS:" + rows);

		// 小于0返回null
		if (index < 0) {
			logger.info("INDEX不能小于0");
			return resultList;
		}

		// 小于或等于0返回null
		if (rows <= 0) {
			logger.info("ROWS不能小于或等于0");
			return resultList;
		}

		// 获得JedisPool
		JedisPool jedisPool = new Redis().getJedisPool();

		// 获得Jedis
		Jedis jedis = jedisPool.getResource();

		// 判断是否连接成功
		if (("PONG").equals(jedis.ping())) {
			logger.info("Redis连接成功！");
		} else {
			logger.info("Redis连接失败！");
			return resultList;
		}

		// 选择数据库
		jedis.select(index);
		logger.info("选择" + index + "号Redis数据库");

		// 查询数据库
		Set<String> set = jedis.keys(keys);

		// 无数据量返回null
		if (set.isEmpty()) {
			logger.info("查询结果为空！");
			return resultList;
		}

		// 获取结果List
		resultList = new ArrayList<Object>();
		for (String key : set) {

			// 获取RedisList
			for (String result : jedis.lrange(key, 0, jedis.llen(key) - 1)) {
				resultList.add(JSON.parse(result));

				// 如果条数达到rows则返回List
				if (resultList.size() == rows) {
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
