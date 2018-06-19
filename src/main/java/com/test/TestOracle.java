package com.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.logger.Logger;
import com.oracle.Oracle;

public class TestOracle {

    public static void main(String[] args) {
        // OracleLike开始时间
        long startTime4OracleOfLike = System.nanoTime();

        String fwzl_like = "濮家 13";

        new TestOracle().testOracle(fwzl_like);

        // OracleLike停止时间
        long stopTime4OracleOfLike = System.nanoTime();

        Logger.logger.info("查询关键字：" + fwzl_like);

        Logger.logger.info("开始时间（OracleLike）：" + startTime4OracleOfLike);
        Logger.logger.info("停止时间（OracleLike）：" + stopTime4OracleOfLike);

        // Oracle总时间消耗
        Logger.logger.info("运行时间（OracleLike）：" + (stopTime4OracleOfLike - startTime4OracleOfLike) + "纳秒");
        Logger.logger.info("运行时间（OracleLike）：" + (stopTime4OracleOfLike - startTime4OracleOfLike) / 1000000 + "毫秒");
        Logger.logger.info("运行时间（OracleLike）：" + (stopTime4OracleOfLike - startTime4OracleOfLike) / 1000000000 + "秒");
    }

    public List<Map<String, String>> testOracle(String fwzl_like) {

        // 定义Oralce并获取连接
        Oracle jracle = new Oracle();
        Connection connection = jracle.getConnection();

        // 获取fwzl信息
        StringBuffer sql = new StringBuffer(
                "SELECT T.FWCODE, T.ID, T.FWZL FROM HZ_GIS.TPS_FW T WHERE T.LSBZ = 0 AND T.FWZL IS NOT NULL AND T.FWSMZQ = 1201");

        // 去除头尾空格
        fwzl_like = fwzl_like.trim();

        // 如果有空格则执行分割组装
        if (fwzl_like.indexOf(" ") != -1) {
            String[] string_fwzl = fwzl_like.split(" ");
            for (int i = 0; i < string_fwzl.length; i++) {
                sql.append(" AND T.FWZL LIKE ?");
            }
        }

        PreparedStatement preparedStatement = jracle.getPreparedStatement(connection, sql.toString());

        // SQL赋值
        if (fwzl_like.indexOf(" ") != -1) {
            String[] string_fwzl = fwzl_like.split(" ");
            for (int i = 0; i < string_fwzl.length; i++) {
                try {
                    preparedStatement.setString(i + 1, "%" + string_fwzl[i] + "%");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        ResultSet resultSet = jracle.getResultSet(preparedStatement);

        // 数据存入Map
        Map<String, String> map = null;
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            while (resultSet.next()) {
                map = new HashMap<String, String>();
                map.put(resultSet.getMetaData().getColumnName(1), resultSet.getString(1));
                map.put(resultSet.getMetaData().getColumnName(2), resultSet.getString(2));
                map.put(resultSet.getMetaData().getColumnName(3), resultSet.getString(3));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            jracle.close(resultSet, preparedStatement, connection);
        }
        return list;
    }
}
