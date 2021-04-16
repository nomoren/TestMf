package cn.ssq.ticket.system.util;

import java.util.concurrent.*;

public class OrderThreadPoolUtil {

	public static ThreadPoolExecutor threadPool = getThreadPool();

	/**
	 * 无返回值直接执行
	 * @param runnable
	 */
	public  static void execute(Runnable runnable){
        threadPool.execute(runnable);
	}




	/**
	 * dcs获取线程池
	 * @return 线程池对象
	 */
	public static ThreadPoolExecutor getThreadPool() {

        return  new ThreadPoolExecutor(15, 50, 1, TimeUnit.HOURS,
                new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());

	}

}
