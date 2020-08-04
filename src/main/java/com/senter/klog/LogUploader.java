package com.senter.klog;

import android.os.AsyncTask;
import android.os.SystemClock;


import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在一个独立的线程里，把要上传的log放到队列里，pop最新的来提效，控制提交线程.
 */
public class LogUploader {

    private final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
    private final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTaskLogUp#" + mCount.getAndIncrement());
        }
    };
    /*0~2个线程，存活最多30秒*/
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 2, 30,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    private final ArrayDeque<String> sLogDeque = new ArrayDeque<>();
    private Thread sTaskMakeThread = new LogMergeThread();

    public static LogUploader sLogUploader;

    public static LogUploader getInstance() {
        if (sLogUploader == null) {
            synchronized (LogUploader.class) {
                if (sLogUploader == null) {
                    sLogUploader = new LogUploader();
                }
            }
        }
        return sLogUploader;
    }

    private LogUploader() {
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        sTaskMakeThread.start();
    }

    public void addLog(String log) {
        sLogDeque.add(log);
        if (sLogDeque.size() > 1024) {
            sLogDeque.clear();
        }
    }

    private class LogMergeThread extends Thread {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            final int MAX_LEN = 100 * 1024;//一般最多按MAX_LEN来上传，单条log最大小于MAX_LEN的2倍
            while (true) {
                if (sLogDeque.peek() == null) {
                    SystemClock.sleep(1000);//没有待上传的log时等待
                } else {
                    StringBuilder sb = new StringBuilder();
                    do {
                        String nextLog = sLogDeque.peek();
                        if (sb.length() == 0) {//先取一个log
                            sb.append(nextLog);
                            sLogDeque.poll();
                        } else {
                            if ( null!=nextLog && nextLog.length()>0){
                                if (sb.length() + nextLog.length() <= MAX_LEN) {
                                    //拼接队列中下一个待上传的log，超过MAX_LEN不拼接。
                                    sb.append(nextLog);
                                    sLogDeque.poll();
                                } else {
                                    break;
                                }
                            }else {
                                break;
                            }
                        }
                    } while (sLogDeque.size() > 0);

                    String tmp = sb.toString();
                    // 新的异步线程中上传log.
                    try {
                        while (tmp.length() >= MAX_LEN * 2) {
                            //对单个LOG大于 MAX_LEN×2 的按MAX_LEN拆分
                            new UpLogTask().executeOnExecutor(threadPoolExecutor, tmp.substring(0, MAX_LEN));
                            tmp = tmp.substring(MAX_LEN);
                        }
                        if (tmp.length() > 0) {
                            new UpLogTask().executeOnExecutor(threadPoolExecutor, tmp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sb.delete(0, sb.length());
                    //有待上传的log时等待
                    SystemClock.sleep(1000);
                }
            }
        }
    }

   private class UpLogTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            LogModel.getInstance().addLog(LogCtrl.getDeviceId(), "<br/>" + (String) objects[0]);
            return null;
        }
    }
}
