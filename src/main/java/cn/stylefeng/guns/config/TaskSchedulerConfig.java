package cn.stylefeng.guns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class TaskSchedulerConfig {

	@Bean
	public TaskScheduler taskSchduler(){
		ThreadPoolTaskScheduler taskScheduler=new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(50);
		taskScheduler.setThreadNamePrefix("OrderBatchImport-");
		return taskScheduler;
	}
	
}
