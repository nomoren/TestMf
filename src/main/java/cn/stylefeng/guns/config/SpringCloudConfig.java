package cn.stylefeng.guns.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpringCloudConfig {


    @Bean
    @LoadBalanced//服务提供者设置了集群，RestTemplate自动负载均衡，访问集群
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    //Ribbon使用轮训策略，这里可以更改策略
    @Bean
    public IRule myRule(){
        return new RoundRobinRule();
    }




}
