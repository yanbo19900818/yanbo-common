package com.yanbo.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自增线程池，为了高并发下降低延迟，采用一个监听器不断调整线程池内线程数目
 * 
 * @author yanbo
 *
 */
public class AutoIncreaseThreadPool {
    /**
     * 核心线程数
     */
    private int coreThread = 10;
    /**
     * 线程存活时间
     */
    private long aliveTime = 60;
    /**
     * 线程存活时间单位
     */
    private TimeUnit aliveTimeUnit = TimeUnit.SECONDS;
    /**
     * 线程增加的步长
     */
    private int step = 10;
    /**
     * 线程池队列
     */
    private LinkedBlockingQueue<Runnable> queue;
    /**
     * 核心线程池
     */
    private ThreadPoolExecutor executor;
    /**
     * 控制核心线程数的监听线程
     */
    private Thread listenThread;

    public AutoIncreaseThreadPool(int coreThread, long aliveTime, TimeUnit aliveTimeUnit, int step) {
        CommonAutoIncreaseThreadPoolMonitor commonAutoIncreaseThreadPoolMonitor = new CommonAutoIncreaseThreadPoolMonitor(
                100, TimeUnit.MILLISECONDS, 100, 0, this);
        init(coreThread, aliveTime, aliveTimeUnit, step, commonAutoIncreaseThreadPoolMonitor);

    }

    public AutoIncreaseThreadPool(int coreThread, long aliveTime, TimeUnit aliveTimeUnit, int step,
            CommonAutoIncreaseThreadPoolMonitor commonAutoIncreaseThreadPoolMonitor) {
        init(coreThread, aliveTime, aliveTimeUnit, step, commonAutoIncreaseThreadPoolMonitor);
    }

    private void init(int coreThread, long aliveTime, TimeUnit aliveTimeUnit, int step,
            CommonAutoIncreaseThreadPoolMonitor commonAutoIncreaseThreadPoolMonitor) {
        this.aliveTimeUnit = aliveTimeUnit;
        this.coreThread = coreThread;
        this.aliveTime = aliveTime;
        this.step = step;
        queue = new LinkedBlockingQueue<>();
        executor = new ThreadPoolExecutor(coreThread, Integer.MAX_VALUE, aliveTime, aliveTimeUnit, queue);
        listenThread = new Thread(commonAutoIncreaseThreadPoolMonitor);
        listenThread.start();
    }

    public int getCoreThread() {
        return coreThread;
    }

    public void setCoreThread(int coreThread) {
        this.coreThread = coreThread;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public LinkedBlockingQueue<Runnable> getQueue() {
        return queue;
    }

    public void setQueue(LinkedBlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }
}
