package com.scienjus.disque.consumer;

import com.github.xetorthio.jedisque.Jedisque;
import com.github.xetorthio.jedisque.Job;

import java.util.List;

/**
 * @author XieEnlong
 * @date 2015/12/14.
 */
public class DisqueConsumer {

    private Jedisque jedisque;

    public void setJedisque(Jedisque jedisque) {
        this.jedisque = jedisque;
    }

    public Job getJob(String queue) {
        List<Job> jobs = jedisque.getJob(queue);
        if (jobs != null && !jobs.isEmpty()) {
            return jobs.get(0);
        }
        return null;
    }

    public void ackJob(Job job) {
        jedisque.ackjob(job.getId());
    }
}
