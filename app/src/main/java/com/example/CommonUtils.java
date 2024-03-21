package com.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hey_wego on 2017-02-07.
 */
public class CommonUtils {
    public final static String ALI_DETAIL_KEY = "m.1688.com/offer/";
    public final static String ALI_DETAIL_KEY1 = "dj.1688.com/ci_bb";
    public static final String QQZONE_IMAGE_PREFIX = "photocq.photo.store.qq.com/psc";

    public static String getTopDomain(String url) {
        String RE_TOP = "[\\w-]+\\.(com.cn|net.cn|gov.cn|org\\.nz|org.cn|com|net|org|gov|cc|biz|info|cn|co)\\b()*";
        String result = url;
        Pattern pattern = Pattern.compile(RE_TOP, Pattern.CASE_INSENSITIVE);
        try {
            Matcher matcher = pattern.matcher(url);
            matcher.find();
            result = matcher.group();
        } catch (Exception e) {
            System.out.println("[getTopDomain ERROR]====>");
            e.printStackTrace();
        }
        return result;
    }

    //
//    public static String exchangeImageURL(String url) {
//        String newURL = url;
//        if (url.startsWith("//")) {
//            newURL = "http:" + url;
//        }
//        newURL = newURL.replace("https", "http");
//        return newURL;
//    }
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static boolean isImgURL(String url, String[] imgsufix) {
//        PrefsUtils.loadPrefString(getApplicationContext(), Constants.token)
        String[] imgeArray = imgsufix;
        if (imgsufix == null || imgsufix.length == 0) {
            imgeArray = new String[]{"bmp", "jpe", "jpeg", "jpg", "png", "tif", "tiff", "webp"};
        }
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (!url.startsWith("//") && !url.startsWith("http")) {
            return false;
        }
        String tmpName = url.substring(url.lastIndexOf(".") + 1, url.length());

        for (int i = 0; i < imgeArray.length; i++) {
            if (tmpName.equalsIgnoreCase(imgeArray[i])) {
                return true;
            }
        }
        if (url.contains(QQZONE_IMAGE_PREFIX)){
            return true;
        }
        return false;
    }

    public static String filterImgLabelURL(String url, String sourceURL) {
        String _url = url;
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        if (url.length() > 200) {
            return "";
        }
        if (!url.startsWith("//") && !url.startsWith("http")) {
            return "";
        }

        String protocol = "http";
        if (sourceURL != null) {
            try {
                URL uri = new URL(sourceURL);
                protocol = uri.getProtocol();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        if (!url.contains("http")) {
            _url = protocol + ":" + url;
        }
//        _url = _url.replace("https","http");
        return _url;
    }



    public static boolean isAliImgThumbnail(String url) {

        boolean isAliImg = isAliImg(url);

        if (!isAliImg) {
            return false;
        }

        String patternString = ".*\\_(\\d+x|Q|q)\\d+.*";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            return false;
        }

        return true;
    }


    /**
     * 判断sina图片是否有小图格式
     *
     * @param url
     * @return
     */
    public static boolean isSinaImgThumbnail(String url) {
        String[] filter = {
                "sinaimg.cn",
        };
        if (!url.contains("/large/")) {
            return false;
        }

        for (int i = 0; i < filter.length; i++) {
            if (url.contains(filter[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断QQZone图片是否有小图格式
     *
     * @param url
     * @return
     */
    public static boolean isQQZoneImgThumbnail(String url) {
        String[] filter = {
                "m.qpic.cn",
        };

        boolean isQQZoneImg = false;
        //判断是否QQ图片
        for (int i = 0; i < filter.length; i++) {
            if (url.contains(filter[i])) {
                isQQZoneImg = true;
                break;
            }
        }
        if (!isQQZoneImg) {
            return false;
        }

        String patternString = "\\/([a-z0-9])\\/"; //匹配小图片
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            String str = matcher.group(1);
            char label = str.charAt(0);
            if (label == 'a' || label == 'i' || label == 'l' || label == 'm') {
                //本身是小图
                return false;
            }
            return true;
        }
        return false;

    }


    public static boolean isAliImg(String url) {
        String patternString = ".*((gw\\d+|gju\\d+|cub01|cbu01|gd1|img)\\.alicdn\\.com).*";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }



    /**
     * 判断URL是否有效，去掉无效的资源图片
     *
     * @param url
     * @return
     */
    private static boolean isVaildImg(String url) {
//        String[] filter = {
//                "assets.alicdn.com",
//                "imgs.t.sinajs",
//                "h5.sinaimg",
//                "tva1.sinaimg",
//                "tva2.sinaimg",
//                "tva3.sinaimg",
//                "tva4.sinaimg",
//        };
//
//        for (int i = 0; i < filter.length; i++) {
//            if (url.contains(filter[i])) {
//                return false;
//            }
//        }
//        return true;
        String patternString = ".*((tva\\d+|h5|tc)\\.sinaimg|imgs\\.t\\.sinajs|assets\\.alicdn).*";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            return false;
        }
        return true;
    }



    private static final String STATUS_BAR_HEIGHT = "status_bar_height";
    private static final String NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";
    private static final String MIUI_NAVIGATION_BAR_HIDE_SHOW = "force_fsg_nav_bar";
    private static final String EMUI_NAVIGATION_BAR_HIDE_SHOW = "navigationbar_is_min";

    public static int getStatusBarHeight(Context context) {
        return getInternalDimensionSize(context,STATUS_BAR_HEIGHT);
    }

    private static int getInternalDimensionSize(Context context,String key) {
        int result = 0;
        try {
            int resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                int sizeOne = context.getResources().getDimensionPixelSize(resourceId);
                int sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId);
                if (sizeTwo >= sizeOne) {
                    return sizeTwo;
                } else {
                    float densityOne = context.getResources().getDisplayMetrics().density;
                    float densityTwo = Resources.getSystem().getDisplayMetrics().density;
                    float f = sizeOne * densityTwo / densityOne;
                    return (int) ((f >= 0) ? (f + 0.5f) : (f - 0.5f));
                }
            }
        } catch (Resources.NotFoundException ignored) {
            return 0;
        }
        return result;
    }



    /**
     * dpתpx
     */
    public static int dip2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * pxתdp
     */
    public static int px2dip(Context ctx, float pxValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 包含虚拟导航栏高度
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        defaultDisplay.getRealSize(outPoint);
        return outPoint.y;
    }

    /**
     * 是否是平板
     *
     * @param context 上下文
     * @return 是平板则返回true，反之返回false
     */
    public static boolean isPad(Context context) {
        boolean isPad = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y); // 屏幕尺寸
        return isPad || screenInches >= 7.0;
    }

    public static String getPath(String path){
        if (path != null) {
            if (path.startsWith("http://local")) {
                path = path.replace("http://local", "");
            } else if (path.startsWith("https://local")) {
                path = path.replace("https://local", "");
            } else if (path.startsWith("file://")) {
                path = path.replace("file://", "");
            }
        }
        return path;
    }


    //转换字符
    private static String changeString(int num) {
        return num >= 0 && num < 10 ? "0" + num : "" + num;
    }

    //转换时分秒函数，
    public static String transformationDate(int times) {
        //参数时间戳传入进来是以毫秒为单位的时间戳
        //剩余秒数
        int second = times / 1000;
        //剩余小时，每一个小时有60分钟，每分钟有60秒
        //剩余分钟，每分钟有60秒
        second %= 3600;//剩余的秒
        int branch = second / 60;
        //最后剩余秒数
        second %= 60;
        return changeString(branch) + ":" + changeString(second);
    }

    //判断悬浮窗权限是否开启
    public static boolean isOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
            try {
                Class<Settings> clazz = Settings.class;
                Method method = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                Object o = method.invoke(null, context);
                return o != null && (boolean) o;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
