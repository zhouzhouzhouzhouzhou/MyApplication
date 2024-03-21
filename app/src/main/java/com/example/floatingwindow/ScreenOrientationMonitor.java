package com.example.floatingwindow;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;

final class ScreenOrientationMonitor implements ComponentCallbacks {

    /** 当前屏幕的方向 */
    private int mScreenOrientation;

    /** 屏幕旋转回调 */
    private OnScreenOrientationCallback mCallback;

    public ScreenOrientationMonitor(Configuration configuration) {
        mScreenOrientation = configuration.orientation;
    }

    /**
     * 注册监听
     */
    void registerCallback(Context context, OnScreenOrientationCallback callback) {
        context.getApplicationContext().registerComponentCallbacks(this);
        mCallback = callback;
    }

    /**
     * 取消监听
     */
    void unregisterCallback(Context context) {
        context.getApplicationContext().unregisterComponentCallbacks(this);
        mCallback = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mScreenOrientation == newConfig.orientation) {
            return;
        }
        mScreenOrientation = newConfig.orientation;

        if (mCallback == null) {
            return;
        }
        mCallback.onScreenOrientationChange(mScreenOrientation);
    }

    @Override
    public void onLowMemory() {
        // default implementation ignored
    }

    /**
     * 屏幕方向监听器
     */
    interface OnScreenOrientationCallback {

        /**
         * 监听屏幕旋转了
         *
         * @param newOrientation         最新的屏幕方向
         */
        default void onScreenOrientationChange(int newOrientation) {}
    }
}
