package com.redis;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Keys {

	public static void main(String[] args) {

		// ���keys����
		Set<String> set = new Keys().getKeys("*");

		// �������keys
		for (String string : set) {
			System.out.println(string);
		}
	}

	public Set<String> getKeys(String keys) {

		// ���JedisPool
		JedisPool jedisPool = new Redis().getJedisPool();

		// ���Jedis
		Jedis jedis = jedisPool.getResource();

		// �ж��Ƿ����ӳɹ�
		if (("PONG").equals(jedis.ping())) {
			System.out.println("Redis���ӳɹ���");
		}

		// ѡ�����ݿ�
		jedis.select(0);

		// ��ȡkeys
		Set<String> set = jedis.keys(keys);

		// �����ǰ���ݿ�keys����
		System.out.println("��ǰ���ݿ�keys������" + jedis.dbSize());

		// �ر�
		jedis.close();
		jedisPool.close();

		// ����keys����
		return set;

	}

}
