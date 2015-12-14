package com.scienjus.disque.consumer.model;

import java.lang.reflect.Method;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public class ConsumerMethod {

    private String queue;

    private Method method;

    private Object bean;

    public ConsumerMethod(String queue, Method method, Object bean) {
        this.queue = queue;
        this.method = method;
        this.bean = bean;
    }

    public String getQueue() {
        return queue;
    }

    public Method getMethod() {
        return method;
    }

    public Object getBean() {
        return bean;
    }
}
