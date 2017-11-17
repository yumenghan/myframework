package com.myframework.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Menghan Yu
 * @description 类型转换工具类
 * @date 11/17/17 2:52 PM
 */
public final class CastUtil {
    private CastUtil(){}

    public static String castString(Object o){
        return castString(o, "");
    }

    /**
     * obj 转换为 String 类型
     * @param o
     * @param defaultValue 默认值
     * @return
     */
    public static String castString(Object o, String defaultValue) {
        return o != null ? String.valueOf(o) : defaultValue;
    }

    /**
     * obj 转换为 Double 类型
     * @param o 转换对象
     * @param defaultValue 默认值
     * @return
     */
    public static double castDouble(Object o, double defaultValue) {
        double doubleValue = defaultValue;
        if (o != null) {
            String strValue = castString(o);
            if(StringUtils.isNotEmpty(strValue)) {
                try {
                    doubleValue = Double.parseDouble(strValue);
                } catch (NumberFormatException e) {
                    doubleValue = defaultValue;
                }
            }
        }
        return doubleValue;
    }
}
