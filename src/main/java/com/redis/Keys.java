package com.redis;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Keys {

	public static void main(String[] args) {

		// 获得keys集合
		Set<String> set = new Keys().getKeys("*");

		// 遍历输出keys
		for (String string : set) {
			System.out.println(string);
		}
	}

	public Set<String> getKeys(String keys) {

		// 获得JedisPool
		JedisPool jedisPool = new Redis().getJedisPool();

		// 获得Jedis
		Jedis jedis = jedisPool.getResource();

		// 判断是否连接成功
		if (("PONG").equals(jedis.ping())) {
			System.out.println("Redis连接成功！");
		}

		// 选择数据库
		jedis.select(0);

		// 获取keys
		Set<String> set = jedis.keys(keys);

		// 输出当前数据库keys数量
		System.out.println("当前数据库keys数量：" + jedis.dbSize());

		// 关闭
		jedis.close();
		jedisPool.close();

		// 返回keys集合
		return set;

	}

}
