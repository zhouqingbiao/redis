package com.data;

import java.util.ArrayList;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.logger.Logger;
import com.redis.Redis;

import redis.clients.jedis.Jedis;

public class Redis2WebService {

    /**
     * 根据index和rows以及keys返回数据
     *
     * @param keys
     * @param index
     * @param rows
     * @return
     */
    public ArrayList<Object> getData(String keys, int index, int rows) {

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

        // 小于或等于0且不等于-1返回null
        if (rows <= 0 && rows != -1) {
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
