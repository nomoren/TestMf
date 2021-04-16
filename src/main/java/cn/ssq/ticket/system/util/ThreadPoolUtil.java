package cn.ssq.ticket.system.util;

import java.util.concurrent.*;

public class ThreadPoolUtil {

	public static ThreadPoolExecutor threadPool = getThreadPool();

	/**
	 * 无返回值直接执行
	 * @param runnable
	 */
	public  static void execute(Runnable runnable){
        threadPool.execute(runnable);
	}

	/**
	 * 返回值直接执行
	 * @param callable
	 */
	public  static <T> Future<T> submit(Callable<T> callable){
		return  threadPool.submit(callable);
	}


	/**
	 * dcs获取线程池
	 * @return 线程池对象
	 */
	public static ThreadPoolExecutor getThreadPool() {

        return  new ThreadPoolExecutor(10, 50, 1, TimeUnit.HOURS,
                new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
		/*if (threadPool != null) {
			return threadPool;
		} else {
			synchronized (ThreadPoolUtil.class) {
				if (threadPool == null) {

				}
				return threadPool;
			}
		}*/
	}

}
