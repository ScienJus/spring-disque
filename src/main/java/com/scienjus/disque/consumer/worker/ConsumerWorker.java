package com.scienjus.disque.consumer.worker;


import com.github.xetorthio.jedisque.Job;
import com.scienjus.disque.consumer.DisqueConsumer;
import com.scienjus.disque.consumer.model.ConsumerMethod;
import com.scienjus.disque.util.SerializeUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public class ConsumerWorker implements Runnable {

    private ConsumerMethod method;

    private DisqueConsumer consumer;

    public ConsumerWorker(ConsumerMethod method, DisqueConsumer consumer) {
        this.method = method;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        Object bean = method.getBean();
        Method jobMethod = method.getMethod();
        String queue = method.getQueue();
        //获取消息
        Job job;
        while ((job = consumer.getJob(queue)) != null) {
            try {
                System.out.println(job.getId());
                boolean isSuccess;
                if (method.getMethod().getReturnType().isAssignableFrom(Boolean.TYPE)) {
                    isSuccess = (boolean) jobMethod.invoke(bean, SerializeUtil.unserialize(job.getBodyAsBytes()));
                } else {
                    jobMethod.invoke(bean, SerializeUtil.unserialize(job.getBodyAsBytes()));
                    isSuccess = true;
                }
                if (isSuccess) {
                    consumer.ackJob(job);
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
