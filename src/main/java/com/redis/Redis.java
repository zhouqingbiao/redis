package com.redis;

import com.logger.Logger;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {

    public static JedisPool Pool = null;

    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        int maxTotal = 1024;
        poolConfig.setMaxTotal(maxTotal);
        Logger.logger.info("maxTotal" + "=" + maxTotal);

        String host = "localhost";
        Logger.logger.info("host" + "=" + host);

        int port = 6379;
        Logger.logger.info("port" + "=" + port);

        int timeout = 100000;
        Logger.logger.info("timeout" + "=" + timeout);

        Pool = new JedisPool(poolConfig, host, port, timeout);
    }
}
