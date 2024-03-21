package com.example.utils;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;


public class ViewClickEffect {
    @SuppressLint("ClickableViewAccessibility")
    public static void setAlphaChange(final View...views){
        for (View view : views) {
            view.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setAlpha(0.6f);

                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:

                        v.setAlpha(1.0f);

                        break;

                }

                return false;

            });

        }

    }

    public static void setColorAlphaChange(View view, int defColor, int clickColor, int radius){
        setColorAlphaChange(view, defColor, clickColor, radius, radius, radius, radius, 0, 0, 1);
    }

    /**
     *
     * @param view          点击的view
     * @param defColor      默认颜色
     * @param clickColor    点击颜色
     * @param radius        圆角
     * @param alpha         透明度
     */
    public static void setColorAlphaChange(View view, int defColor, int clickColor, int radius, float alpha){
        setColorAlphaChange(view, defColor, clickColor, radius, radius, radius, radius, 0, 0, alpha);
    }

    public static void setColorAlphaChange(View view, int defColor, int clickColor, int radius, int strokeWidth, int strokeColor, float alpha){
        setColorAlphaChange(view, defColor, clickColor, radius, radius, radius, radius, strokeWidth, strokeColor, alpha);
    }

    /**
     *
     * @param view              点击的view
     * @param defColor          默认颜色
     * @param clickColor        点击颜色
     * @param leftTopRadius     左上角圆角
     * @param rightTopRadius    右上角圆角
     * @param leftBottomRadius  左下角圆角
     * @param rightBottomRadius 右下角圆角
     * @param strokeWidth       边框线宽
     * @param strokeColor       边框颜色
     * @param alpha             透明度
     */
    public static void setColorAlphaChange(View view, int defColor, int clickColor, int leftTopRadius, int rightTopRadius, int leftBottomRadius, int rightBottomRadius, int strokeWidth, int strokeColor, float alpha){
        GradientDrawable defDrawable = new GradientDrawable();
        defDrawable.setCornerRadii(new float[]{
                ScreenUtils.dip2px(view.getContext(), leftTopRadius), ScreenUtils.dip2px(view.getContext(), leftTopRadius),
                ScreenUtils.dip2px(view.getContext(), rightTopRadius), ScreenUtils.dip2px(view.getContext(), rightTopRadius),
                ScreenUtils.dip2px(view.getContext(), rightBottomRadius), ScreenUtils.dip2px(view.getContext(), rightBottomRadius),
                ScreenUtils.dip2px(view.getContext(), leftBottomRadius), ScreenUtils.dip2px(view.getContext(), leftBottomRadius)});
        defDrawable.setColor(defColor);
        if(strokeWidth > 0) {
            defDrawable.setStroke(strokeWidth, strokeColor);
        }


        GradientDrawable clickDrawable = new GradientDrawable();
        clickDrawable.setCornerRadii(new float[]{
                ScreenUtils.dip2px(view.getContext(), leftTopRadius), ScreenUtils.dip2px(view.getContext(), leftTopRadius),
                ScreenUtils.dip2px(view.getContext(), rightTopRadius), ScreenUtils.dip2px(view.getContext(), rightTopRadius),
                ScreenUtils.dip2px(view.getContext(), rightBottomRadius), ScreenUtils.dip2px(view.getContext(), rightBottomRadius),
                ScreenUtils.dip2px(view.getContext(), leftBottomRadius), ScreenUtils.dip2px(view.getContext(), leftBottomRadius)});
        if(strokeWidth > 0) {
            clickDrawable.setStroke(strokeWidth, strokeColor);
        }
        clickDrawable.setColor(clickColor);

        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackground(clickDrawable);
                    v.setAlpha(alpha);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setBackground(defDrawable);
                    v.setAlpha(1);
                    break;

            }

            return false;

        });

    }

    public static void setAlphaChange(final View view, float alpha){
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    v.setAlpha(alpha);

                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

                    v.setAlpha(1.0f);

                    break;

            }

            return false;

        });

    }
}
