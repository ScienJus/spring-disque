package com.scienjus.disque.consumer.factory;

import com.scienjus.disque.consumer.DisqueConsumer;
import com.scienjus.disque.consumer.annotation.Consumer;
import com.scienjus.disque.consumer.annotation.GetJob;
import com.scienjus.disque.consumer.model.ConsumerMethod;
import com.scienjus.disque.consumer.worker.ConsumerWorker;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public class SchedulerBeanFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private DisqueConsumer consumer;

    private ScheduledExecutorService scheduled;

    public void setConsumer(DisqueConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void init() {
        //获得所有消费者Bean
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Consumer.class);
        //保存worker
        List<ConsumerWorker> workers = new ArrayList<>();

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            String name = entry.getKey();
            Object bean = entry.getValue();
            Class clazz = applicationContext.getType(name);
            for (Method method : clazz.getMethods()) {
                GetJob getJob = method.getAnnotation(GetJob.class);
                if (getJob != null) {
                    ConsumerMethod consumerMethod = new ConsumerMethod(getJob.queue(), method, bean);
                    ConsumerWorker worker = new ConsumerWorker(consumerMethod, consumer);
                    workers.add(worker);
                }
            }
        }
        //注册Scheduler
        scheduled = Executors.newScheduledThreadPool(workers.size());
        for (ConsumerWorker worker : workers) {
            scheduled.scheduleWithFixedDelay(worker, 10, 2, TimeUnit.SECONDS);
        }
    }

    private void destroy() {
        scheduled.shutdown();
    }

}
