package com.scienjus.disque.producer.worker;

import com.scienjus.disque.producer.DisqueProducer;
import com.scienjus.disque.producer.annotation.AddJob;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
@Aspect
public class ProducerWorker {

    private DisqueProducer producer;

    public void setProducer(DisqueProducer producer) {
        this.producer = producer;
    }

    @Around("@annotation(addJob)")
    public Object around(ProceedingJoinPoint point, AddJob addJob) {
        Object content = null;
        try {
            content = point.proceed();
            String queue = addJob.queue();
            producer.addJob(queue, content, 0);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return content;
    }

}
