package cn;

import cn.stylefeng.roses.core.config.WebAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * SpringBoot方式启动类
 */
@ImportResource({"classpath:applicationContext.xml"})
@SpringBootApplication(exclude = {WebAutoConfiguration.class})
@EnableTransactionManagement
@EnableCaching
@EnableHystrix
@EnableEurekaClient
@EnableFeignClients(basePackages = "cn.ssq.ticket.system.service")
//@EnableScheduling
public class GunsApplication{
	private final static Logger logger = LoggerFactory.getLogger(GunsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GunsApplication.class, args);
        logger.info(GunsApplication.class.getSimpleName() + "  MF is success!");
    }

}
