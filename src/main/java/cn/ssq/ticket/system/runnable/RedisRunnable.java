package cn.ssq.ticket.system.runnable;

import cn.stylefeng.roses.core.util.SpringContextHolder;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisRunnable implements  Runnable{

    private String key;

    private static RedisTemplate<Object,Object> redisTemplate  = SpringContextHolder.getBean("redisTemplate");

    public RedisRunnable(String key) {
        this.key = key;
    }


    @Override
    public void run() {
        try {
            redisTemplate.opsForValue().decrement(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
