package com.example.wgcommon;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AppInitManager {
    private AppInitManager(){
    }
    private static class SingleTonHolder {
        private static final AppInitManager INSTANCE = new AppInitManager();
    }

    public static AppInitManager getInstance(){
        return SingleTonHolder.INSTANCE;
    }

    private List<IAppInitTask> tasks = new ArrayList<>();

    public AppInitManager setTasks(List<IAppInitTask> tasks) {
        this.tasks = tasks;
        return this;
    }

    public AppInitManager addTask(IAppInitTask task) {
        tasks.add(task);
        return this;
    }

    public void init(final Application app){
        final CountDownLatch latch = new CountDownLatch(tasks.size());
        long ss = System.currentTimeMillis();
        for (final IAppInitTask task : tasks) {
            new Thread(() -> {
                long s = System.currentTimeMillis();
                task.onInit(app);
                latch.countDown();
                long e = System.currentTimeMillis();
                Log.i("AppInitManager", "AppInitManager init --" + (e - s));
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("AppInitManager init all --" + (System.currentTimeMillis()-ss));
    }


    public interface InitCallback{
        void finish();
    }
}
