package com.example.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.example.WgApp;


/**
 * Created by chenyu on 15/10/18.
 */
public class ViewSizeUtil {


    /**
     * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
     * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
     */

    public static Rect getWindowRootViewRect(Activity context) {
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame;
    }

    public static Integer[] getWidthAndHeight(Window window) {
        Integer[] integer = new Integer[2];
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        } else {
            window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        }
        integer[0] = dm.widthPixels;
        integer[1] = dm.heightPixels;
        return integer;
    }

    public static Rect getViewRectInParent(View view, ViewGroup parent) {
        Rect rect = new Rect();
        view.getDrawingRect(rect);
        parent.offsetDescendantRectToMyCoords(view, rect);
        return rect;
    }

    public static Point getScreenSize() {
        WindowManager wmManager = (WindowManager) WgApp.app.getApp().getSystemService(Context.WINDOW_SERVICE);
        Display display = wmManager.getDefaultDisplay();
        DisplayMetrics metric = new DisplayMetrics();
        display.getMetrics(metric);
        Point point = new Point();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getSize(point);
        } else {
            display.getRealSize(point);
        }
        return point;
    }

    public static float getPhoneRatio() {
        Point screenSize = getScreenSize();
        return ((float) screenSize.y) / ((float) screenSize.x);
    }

    /**
     * @param dimen dp值
     * @return
     */
    public static int getCustomDimen(float dimen) {
        int result = (int) (0.5f + ViewSizeUtil.getScreenSize().x * dimen / 375f);
        return result;
    }

    public static float getDimen(int dimens){
        return WgApp.app.getApp().getResources().getDimension(dimens);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isFullScreen() {
        // true 是手势，默认是 false
        // https://www.v2ex.com/t/470543
        return Settings.Global.getInt(WgApp.app.getApp().getContentResolver(), "force_fsg_nav_bar", 0) != 0;

    }


    public static boolean isMIUI() {
        String manufacturer = Build.MANUFACTURER;
        // 这个字符串可以自己定义,例如判断华为就填写huawei,魅族就填写meizu
        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
            return true;
        }
        return false;
    }

    public static float getDensity() {
        WindowManager wmManager = (WindowManager) WgApp.app.getApp().getSystemService(Context.WINDOW_SERVICE);
        Display display = wmManager.getDefaultDisplay();
        DisplayMetrics metric = new DisplayMetrics();
        display.getMetrics(metric);
        return metric.density;
    }

    public static void getAndroiodScreenProperty() {
        WindowManager wm = (WindowManager) WgApp.app.getApp().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// 屏幕宽度（像素）
        int height = dm.heightPixels; // 屏幕高度（像素）
        float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        //屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);//屏幕宽度(dp)
        int screenHeight = (int) (height / density);//屏幕高度(dp)
    }

    public static int getAppHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.height();
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    public static int getStatusBarHeight(Activity activity) {
        int height = 0;
        //获取status_bar_height资源的ID
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            height = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }


}
