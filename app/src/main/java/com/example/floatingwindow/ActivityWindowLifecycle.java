package com.example.floatingwindow;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

final class ActivityWindowLifecycle implements Application.ActivityLifecycleCallbacks {

    private Activity mActivity;
    private FloatWindow<?> mWindow;

    ActivityWindowLifecycle(FloatWindow<?> window, Activity activity) {
        mActivity = activity;
        mWindow = window;
    }

    public void setWindow(FloatWindow<?> mWindow) {
        this.mWindow = mWindow;
    }

    /**
     * 注册监听
     */
    void register() {
        if (mActivity == null) {
            return;
        }
        mActivity.getApplication().registerActivityLifecycleCallbacks(this);
    }

    /**
     * 取消监听
     */
    void unregister() {
        if (mActivity == null) {
            return;
        }
        mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
//        boolean isGoodsEditActivity = Shadow.getService(ICommonService.class).getShareActivityClassName().equals(activity.getClass().getName());
//        // 商品编辑页不显示弹窗
//        if(mWindow != null && mWindow.getDuration() > 0 && !isGoodsEditActivity){
//            FloatingViewToast.show(activity, (ToastConfigBean) mWindow.getParams());
//        }
//        // 进入编辑页关闭底部弹窗
//        if(isGoodsEditActivity && mWindow != null){
//            mWindow.cancel();
//        }

        if(mWindow != null){
            mWindow.cancel();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e("Lifecycle==>", "onActivityPaused");
        // 一定要在 onPaused 方法中销毁掉，如果放在 onDestroyed 方法中还是有一定几率会导致内存泄露
        if (mActivity != activity) {
            return;
        }
        if(mWindow != null){
            mWindow.cancel();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (mActivity != activity) {
            return;
        }
        // 释放 Activity 的引用
        mActivity = null;
        if (mWindow == null) {
            return;
        }
        mWindow.recycle();
        mWindow = null;
    }
}
