package com.example.floatingwindow;

import android.view.View;

final class ViewLongClickWrapper implements View.OnLongClickListener {

    private final FloatWindow<?> mWindow;
    private final FloatWindow.OnLongClickListener mListener;

    ViewLongClickWrapper(FloatWindow<?> window, FloatWindow.OnLongClickListener listener) {
        mWindow = window;
        mListener = listener;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean onLongClick(View view) {
        if (mListener == null) {
            return false;
        }
        return mListener.onLongClick(mWindow, view);
    }
}
