package com.example.floatingwindow;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

final class ViewTouchWrapper implements View.OnTouchListener {

    private final FloatWindow<?> mWindow;
    private final FloatWindow.OnTouchListener mListener;

    ViewTouchWrapper(FloatWindow<?> window, FloatWindow.OnTouchListener listener) {
        mWindow = window;
        mListener = listener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("unchecked")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (mListener == null) {
            return false;
        }
        return mListener.onTouch(mWindow, view, event);
    }
}
