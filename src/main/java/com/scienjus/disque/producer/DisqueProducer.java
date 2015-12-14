package com.scienjus.disque.producer;

import com.github.xetorthio.jedisque.Jedisque;
import com.scienjus.disque.util.SerializeUtil;

/**
 * @author XieEnlong
 * @date 2015/12/14.
 */
public class DisqueProducer {

    private Jedisque jedisque;

    public void setJedisque(Jedisque jedisque) {
        this.jedisque = jedisque;
    }

    public void addJob(String queue, Object job, long mstimeout) {
        jedisque.addJob(queue.getBytes(), SerializeUtil.serialize(job), mstimeout);
    }
}
