package com.example.floatingwindow;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

public class MovingDraggable extends BaseDraggable {

    /** 手指按下的坐标 */
    private float mViewDownX;
    private float mViewDownY;

    /** 触摸移动标记 */
    private boolean mMoveTouch;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下的位置（相对 View 的坐标）
                mViewDownX = event.getX();
                mViewDownY = event.getY();
                mMoveTouch = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 记录移动的位置（相对屏幕的坐标）
                float rawMoveX = event.getRawX() - getWindowInvisibleWidth();
                float rawMoveY = event.getRawY() - getWindowInvisibleHeight();

                float newX = rawMoveX - mViewDownX;
                float newY = rawMoveY - mViewDownY;
                if (newX < 0) {
                    newX = 0;
                }
                if (newY < 0) {
                    newY = 0;
                }

                // 更新移动的位置
                updateLocation(newX, newY);

                if (!mMoveTouch && isFingerMove(mViewDownX, event.getX(), mViewDownY, event.getY())) {
                    // 如果用户移动了手指，那么就拦截本次触摸事件，从而不让点击事件生效
                    mMoveTouch = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return mMoveTouch;
            default:
                break;
        }
        return false;
    }
}
