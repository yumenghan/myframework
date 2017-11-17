package com.myframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;


/**
 * @author Menghan Yu
 * @description 配置文件读取工具
 * @date 11/17/17 2:42 PM
 */
public final class PropsUtil {
    private PropsUtil(){}
    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

    /**
     *  加载配置文件
     * @param filename
     * @return
     */
    public static Properties loadProps(String filename){
        Properties properties = null;
        InputStream inputStream = null;
        try{
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            if (inputStream == null) {
                throw new FileNotFoundException(filename + "is not find");
            }
            properties = new Properties();
            properties.load(inputStream);
        } catch (Exception e) {
            LOGGER.error("load properties file failure", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    LOGGER.error("close input stream failure", e);
                }
            }
        }
        return properties;
    }

    /**
     * 获取字符属性
     * @param properties
     * @param key
     * @return
     */
    public static String getString(Properties properties, String key) {
        return getString(properties, key, "");
    }
    /**
     * 获取字符属性， 可指定默认值
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(Properties properties, String key, String defaultValue) {
        String value = defaultValue;
        if (properties.containsKey(key)) {
            value = properties.getProperty(key);
        }
        return value;
    }

}
