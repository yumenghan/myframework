package com.myframework.helper;

import com.myframework.util.PropsUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Menghan Yu
 * @description 数据库操作
 * @date 11/17/17 8:35 PM
 */
public class DatabaseHelper {

    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    /**
     * 保证线程安全
     */
    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<Connection>();

    /**
     * 数据源
     */
    private static final BasicDataSource DATA_SOURCE;

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);


    static {
        Properties properties = PropsUtil.loadProps("my.properties");
        String driver = properties.getProperty("jdbc.driver");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);

    }

    /**
     * 获取连接
     * @return
     */
    public static Connection getConnection(){
        Connection connection = CONNECTION_HOLDER.get();
        if (connection == null) {
            try {
                connection = DATA_SOURCE.getConnection();
            } catch (SQLException e) {
                LOGGER.error("get connection failure", e);
            }finally {
                CONNECTION_HOLDER.set(connection);
            }
        }
        return connection;
    }



    /**
     * 查询实体列表
     * @param entityClass 实体类
     * @param sql sql语句
     * @param params 查询参数
     * @param <T>
     * @return
     */
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        List<T> entityList = null;
        Connection connection = getConnection();
        try {
            entityList = QUERY_RUNNER.query(connection, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
        }
        return entityList;
    }

    /**
     * 查询单个实体对象
     * @param entityClass
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public static <T> T queryEntity(Class<T> entityClass, String sql, Object ...params) {
        T entity = null;
        Connection connection = getConnection();
        try {
            entity = QUERY_RUNNER.query(connection, sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity failure", e);
        }
        return entity;
    }

    /**
     * 多表关联查询
     * @param sql
     * @param params
     * @return
     */
    public static List<Map<String, Object>> executeQuery(String sql, Object ...params) {
        List<Map<String, Object>> result = null;
        Connection connection = getConnection();
        try {
            result = QUERY_RUNNER.query(connection, sql, new MapListHandler(),params);
        } catch (SQLException e) {
            LOGGER.error("query fail", e);
        }
        return result;
    }

    /**
     * update insert delete
     * @param sql
     * @param params
     * @return
     */
    public static int executeUpdate(String sql, Object ...params) {
        int rows = 0;
        Connection connection = getConnection();
        try {
            rows = QUERY_RUNNER.update(connection, sql, params);
        } catch (SQLException e) {
            LOGGER.error("query fail", e);
        }
        return rows;
    }

    /**
     * 插入实体对象
     * @param entityClass 实体对象class
     * @param fieldMap key为column
     * @param <T>
     * @return
     */
    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
        if (fieldMap.isEmpty()) {
            LOGGER.error("can not insert entity field map is empty");
            return false;
        }

        String sql = "INSERT INTO" + getTableName(entityClass);
        StringBuilder col = new StringBuilder("(");
        StringBuilder val = new StringBuilder("(");

        for(String fieldName : fieldMap.keySet()) {
            col.append(fieldName).append(", ");
            val.append("?, ");
        }
        col.replace(col.lastIndexOf(", "), col.length(), ")");
        val.replace(val.lastIndexOf(", "), val.length(), ")");
        sql += col + "VALUES " + val;
        Object[] params = fieldMap.values().toArray();
        return executeUpdate(sql, params) == 1;
    }

    /**
     * 更新实体
     * @param entityClass
     * @param id 根据id更新实体
     * @param fieldMap
     * @param <T>
     * @return
     */
    public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {
        if (fieldMap.isEmpty()) {
            LOGGER.error("can not update entity");
            return false;
        }

        String sql = "UPDATE" + getTableName(entityClass) + "SET";
        StringBuilder columns = new StringBuilder();
        for (String fielName : fieldMap.keySet()) {
            columns.append(fielName).append("=?, ");
        }
        sql += columns.substring(0, columns.lastIndexOf(", ")) + " WHERE id = ?";

        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();

        return executeUpdate(sql, params) == 1;
    }

    /**
     * 删除实体
     * @param entityClass 实体class
     * @param id 待删除的id
     * @param <T>
     * @return
     */
    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String sql = "DELETE FROM " + getTableName(entityClass) + " WHERE id = ?";
        return executeUpdate(sql, id) == 1;
    }

    public static void close() {
        CONNECTION_HOLDER.remove();

    }
    private static String getTableName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }
}
