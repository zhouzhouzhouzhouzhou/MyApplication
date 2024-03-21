package com.example.toast;

import android.os.Handler;


import com.example.base.BaseActivity;
import com.example.floatingwindow.FloatingViewToast;
import com.example.floatingwindow.ToastConfigBean;
import com.example.log.NLog;
import com.example.wgapplication.ToastTestActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义 toast 管理类
 */
public class CustomToastManager {
    private static final String TAG = "浮窗";
    private volatile static CustomToastManager mInstance;

    public static CustomToastManager getInstance() {
        if(null == mInstance) {
            synchronized (CustomToastManager.class) {
                if (null == mInstance) {
                    mInstance = new CustomToastManager();
                }
            }
        }
        return mInstance;
    }
    /**
     * 自定义下架 toast
     * @param isDelayShow
     */
    public void showRemoveToast(Boolean isDelayShow) {
        ToastConfigBean toastConfigBean = new ToastConfigBean("https://static.szwego.com/etc/h5/ic_toast.png","下架成功");
        ToastConfigBean.ButtonBean buttonBean = new ToastConfigBean.ButtonBean();
        buttonBean.setButtonType(ToastConfigBean.TYPE_ARROW_BUTTON);
        buttonBean.setButtonJumpMode(ToastConfigBean.JUMP_NEW_WEB_VIEW);
        String url = "";
        buttonBean.setJumpHtmlUrl(url);
        buttonBean.setButtonTitle("查看下架商品");
        List<ToastConfigBean.ButtonBean> buttonBeanList = new ArrayList<>();
        buttonBeanList.add(buttonBean);
        toastConfigBean.setToastTime(4000);
        toastConfigBean.setButtons(buttonBeanList);
        NLog.i(TAG, "showRemoveToast: "+BaseActivity.topActivity().getClass().getSimpleName());
        if (isDelayShow) {
            new Handler().postDelayed(() -> FloatingViewToast.show(BaseActivity.bottomActivity(), toastConfigBean), 300);
        } else {
            FloatingViewToast.show(BaseActivity.bottomActivity(), toastConfigBean);
        }
    }

    /**
     * 自定义删除成功 toast
     */
    public void showDeleteGoodsSuccess(Boolean isOpen, String shopId, Boolean isDelayShow) {
        ToastConfigBean toastConfigBean = new ToastConfigBean("https://static.szwego.com/etc/h5/ic_toast.png", "删除成功");
        if (!isOpen) {
            ToastConfigBean.ButtonBean buttonBean = new ToastConfigBean.ButtonBean();
            buttonBean.setButtonType(ToastConfigBean.TYPE_ARROW_BUTTON);
            buttonBean.setButtonJumpMode(ToastConfigBean.JUMP_NEW_WEB_VIEW);
            String url = "";
            buttonBean.setJumpHtmlUrl(url);
            buttonBean.setButtonTitle("开启智能清理更省事");
            List<ToastConfigBean.ButtonBean> buttonBeanList = new ArrayList<>();
            buttonBeanList.add(buttonBean);
            toastConfigBean.setButtons(buttonBeanList);
        }
        toastConfigBean.setToastTime(4000);
        if (isDelayShow) {
            new Handler().postDelayed(() -> FloatingViewToast.show(BaseActivity.topActivity(), toastConfigBean), 500);
        } else {
            FloatingViewToast.show(BaseActivity.topActivity(), toastConfigBean);
        }
    }

}
