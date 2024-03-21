package com.example.floatingwindow;

import android.view.View;

final class ViewClickWrapper implements View.OnClickListener {

    private final FloatWindow<?> mWindow;
    private final FloatWindow.OnClickListener mListener;

    ViewClickWrapper(FloatWindow<?> window, FloatWindow.OnClickListener listener) {
        mWindow = window;
        mListener = listener;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onClick(View view) {
        if (mListener == null) {
            return;
        }
        mListener.onClick(mWindow, view);
    }
}
