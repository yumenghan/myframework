package com.myframework.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;

/**
 * @author Menghan Yu
 * @description 集合工具类
 * @date 11/17/17 7:20 PM
 */
public final class CollectionUtil {
    private CollectionUtil(){}

    /**
     * 判断集合是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return CollectionUtils.isEmpty(collection);
    }

    /**
     * 判断集合是否不为空
     * @param collection
     * @return
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return CollectionUtils.isNotEmpty(collection);
    }
}
