package com.jws;

import com.job.Job;
import com.logger.Logger;

import javax.xml.ws.Endpoint;

/**
 * 定时任务启动并发布WebService
 *
 * @author QB.Zhou
 */
public class Publish {

    public static void main(String[] args) {

        // 启动定时任务0 0 0/6 * * ?
        Job.start();

        // IP地址
        String address = "172.16.100.51";

        // 组装WebService地址
        address = "http://" + address + "/Jws";

        // 发布WebService
        Object implementor = new Jws();
        Endpoint.publish(address, implementor);

        // 输出WebService地址及如何解析操作
        Logger.logger.info(address + "?wsdl");
        Logger.logger.info("wsimport -keep " + address + "?wsdl");

    }
}