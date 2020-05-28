package com.jws;

import com.alibaba.fastjson.JSON;
import com.data.Oracle2Redis;
import com.data.Redis2WebService;
import com.logger.Logger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.xml.internal.ws.developer.JAXWSProperties;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

@WebService
public class Jws {

    @Resource
    private WebServiceContext wsContext;

    /**
     * 输出InetSocketAddress、HostAddress和HostName
     */
    private void get() {
        MessageContext mc = wsContext.getMessageContext();
        HttpExchange exchange = (HttpExchange) mc.get(JAXWSProperties.HTTP_EXCHANGE);
        InetSocketAddress isa = exchange.getRemoteAddress();
        Logger.logger.info("InetSocketAddress : " + isa);
    }

    /**
     * 用户名密码校验
     *
     * @param user
     * @param password
     * @return
     */
    private boolean checkUserAndPassword(String user, String password) {

        // 获取Properties数据
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("Jws.properties"));

            // user和password都不能为空
            if (null == user || null == password || "".equals(user) || "".equals(password)) {
                Logger.logger.info("校验失败！");
                return false;
            }

            // 验证成功返回true
            if (password.equals(properties.getProperty(user))) {
                Logger.logger.info("校验成功！");
                return true;
            }
        } catch (IOException e) {
            Logger.logger.warn(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 密码校验并手动提取数据
     *
     * @param password
     * @param index
     * @return
     */
    public boolean manualExtractData(String password, int index) {

        this.get();

        String user = "ZhouQingbiao";
        Logger.logger.info("user:" + user);
        Logger.logger.info("password:" + password);

        // 定义Oracle2Redis
        Oracle2Redis o2R = new Oracle2Redis();

        // 校验密码
        if (checkUserAndPassword(user, password) == true) {
            o2R.addKey(index);
            return true;
        } else {
            Logger.logger.info("密码错误！");
            return false;
        }
    }

    /**
     * @param keys
     * @param index
     * @param rows
     * @return
     */
    public String getData(String keys, int index, int rows) {
        this.get();
        return JSON.toJSONString(new Redis2WebService().getData(keys, index, rows));
    }
}