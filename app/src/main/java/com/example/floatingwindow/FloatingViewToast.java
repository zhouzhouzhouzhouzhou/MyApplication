package com.example.floatingwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.CommonUtils;
import com.example.base.BaseActivity;
import com.example.log.NLog;
import com.example.utils.ViewClickEffect;
import com.example.utils.ViewSizeUtil;
import com.example.wgapplication.R;


/**
 * 自定义悬浮可点击Toast组件
 */
public class FloatingViewToast {
    private static final String TAG = "浮窗";
    public static void show(Activity activity, ToastConfigBean bean) {
        try{
            NLog.i(TAG, "show:==> "+ activity.getClass().getSimpleName());
            if(activity == null || activity.isFinishing() || bean == null || TextUtils.isEmpty(bean.getTitle())){
                return;
            }
            NLog.i(TAG, "show:==>2 "+ activity.getClass().getSimpleName());
            FloatWindow floatWindow = FloatWindow.with(activity)
                .setParams(bean)
                .setContentView(R.layout.layout_toast_hint)
                .setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .setYOffset(CommonUtils.dip2px(activity, 64))
                .setAnimStyle(R.style.ToastAnimation) // 设置动画样式
                .setDuration(bean.getToastTime()) // 设置显示时长
                .setImageUrl(R.id.iv_icon, bean.getImageIcon())
                .setText(R.id.tv_title, bean.getTitle());
            LinearLayout llButtonView = (LinearLayout) floatWindow.getViewGroup(R.id.ll_button_view);
            llButtonView.removeAllViews();
            for (ToastConfigBean.ButtonBean b : bean.getButtons()) {
                if(!TextUtils.isEmpty(b.getButtonType())){
                    llButtonView.addView(getViewByType(activity, floatWindow, bean, b, b.getButtonType()));
                }else{
                    llButtonView.addView(getViewByType(activity, floatWindow, bean, b, ToastConfigBean.TYPE_TITLE_BUTTON));
                }
            }
            floatWindow.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 设置悬浮Toast组件点击按钮样式
     * @param context 悬浮页面
     * @param floatWindow 悬浮Toast组件
     * @param button 按钮配置
     * @param buttonType 按钮样式
     */
    private static View getViewByType(Context context, FloatWindow floatWindow, ToastConfigBean bean, ToastConfigBean.ButtonBean button, String buttonType) {
        String title = bean.getTitle();
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                CommonUtils.dip2px(context, 24));
        param.setMargins(CommonUtils.dip2px(context, 8), 0, 0, 0);
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setLayoutParams(param);
        textView.setText(button.getButtonTitle());
        if(!TextUtils.isEmpty(button.getJumpHtmlUrl()) && button.getJumpHtmlUrl().contains("ime/guide1")){
        }
        if (button.getButtonTitle().contains("查看下架商品")) {
        }
        switch (buttonType){
            case ToastConfigBean.TYPE_COLOR_BUTTON:
                textView.setPadding(CommonUtils.dip2px(context, 12), 0, CommonUtils.dip2px(context, 12), 0);
                GradientDrawable background = new GradientDrawable();
                background.setCornerRadius(CommonUtils.dip2px(context, 12));
                int color = TextUtils.isEmpty(button.getButtonColor()) ? ContextCompat.getColor(context, R.color._49C167)
                        : Color.parseColor(button.getButtonColor());
                background.setColor(color);
                textView.setBackground(background);
                textView.setTextColor(ContextCompat.getColor(context, R.color.white));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                //textView.setTypeface(Typeface.DEFAULT_BOLD);
                ViewClickEffect.setAlphaChange(textView);
                break;
            case ToastConfigBean.TYPE_TITLE_BUTTON:
                textView.setPadding(CommonUtils.dip2px(context, 4), 0, CommonUtils.dip2px(context, 4), 0);
                int textColor = TextUtils.isEmpty(button.getButtonColor()) ? ContextCompat.getColor(context, R.color._49C167)
                        : Color.parseColor(button.getButtonColor());
                textView.setTextColor(textColor);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                //textView.setTypeface(Typeface.DEFAULT_BOLD);
                int clickColor = getTransparentColor(textColor, 0.2f);
                ViewClickEffect.setColorAlphaChange(textView, ContextCompat.getColor(context, R.color.trans), clickColor, 4);
                break;
            case ToastConfigBean.TYPE_ARROW_BUTTON:
                if(TextUtils.isEmpty(button.getButtonTitle())){
                    textView.setText("去看看");
                }
                if(TextUtils.isEmpty(button.getButtonColor())){
                    textView.setTextColor(ContextCompat.getColor(context, R.color._969AA0));
                }else{
                    textView.setTextColor(Color.parseColor(button.getButtonColor()));
                }
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_right_gray_16);
                drawable.setBounds(0, 0, ViewSizeUtil.getCustomDimen(16), ViewSizeUtil.getCustomDimen(16));
                textView.setCompoundDrawables(null, null, drawable, null);
                ViewClickEffect.setAlphaChange(textView);
                break;
        }
        textView.setOnClickListener(v -> {   });
        return textView;
    }



    private static int getTransparentColor(int color, float f){
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        alpha *= f;
        return Color.argb(alpha, red, green, blue);
    }
}
