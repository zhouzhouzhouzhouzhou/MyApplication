package com.example.wgcommon;

import android.os.Handler;
import android.os.Looper;


import com.example.log.NLog;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Global executor pools for the whole application.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
public class AppExecutors {

    private final ExecutorService diskIO;

    private final ExecutorService networkIO;

    public final Executor mainThread;
    private ExecutorService cupIO;
    /**定时任务线程池**/
    private final ScheduledExecutorService scheduledExecutor;


    private AppExecutors(ExecutorService diskIO, ExecutorService networkIO, Executor mainThread, ScheduledExecutorService scheduledExecutor) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
        this.scheduledExecutor = scheduledExecutor;
    }

    private AppExecutors() {
        this(createSinglePool(), Executors.newFixedThreadPool(3),
                new MainThreadExecutor(), scheduledThreadPoolExecutor());
        cupIO = createThreadPool();
    }


    private static class SingletonInstance {
        private static final AppExecutors INSTANCE = new AppExecutors();
    }

    public static AppExecutors getInstance() {
        return SingletonInstance.INSTANCE;
    }

    private static ScheduledExecutorService scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(16, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "scheduled_executor");
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                NLog.e("test", "rejectedExecution: scheduled executor queue overflow");
            }
        });
    }

    /**
     * 定时(延时)任务线程池
     *
     * 替代Timer,执行定时任务,延时任务
     */
    public ScheduledExecutorService scheduledExecutor() {
        return scheduledExecutor;
    }


    public static ExecutorService diskIO() {
        return AppExecutors.getInstance().diskIO;
    }

    public static <T> Future<T> diskIOSubmit(Callable<T> runnable) {
        try {
            return AppExecutors.getInstance().diskIO.submit(runnable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这是rxjava执行一个异步任务的基础架子
     */
    public static void rxJavaIOExecute(Runnable runnable) {
        Observable.create(observableEmitter -> runnable.run())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }


    public static void diskIOExecute(Runnable runnable) {
        try {
            AppExecutors.getInstance().diskIO.execute(runnable);
        } catch (Exception e){
            e.printStackTrace();
            NLog.d("AppExecutors", "diskIOExecutev-" + e.getMessage());
        }
    }


    public static <T> Future<T> autoSubmit(Callable<T> runnable) {
        try {
            return autoThread().submit(runnable);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static void autoExecute(Runnable runnable) {
        try {
            autoThread().execute(runnable);
        } catch (Exception e){
            e.printStackTrace();
            NLog.d("AppExecutors", "autoExecute-" + e.getMessage());
        }
    }

    public static <T> Future<T> netIOSubmit(Callable<T> runnable) {
        try {
            return networkIO().submit(runnable);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void netIOExecute(Runnable runnable) {
        try {
            networkIO().execute(runnable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static ExecutorService networkIO() {
        return AppExecutors.getInstance().networkIO;
    }

    public static Executor mainThread() {
        return AppExecutors.getInstance().mainThread;
    }

    private static ExecutorService autoThread() {
        return AppExecutors.getInstance().cupIO;
    }
    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    //最大可用的CPU核数
    public static final int PROCESSORS=Runtime.getRuntime().availableProcessors();
    //线程最大的空闲存活时间，单位为秒
    public static final int KEEPALIVETIME=60;
    //任务缓存队列长度
    public static final int BLOCKINGQUEUE_LENGTH=2500;

    public static ThreadPoolExecutor createThreadPool(){
        return new ThreadPoolExecutor(PROCESSORS * 10,PROCESSORS * 30, KEEPALIVETIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(BLOCKINGQUEUE_LENGTH));
    }

    public static ThreadPoolExecutor createSinglePool(){
        return new ThreadPoolExecutor(1,1,KEEPALIVETIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(BLOCKINGQUEUE_LENGTH));
    }
}
