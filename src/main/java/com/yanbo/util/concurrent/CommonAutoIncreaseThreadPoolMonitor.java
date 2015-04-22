package com.yanbo.util.concurrent;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class CommonAutoIncreaseThreadPoolMonitor implements Runnable {
    Logger logger = Logger.getLogger(CommonAutoIncreaseThreadPoolMonitor.class);
    /**
     * 间隔时间，单位毫秒
     */
    private int intervalTime = 100;
    /**
     * 间隔时间单位
     */
    private TimeUnit intervalTimeUnit = TimeUnit.MILLISECONDS;
    /**
     * 达到间隔时间次数
     */
    private int intervalNum = 100;
    /**
     * 队列堆积阈值，达到就增加核心线程数
     */
    private int limit = 100;
    private int num = 0;
    private AutoIncreaseThreadPool autoIncreaseThreadPool;

    public CommonAutoIncreaseThreadPoolMonitor(int intervalTime, TimeUnit intervalTimeUnit, int intervalNum, int limit,
            AutoIncreaseThreadPool autoIncreaseThreadPool) {
        if (limit == 0)
            limit = 1;
        this.limit = limit;
        this.intervalTime = intervalTime;
        this.intervalTimeUnit = intervalTimeUnit;
        this.intervalNum = intervalNum;
        this.autoIncreaseThreadPool = autoIncreaseThreadPool;
    }

    @Override
    public void run() {
        while (true) {
            if (autoIncreaseThreadPool.getQueue().size() >= limit) {
                num = 0;
                autoIncreaseThreadPool.setCoreThread(autoIncreaseThreadPool.getCoreThread()
                        + autoIncreaseThreadPool.getStep());
                autoIncreaseThreadPool.getExecutor().setCorePoolSize(autoIncreaseThreadPool.getCoreThread());
            } else {
                if (autoIncreaseThreadPool.getQueue().size() == 0) {
                    num++;
                }
                if (num >= intervalNum) {
                    num = 0;
                    if (autoIncreaseThreadPool.getCoreThread() > autoIncreaseThreadPool.getStep()) {
                        autoIncreaseThreadPool.setCoreThread(autoIncreaseThreadPool.getCoreThread()
                                - autoIncreaseThreadPool.getStep());
                        autoIncreaseThreadPool.getExecutor().setCorePoolSize(autoIncreaseThreadPool.getCoreThread());
                    }
                }
            }
            try {
                intervalTimeUnit.sleep(intervalTime);
            } catch (InterruptedException e) {
                logger.warn(e);
            }
        }

    }
}
