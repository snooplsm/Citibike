package us.wmwm.citibike.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadHelper {

	private static ScheduledExecutorService POOL = Executors
			.newScheduledThreadPool(10);
	static int THREADS = 0;

	public static ScheduledExecutorService getScheduler() {

		if (POOL.isShutdown() || POOL.isTerminated()) {

			POOL = Executors.newScheduledThreadPool(10);
		}
		
		THREADS++;
		//Log.d("ThreadHelper", "thread count :" + THREADS);

		return POOL;

	}

	public static void cleanUp() {

		if (POOL != null && !POOL.isShutdown()) {

			POOL.shutdownNow();

		}

	}
	
	private static PausableThreadPoolExecutor IMAGE_POOL;
	
	public static PausableThreadPoolExecutor getImagePool() {
		if(IMAGE_POOL==null) {
			IMAGE_POOL = new PausableThreadPoolExecutor(10, 10, 60000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
		}
		return IMAGE_POOL;
	}
			
			
	
	public static class PausableThreadPoolExecutor extends ThreadPoolExecutor {
		   private boolean isPaused;
		   private ReentrantLock pauseLock = new ReentrantLock();
		   private Condition unpaused = pauseLock.newCondition();
		   
		   

		   public PausableThreadPoolExecutor(int corePoolSize,
				int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
			// TODO Auto-generated constructor stub
		}

		protected void beforeExecute(Thread t, Runnable r) {
		     super.beforeExecute(t, r);
		     pauseLock.lock();
		     try {
		       while (isPaused) unpaused.await();
		     } catch (InterruptedException ie) {
		       t.interrupt();
		     } finally {
		       pauseLock.unlock();
		     }
		   }

		   public void pause() {
		     pauseLock.lock();
		     try {
		       isPaused = true;
		     } finally {
		       pauseLock.unlock();
		     }
		   }

		   public void resume() {
		     pauseLock.lock();
		     try {
		       isPaused = false;
		       unpaused.signalAll();
		     } finally {
		       pauseLock.unlock();
		     }
		   }
		 }

}