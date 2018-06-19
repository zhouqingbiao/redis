package com.test;

import java.util.ArrayList;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.logger.Logger;
import com.redis.Redis;

import redis.clients.jedis.Jedis;

public class TestRedis {

    public static void main(String[] args) {
        // Redis开始时间
        long startTime4Redis = System.nanoTime();

        String keys = "*濮家*13*";

        int index = 0;

        int rows = 1000;

        new TestRedis().testRedis(keys, index, rows);

        // Redis停止时间
        long stopTime4Redis = System.nanoTime();

        Logger.logger.info("查询关键字：" + keys);

        Logger.logger.info("开始时间（Redis）：" + startTime4Redis);
        Logger.logger.info("停止时间（Redis）：" + stopTime4Redis);

        // Redis总时间消耗
        Logger.logger.info("运行时间（Redis）：" + (stopTime4Redis - startTime4Redis) + "纳秒");
        Logger.logger.info("运行时间（Redis）：" + (stopTime4Redis - startTime4Redis) / 1000000 + "毫秒");
        Logger.logger.info("运行时间（Redis）：" + (stopTime4Redis - startTime4Redis) / 1000000000 + "秒");
    }

    public ArrayList<Object> testRedis(String keys, int index, int rows) {

        // 过滤垃圾信息

        Logger.logger.info("keys=" + keys);
        Logger.logger.info("index=" + index);
        Logger.logger.info("rows=" + rows);

        // keys不能为空。
        if (null == keys || "".equals(keys)) {
            Logger.logger.info("keys不能为空。");
            return null;
        }

        // 小于0返回null
        if (index < 0) {
            Logger.logger.info("index不能小于零。");
            return null;
        }

        // 小于或等于0返回null
        if (rows <= 0) {
            Logger.logger.info("rows不能小于或等于零。");
            return null;
        }

        // 获得Jedis
        try (Jedis jedis = Redis.Pool.getResource()) {

            // 判断是否连接成功
            if (("PONG").equals(jedis.ping())) {
                Logger.logger.info("Redis连接成功！");
            } else {
                Logger.logger.info("Redis连接失败！");
                return null;
            }

            // 选择数据库
            jedis.select(index);
            Logger.logger.info("选择" + index + "号数据库。");

            // 查询数据库
            Set<String> set = jedis.keys(keys);

            // 无数据量返回null
            if (set.isEmpty()) {
                Logger.logger.info("查询结果为空！");
                return null;
            }

            // 定义结果List
            ArrayList<Object> resultList = new ArrayList<Object>();

            for (String key : set) {
                // 获取RedisList
                for (String result : jedis.lrange(key, 0, jedis.llen(key) - 1)) {
                    resultList.add(JSON.parse(result));
                    // 如果条数达到rows则返回List
                    if (resultList.size() == rows) {
                        return resultList;
                    }
                }
            }
            return resultList;
        }

    }
}
