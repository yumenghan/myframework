package com.myframework.helper;

import com.myframework.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Menghan Yu
 * @description Bean管理，调用ClassHelper 类的getBeanClassSet方法，循环调用ReflectionUtil的newInstance方法，根据
 * 类来实例化对象，最后将创建好的对象放在一个静态的Map<Class<?>, Object> 中，通过key获取bean对象
 * @date 12/19/17 9:02 PM
 */
public class BeanHelper {
    private static final Map<Class<?>, Object> BEAN_MAP = new HashMap<Class<?>, Object>(16);

    static {
        Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
        for (Class<?> beanClass : beanClassSet) {
            Object obj = ReflectionUtil.newInstance(beanClass);
            BEAN_MAP.put(beanClass, obj);
        }
    }

    public static Map<Class<?>, Object> getBeanMap(){
        return BEAN_MAP;
    }

    /**
     * 获取bean 实例
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> cls) {
        if (!BEAN_MAP.containsKey(cls)) {
            throw new RuntimeException("can not find class:" + cls);
        }
        return (T) BEAN_MAP.get(cls);
    }

}
