package com.example.floatingwindow;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings({"unchecked", "unused", "UnusedReturnValue"})
public class FloatWindow<X extends FloatWindow<?>> implements Runnable, ScreenOrientationMonitor.OnScreenOrientationCallback {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private static final List<FloatWindow<?>> sWindowInstanceSet = new ArrayList<>();

    /**
     * 基于 Activity 创建一个 FloatWindow 实例
     */
    @SuppressWarnings("rawtypes")
    public static FloatWindow with(Activity activity) {
        return new FloatWindow(activity);
    }

    /**
     * 基于全局创建一个 FloatWindow 实例，需要悬浮窗权限
     */
    @SuppressWarnings("rawtypes")
    public static FloatWindow with(Application application) {
        return new FloatWindow(application);
    }

    /**
     * 取消所有正在显示的悬浮窗
     */
    public static synchronized void cancelAll() {
        try {
            for (FloatWindow<?> floatWindow : sWindowInstanceSet) {
                if (floatWindow == null) {
                    continue;
                }
                if(floatWindow.mLifecycle != null){
                    floatWindow.mLifecycle.unregister();
                    floatWindow.mLifecycle.setWindow(null);
                }
                floatWindow.cancel();

            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    /**
     * 取消特定类名的悬浮窗
     */
    public static synchronized void cancelByClass(Class<FloatWindow<?>> clazz) {
        if (clazz == null) {
            return;
        }
        for (FloatWindow<?> floatWindow : sWindowInstanceSet) {
            if (floatWindow == null) {
                continue;
            }
            if (!clazz.equals(floatWindow.getClass())) {
                continue;
            }
            floatWindow.cancel();
        }
    }

    /**
     * 取消特定标记的悬浮窗
     */
    public static synchronized void cancelByTag(String tag) {
        if (tag == null) {
            return;
        }
        for (FloatWindow<?> floatWindow : sWindowInstanceSet) {
            if (floatWindow == null) {
                continue;
            }
            if (!tag.equals(floatWindow.getTag())) {
                continue;
            }
            floatWindow.cancel();
        }
    }

    /**
     * 回收所有正在显示的悬浮窗
     */
    public static synchronized void recycleAll() {
        Iterator<FloatWindow<?>> iterator = sWindowInstanceSet.iterator();
        while (iterator.hasNext()) {
            FloatWindow<?> floatWindow = iterator.next();
            if (floatWindow == null) {
                continue;
            }
            // 这里解释一下，为什么要使用迭代器移除，如果不那么做的话
            // recycle 方法里面会再移除一次
            // 当前又是一个 while 循环，可能会出现角标越界的情况
            iterator.remove();
            floatWindow.recycle();
        }
    }

    /**
     * 回收特定类名的悬浮窗
     */
    public static void recycleByClass(Class<FloatWindow<?>> clazz) {
        if (clazz == null) {
            return;
        }
        Iterator<FloatWindow<?>> iterator = sWindowInstanceSet.iterator();
        while (iterator.hasNext()) {
            FloatWindow<?> floatWindow = iterator.next();
            if (floatWindow == null) {
                continue;
            }
            if (!clazz.equals(floatWindow.getClass())) {
                continue;
            }
            // 这里解释一下，为什么要使用迭代器移除，如果不那么做的话
            // recycle 方法里面会再移除一次
            // 当前又是一个 while 循环，可能会出现角标越界的情况
            iterator.remove();
            floatWindow.recycle();
        }
    }

    /**
     * 回收特定标记的悬浮窗
     */
    public static void recycleByTag(String tag) {
        if (tag == null) {
            return;
        }

        Iterator<FloatWindow<?>> iterator = sWindowInstanceSet.iterator();
        while (iterator.hasNext()) {
            FloatWindow<?> floatWindow = iterator.next();
            if (floatWindow == null) {
                continue;
            }
            if (!tag.equals(floatWindow.getTag())) {
                continue;
            }
            // 这里解释一下，为什么要使用迭代器移除，如果不那么做的话
            // recycle 方法里面会再移除一次
            // 当前又是一个 while 循环，可能会出现角标越界的情况
            iterator.remove();
            floatWindow.recycle();
        }
    }

    private Object mParams;
    /** 上下文 */
    private Context mContext;
    /** 根布局 */
    private ViewGroup mDecorView;
    /** 悬浮窗 */
    private WindowManager mWindowManager;
    /** 悬浮窗参数 */
    private WindowManager.LayoutParams mWindowParams;

    /** 当前是否已经显示 */
    private boolean mShowing;
    /** 悬浮窗显示时长 */
    public int mDuration;
    public DurationCountDownTimer countDownTimer;
    /** 悬浮窗标记 */
    private String mTag;
    /** Toast 生命周期管理 */
    private ActivityWindowLifecycle mLifecycle;
    /** 自定义拖动处理 */
    private BaseDraggable mDraggable;
    /** 吐司显示和取消监听 */
    private OnWindowLifecycle mListener;

    /** 屏幕旋转监听 */
    private ScreenOrientationMonitor mScreenOrientationMonitor;

    /** 更新任务 */
    private final Runnable mUpdateRunnable = this::update;

    /**
     * 创建一个局部悬浮窗
     */
    public FloatWindow(Activity activity) {
        this((Context) activity);

//        Window window = activity.getWindow();
//        View decorView = window.getDecorView();
//        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
//        if ((params.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0 ||
//                (decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) {
//            // 如果当前 Activity 是全屏模式，那么需要添加这个标记，否则会导致 WindowManager 在某些机型上移动不到状态栏的位置上
//            // 如果不想让状态栏显示的时候把 WindowManager 顶下来，可以添加 FLAG_LAYOUT_IN_SCREEN，但是会导致软键盘无法调整窗口位置
//            addWindowFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            // 如果是 Android 9.0，则需要对刘海屏进行适配，否则也会导致 WindowManager 移动不到刘海屏的位置上面
//            setLayoutInDisplayCutoutMode(params.layoutInDisplayCutoutMode);
//        }
//
//        if (params.systemUiVisibility != 0) {
//            setSystemUiVisibility(params.systemUiVisibility);
//        }
//
//        if (decorView.getSystemUiVisibility() != 0) {
//            mDecorView.setSystemUiVisibility(decorView.getSystemUiVisibility());
//        }

        // 跟随 Activity 的生命周期
        mLifecycle = new ActivityWindowLifecycle(this, activity);
        // 注册 Activity 生命周期监听
        mLifecycle.register();
    }

    /**
     * 创建一个全局悬浮窗
     */
    public FloatWindow(Application application) {
        this((Context) application);

        // 设置成全局的悬浮窗，注意需要先申请悬浮窗权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            setWindowType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    private FloatWindow(Context context) {
        mContext = context;
        mDecorView = new WindowLayout(context);
        mWindowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        // 配置一些默认的参数
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = android.R.style.Animation_Toast;
        mWindowParams.packageName = context.getPackageName();
        // 设置触摸外层布局（除 WindowManager 外的布局，默认是 WindowManager 显示的时候外层不可触摸）
        // 需要注意的是设置了 FLAG_NOT_TOUCH_MODAL 必须要设置 FLAG_NOT_FOCUSABLE，否则就会导致用户按返回键无效
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 将当前实例添加到静态集合中
        sWindowInstanceSet.add(this);
    }

    /**
     * 设置悬浮窗口标记
     */
    public X setTag(String tag) {
        mTag = tag;
        return (X) this;
    }

    /**
     * 设置悬浮窗宽度
     */
    public X setWidth(int width) {
        mWindowParams.width = width;
        if (mDecorView.getChildCount() > 0) {
            View contentView = mDecorView.getChildAt(0);
            ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
            if (layoutParams != null && layoutParams.width != width) {
                layoutParams.width = width;
                contentView.setLayoutParams(layoutParams);
            }
        }
        postUpdate();
        return (X) this;
    }

    /**
     * 设置悬浮窗高度
     */
    public X setHeight(int height) {
        mWindowParams.height = height;
        if (mDecorView.getChildCount() > 0) {
            View contentView = mDecorView.getChildAt(0);
            ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
            if (layoutParams != null && layoutParams.height != height) {
                layoutParams.height = height;
                contentView.setLayoutParams(layoutParams);
            }
        }
        postUpdate();
        return (X) this;
    }

    /**
     * 设置悬浮窗显示的重心
     */
    public X setGravity(int gravity) {
        mWindowParams.gravity = gravity;
        postUpdate();
        post(() -> {
            if (mDraggable != null) {
                mDraggable.refreshLocationCoordinate();
            }
        });
        return (X) this;
    }

    /**
     * 设置水平偏移量
     */
    public X setXOffset(int px) {
        mWindowParams.x = px;
        postUpdate();
        post(() -> {
            if (mDraggable != null) {
                mDraggable.refreshLocationCoordinate();
            }
        });
        return (X) this;
    }

    /**
     * 设置垂直偏移量
     */
    public X setYOffset(int px) {
        mWindowParams.y = px;
        postUpdate();
        post(() -> {
            if (mDraggable != null) {
                mDraggable.refreshLocationCoordinate();
            }
        });
        return (X) this;
    }

    /**
     * 设置悬浮窗外层是否可触摸
     */
    public X setOutsideTouchable(boolean touchable) {
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (touchable) {
            addWindowFlags(flags);
        } else {
            removeWindowFlags(flags);
        }
        postUpdate();
        return (X) this;
    }

    /**
     * 设置悬浮窗背景阴影强度
     *
     * @param amount        阴影强度值，填写 0 到 1 之间的值
     */
    public X setBackgroundDimAmount(float amount) {
        if (amount < 0 || amount > 1) {
            throw new IllegalArgumentException("amount must be a value between 0 and 1");
        }
        mWindowParams.dimAmount = amount;
        int flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        if (amount != 0) {
            addWindowFlags(flags);
        } else {
            removeWindowFlags(flags);
        }
        postUpdate();
        return (X) this;
    }

    /**
     * 添加窗口标记
     */
    public X addWindowFlags(int flags) {
        mWindowParams.flags |= flags;
        postUpdate();
        return (X) this;
    }

    /**
     * 移除窗口标记
     */
    public X removeWindowFlags(int flags) {
        mWindowParams.flags &= ~flags;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置窗口标记
     */
    public X setWindowFlags(int flags) {
        mWindowParams.flags = flags;
        postUpdate();
        return (X) this;
    }

    /**
     * 是否存在某个窗口标记
     */
    public boolean hasWindowFlags(int flags) {
        return (mWindowParams.flags & flags) != 0;
    }

    /**
     * 设置悬浮窗的显示类型
     */
    public X setWindowType(int type) {
        mWindowParams.type = type;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置动画样式
     */
    public X setAnimStyle(int id) {
        mWindowParams.windowAnimations = id;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置软键盘模式
     *
     * {@link WindowManager.LayoutParams#SOFT_INPUT_STATE_UNSPECIFIED}：没有指定状态,系统会选择一个合适的状态或依赖于主题的设置
     * {@link WindowManager.LayoutParams#SOFT_INPUT_STATE_UNCHANGED}：不会改变软键盘状态
     * {@link WindowManager.LayoutParams#SOFT_INPUT_STATE_HIDDEN}：当用户进入该窗口时，软键盘默认隐藏
     * {@link WindowManager.LayoutParams#SOFT_INPUT_STATE_ALWAYS_HIDDEN}：当窗口获取焦点时，软键盘总是被隐藏
     * {@link WindowManager.LayoutParams#SOFT_INPUT_ADJUST_RESIZE}：当软键盘弹出时，窗口会调整大小
     * {@link WindowManager.LayoutParams#SOFT_INPUT_ADJUST_PAN}：当软键盘弹出时，窗口不需要调整大小，要确保输入焦点是可见的
     */
    public X setSoftInputMode(int mode) {
        mWindowParams.softInputMode = mode;
        // 如果设置了不能触摸，则擦除这个标记，否则会导致无法弹出输入法
        removeWindowFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        postUpdate();
        return (X) this;
    }

    /**
     * 设置悬浮窗 Token
     */
    public X setWindowToken(IBinder token) {
        mWindowParams.token = token;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置悬浮窗透明度
     */
    public X setWindowAlpha(float alpha) {
        mWindowParams.alpha = alpha;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置垂直间距
     */
    public X setVerticalMargin(float verticalMargin) {
        mWindowParams.verticalMargin = verticalMargin;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置水平间距
     */
    public X setHorizontalMargin(float horizontalMargin) {
        mWindowParams.horizontalMargin = horizontalMargin;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置位图格式
     */
    public X setBitmapFormat(int format) {
        mWindowParams.format = format;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置状态栏的可见性
     */
    public X setSystemUiVisibility(int systemUiVisibility) {
        mWindowParams.systemUiVisibility = systemUiVisibility;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置垂直权重
     */
    public X setVerticalWeight(float verticalWeight) {
        mWindowParams.verticalWeight = verticalWeight;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置挖孔屏下的显示模式
     */
    public X setLayoutInDisplayCutoutMode(int mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mWindowParams.layoutInDisplayCutoutMode = mode;
            postUpdate();
        }
        return (X) this;
    }

    /**
     * 设置悬浮窗在哪个显示屏上显示
     */
    public X setPreferredDisplayModeId(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mWindowParams.preferredDisplayModeId = id;
            postUpdate();
        }
        return (X) this;
    }

    /**
     * 设置悬浮窗标题
     */
    public X setWindowTitle(CharSequence title) {
        mWindowParams.setTitle(title);
        postUpdate();
        return (X) this;
    }

    /**
     * 设置屏幕的亮度
     */
    public X setScreenBrightness(float screenBrightness) {
        mWindowParams.screenBrightness = screenBrightness;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置按键的亮度
     */
    public X setButtonBrightness(float buttonBrightness) {
        mWindowParams.buttonBrightness = buttonBrightness;
        postUpdate();
        return (X) this;
    }

    /**
     * 设置悬浮窗的刷新率
     */
    public X setPreferredRefreshRate(float preferredRefreshRate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWindowParams.preferredRefreshRate = preferredRefreshRate;
            postUpdate();
        }
        return (X) this;
    }

    /**
     * 设置悬浮窗的颜色模式
     */
    public X setColorMode(int colorMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mWindowParams.setColorMode(colorMode);
            postUpdate();
        }
        return (X) this;
    }

    /**
     * 设置悬浮窗屏幕方向
     */
    public X setScreenOrientation(int orientation) {
        mWindowParams.screenOrientation = orientation;
        postUpdate();
        return (X) this;
    }

    /**
     * 重新设置 WindowManager 参数集
     */
    public X setWindowParams(WindowManager.LayoutParams params) {
        mWindowParams = params;
        postUpdate();
        return (X) this;
    }

    /**
     * 自定义参数
     */
    public X setParams(Object params) {
        mParams = params;
        return (X) this;
    }

    public Object getParams() {
        return mParams;
    }

    /**
     * 设置随意拖动
     */
    public X setDraggable() {
        return setDraggable(new MovingDraggable());
    }

    /**
     * 设置拖动规则
     */
    public X setDraggable(BaseDraggable draggable) {
        mDraggable = draggable;
        if (draggable != null) {
            // 如果当前是否设置了不可触摸，如果是就擦除掉这个标记
            removeWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            // 如果当前是否设置了可移动窗口到屏幕之外，如果是就擦除这个标记
            removeWindowFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            if (isShowing()) {
                update();
                draggable.start(this);
            }
        }

        if (mScreenOrientationMonitor == null) {
            mScreenOrientationMonitor = new ScreenOrientationMonitor(mContext.getResources().getConfiguration());
        }
        mScreenOrientationMonitor.registerCallback(mContext, this);

        return (X) this;
    }

    /**
     * 限定显示时长
     */
    public X setDuration(int duration) {
        mDuration = duration;
        if (isShowing() && mDuration != 0) {
            removeCallbacks(this);
            postDelayed(this, mDuration);
        }
        return (X) this;
    }

    public int getDuration() {
        return mDuration;
    }

    /**
     * 设置生命周期监听
     */
    public X setOnToastLifecycle(OnWindowLifecycle listener) {
        mListener = listener;
        return (X) this;
    }

    /**
     * 设置根布局（一般情况下推荐使用 {@link #setContentView} 方法来填充布局）
     */
    public X setDecorView(ViewGroup viewGroup) {
        mDecorView = viewGroup;
        return (X) this;
    }

    /**
     * 设置内容布局
     */
    public X setContentView(int id) {
        return setContentView(LayoutInflater.from(mContext).inflate(id, mDecorView, false));
    }

    public X setContentView(View view) {
        if (mDecorView.getChildCount() > 0) {
            mDecorView.removeAllViews();
        }
        mDecorView.addView(view);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = ((ViewGroup.MarginLayoutParams) layoutParams);
            // 清除 Margin，因为 WindowManager 没有这一属性可以设置，并且会跟根布局相冲突
            marginLayoutParams.topMargin = 0;
            marginLayoutParams.bottomMargin = 0;
            marginLayoutParams.leftMargin = 0;
            marginLayoutParams.rightMargin = 0;
        }

        // 如果当前没有设置重心，就自动获取布局重心
        if (mWindowParams.gravity == Gravity.NO_GRAVITY) {
            if (layoutParams instanceof FrameLayout.LayoutParams) {
                int gravity = ((FrameLayout.LayoutParams) layoutParams).gravity;
                if (gravity != FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY) {
                    mWindowParams.gravity = gravity;
                }
            } else if (layoutParams instanceof LinearLayout.LayoutParams) {
                int gravity = ((LinearLayout.LayoutParams) layoutParams).gravity;
                if (gravity != FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY) {
                    mWindowParams.gravity = gravity;
                }
            }

            if (mWindowParams.gravity == Gravity.NO_GRAVITY) {
                // 默认重心是居中
                mWindowParams.gravity = Gravity.BOTTOM;
            }
        }

        if (layoutParams != null) {
            if (mWindowParams.width == WindowManager.LayoutParams.WRAP_CONTENT &&
                    mWindowParams.height == WindowManager.LayoutParams.WRAP_CONTENT) {
                // 如果当前 Dialog 的宽高设置了自适应，就以布局中设置的宽高为主
                mWindowParams.width = layoutParams.width;
                mWindowParams.height = layoutParams.height;
            } else {
                // 如果当前通过代码动态设置了宽高，则以动态设置的为主
                layoutParams.width = mWindowParams.width;
                layoutParams.height = mWindowParams.height;
            }
        }

        postUpdate();
        return (X) this;
    }

    public void showAsDropDown(View anchorView) {
        showAsDropDown(anchorView, Gravity.BOTTOM);
    }

    public void showAsDropDown(View anchorView, int showGravity) {
        showAsDropDown(anchorView, showGravity, 0 , 0);
    }

    /**
     * 将悬浮窗显示在某个 View 下方（和 PopupWindow 同名方法作用类似）
     *
     * @param anchorView            锚点 View
     * @param showGravity           显示重心
     * @param xOff                  水平偏移
     * @param yOff                  垂直偏移
     */
    public void showAsDropDown(View anchorView, int showGravity, int xOff, int yOff) {
        if (mDecorView.getChildCount() == 0 || mWindowParams == null) {
            throw new IllegalArgumentException("WindowParams and view cannot be empty");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // 适配布局反方向
            showGravity = Gravity.getAbsoluteGravity(showGravity,
                    anchorView.getResources().getConfiguration().getLayoutDirection());
        }

        int[] anchorViewLocation = new int[2];
        anchorView.getLocationOnScreen(anchorViewLocation);

        Rect windowVisibleRect = new Rect();
        anchorView.getWindowVisibleDisplayFrame(windowVisibleRect);

        mWindowParams.gravity = Gravity.TOP | Gravity.START;
        mWindowParams.x = anchorViewLocation[0] - windowVisibleRect.left + xOff;
        mWindowParams.y = anchorViewLocation[1] - windowVisibleRect.top + yOff;

        if ((showGravity & Gravity.START) == Gravity.START) {
            int rootViewWidth = mDecorView.getWidth();
            if (rootViewWidth == 0) {
                rootViewWidth = mDecorView.getMeasuredWidth();
            }
            if (rootViewWidth == 0) {
                mDecorView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                rootViewWidth = mDecorView.getMeasuredWidth();
            }
            mWindowParams.x -= rootViewWidth;
        } else if ((showGravity & Gravity.END) == Gravity.END) {
            mWindowParams.x += anchorView.getWidth();
        }

        if ((showGravity & Gravity.TOP) == Gravity.TOP) {
            int rootViewHeight = mDecorView.getHeight();
            if (rootViewHeight == 0) {
                rootViewHeight = mDecorView.getMeasuredHeight();
            }
            if (rootViewHeight == 0) {
                mDecorView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                rootViewHeight = mDecorView.getMeasuredHeight();
            }
            mWindowParams.y -= rootViewHeight;
        } else if ((showGravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            mWindowParams.y += anchorView.getHeight();
        }

        show();
    }

    /**
     * 显示悬浮窗
     */
    public void show() {
        if (mDecorView.getChildCount() == 0 || mWindowParams == null) {
            throw new IllegalArgumentException("WindowParams and view cannot be empty");
        }

        // 如果当前已经显示则进行更新
        if (mShowing) {
            update();
            return;
        }

        if (mContext instanceof Activity) {
            Activity activity = ((Activity) mContext);
            if (activity.isFinishing() ||
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                            activity.isDestroyed())) {
                return;
            }
        }

        try {
            // 如果 View 已经被添加的情况下，就先把 View 移除掉
            if (mDecorView.getParent() != null) {
                mWindowManager.removeViewImmediate(mDecorView);
            }
            mWindowManager.addView(mDecorView, mWindowParams);
            // 当前已经显示
            mShowing = true;
            // 如果当前限定了显示时长
            if (mDuration != 0) {
                removeCallbacks(this);
                //postDelayed(this, mDuration);
                countDownTimer = new DurationCountDownTimer(mDuration, 1000);
                countDownTimer.start();
            }
            // 如果设置了拖拽规则
            if (mDraggable != null) {
                mDraggable.start(this);
            }

            // 回调监听
            if (mListener != null) {
                mListener.onWindowShow(this);
            }

        } catch (NullPointerException | IllegalStateException |
                IllegalArgumentException | WindowManager.BadTokenException e) {
            // 如果这个 View 对象被重复添加到 WindowManager 则会抛出异常
            // java.lang.IllegalStateException: View has already been added to the window manager.
            e.printStackTrace();
        }
    }

    private class DurationCountDownTimer extends CountDownTimer {

        DurationCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mDuration = (int) millisUntilFinished;
        }

        @Override
        public void onFinish() {
            mDuration = 0;
            wCancel();
        }
    }

    public void wCancel(){
        cancel();
    }

    /**
     * 销毁悬浮窗
     */
    public void cancel() {
        if (!mShowing) {
            return;
        }

        try {

            // 如果当前 WindowManager 没有附加这个 View 则会抛出异常
            // java.lang.IllegalArgumentException: View not attached to window manager
            mWindowManager.removeViewImmediate(mDecorView);

            // 移除销毁任务
            removeCallbacks(this);

            // 回调监听
            if (mListener != null) {
                mListener.onWindowCancel(this);
            }

            if(countDownTimer != null){
                countDownTimer.cancel();
            }

        } catch (NullPointerException | IllegalArgumentException | IllegalStateException e) {
            e.printStackTrace();
        } finally {
            // 当前没有显示
            mShowing = false;
        }
    }

    /**
     * 延迟更新悬浮窗
     */
    public void postUpdate() {
        if (!isShowing()) {
            return;
        }
        removeCallbacks(mUpdateRunnable);
        post(mUpdateRunnable);
    }

    /**
     * 更新悬浮窗
     */
    public void update() {
        if (!isShowing()) {
            return;
        }
        try {
            // 更新 WindowManger 的显示
            mWindowManager.updateViewLayout(mDecorView, mWindowParams);
        } catch (IllegalArgumentException e) {
            // 当 WindowManager 已经消失时调用会发生崩溃
            // IllegalArgumentException: View not attached to window manager
            e.printStackTrace();
        }
    }

    /**
     * 回收释放
     */
    public void recycle() {
        if (isShowing()) {
            cancel();
        }
        if (mScreenOrientationMonitor != null) {
            mScreenOrientationMonitor.unregisterCallback(mContext);
        }
        if (mListener != null) {
            mListener.onWindowRecycle(this);
        }
        // 反注册 Activity 生命周期
        if (mLifecycle != null) {
            mLifecycle.unregister();
        }
        mListener = null;
        mContext = null;
        mDecorView = null;
        mWindowManager = null;
        mWindowParams = null;
        mLifecycle = null;
        mDraggable = null;
        mScreenOrientationMonitor = null;
        // 将当前实例从静态集合中移除
        sWindowInstanceSet.remove(this);
    }

    /**
     * 当前是否已经显示
     */
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * 获取 WindowManager 对象（可能为空）
     */
    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    /**
     * 获取 WindowManager 参数集（可能为空）
     */
    public WindowManager.LayoutParams getWindowParams() {
        return mWindowParams;
    }

    /**
     * 获取当前的拖拽规则对象（可能为空）
     */
    public BaseDraggable getDraggable() {
        return mDraggable;
    }

    /**
     * 获取上下文对象（可能为空）
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 获取根布局（可能为空）
     */
    public View getDecorView() {
        return mDecorView;
    }

    /**
     * 获取内容布局
     */
    public View getContentView() {
        if (mDecorView.getChildCount() == 0) {
            return null;
        }
        return mDecorView.getChildAt(0);
    }

    /**
     * 根据 ViewId 获取 View
     */
    public <V extends View> V findViewById(int id) {
        return mDecorView.findViewById(id);
    }

    /**
     * 跳转 Activity
     */
    public void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(mContext, clazz));
    }

    public void startActivity(Intent intent) {
        if (!(mContext instanceof Activity)) {
            // 如果当前的上下文不是 Activity，调用 startActivity 必须加入新任务栈的标记
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
    }

    /**
     * 设置可见状态
     */
    public X setVisibility(int id, int visibility) {
        findViewById(id).setVisibility(visibility);
        return (X) this;
    }

    /**
     * 设置文本
     */
    public X setText(int id) {
        return setText(android.R.id.message, id);
    }

    public X setText(int viewId, int stringId) {
        return setText(viewId, mContext.getResources().getString(stringId));
    }

    public X setText(CharSequence text) {
        return setText(android.R.id.message, text);
    }

    public X setText(int id, CharSequence text) {
        ((TextView) findViewById(id)).setText(text);
        return (X) this;
    }

    /**
     * 设置字体颜色
     */
    public X setTextColor(int id, int color) {
        ((TextView) findViewById(id)).setTextColor(color);
        return (X) this;
    }

    /**
     * 设置字体大小
     */
    public X setTextSize(int id, float size) {
        ((TextView) findViewById(id)).setTextSize(size);
        return (X) this;
    }

    public X setTextSize(int id, int unit, float size) {
        ((TextView) findViewById(id)).setTextSize(unit, size);
        return (X) this;
    }

    /**
     * 设置提示
     */
    public X setHint(int viewId, int stringId) {
        return setHint(viewId, mContext.getResources().getString(stringId));
    }

    public X setHint(int id, CharSequence text) {
        ((TextView) findViewById(id)).setHint(text);
        return (X) this;
    }

    /**
     * 设置提示文本颜色
     */
    public X setHintColor(int id, int color) {
        ((TextView) findViewById(id)).setHintTextColor(color);
        return (X) this;
    }

    /**
     * 设置背景
     */
    public X setBackground(int viewId, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableId);
        return setBackground(viewId, drawable);
    }

    public X setBackground(int id, Drawable drawable) {
        findViewById(id).setBackground(drawable);
        return (X) this;
    }

    /**
     * 设置图片
     */
    public X setImageDrawable(int viewId, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableId);
        return setImageDrawable(viewId, drawable);
    }

    public X setImageDrawable(int viewId, Drawable drawable) {
        ((ImageView) findViewById(viewId)).setImageDrawable(drawable);
        return (X) this;
    }

    public X setImageUrl(int viewId, String url) {
        if(TextUtils.isEmpty(url)){
            setVisibility(viewId, View.GONE);
        }else{
            setVisibility(viewId, View.VISIBLE);
            Glide.with(mContext).load(url).into(((ImageView) findViewById(viewId)));
        }
        return (X) this;
    }

    public ViewGroup getViewGroup(int viewId){
        return findViewById(viewId);
    }

    public Handler getHandler() {
        return HANDLER;
    }

    public String getTag() {
        return mTag;
    }

    /**
     * 延迟执行
     */
    public boolean post(Runnable runnable) {
        return postDelayed(runnable, 0);
    }

    /**
     * 延迟一段时间执行
     */
    public boolean postDelayed(Runnable runnable, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return postAtTime(runnable, SystemClock.uptimeMillis() + delayMillis);
    }

    /**
     * 在指定的时间执行
     */
    public boolean postAtTime(Runnable runnable, long uptimeMillis) {
        // 发送和这个 WindowManager 相关的消息回调
        return HANDLER.postAtTime(runnable, this, uptimeMillis);
    }

    /**
     * 移除消息回调
     */
    public void removeCallbacks(Runnable runnable) {
        HANDLER.removeCallbacks(runnable);
    }

    public void removeCallbacksAndMessages() {
        HANDLER.removeCallbacksAndMessages(this);
    }

    /**
     * 设置点击事件
     */
    public X setOnClickListener(OnClickListener<? extends View> listener) {
        return setOnClickListener(mDecorView, listener);
    }

    public X setOnClickListener(int id, OnClickListener<? extends View> listener) {
        return setOnClickListener(findViewById(id), listener);
    }

    private X setOnClickListener(View view, OnClickListener<? extends View> listener) {
        // 如果当前是否设置了不可触摸，如果是就擦除掉
        removeWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        view.setClickable(true);
        view.setOnClickListener(new ViewClickWrapper(this, listener));
        return (X) this;
    }

    /**
     * 设置长按事件
     */
    public X setOnLongClickListener(OnLongClickListener<? extends View> listener) {
        return setOnLongClickListener(mDecorView, listener);
    }

    public X setOnLongClickListener(int id, OnLongClickListener<? extends View> listener) {
        return setOnLongClickListener(findViewById(id), listener);
    }

    private X setOnLongClickListener(View view, OnLongClickListener<? extends View> listener) {
        // 如果当前是否设置了不可触摸，如果是就擦除掉
        removeWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        view.setClickable(true);
        view.setOnLongClickListener(new ViewLongClickWrapper(this, listener));
        return (X) this;
    }

    /**
     * 设置触摸事件
     */
    public X setOnTouchListener(OnTouchListener<? extends View> listener) {
        return setOnTouchListener(mDecorView, listener);
    }

    public X setOnTouchListener(int id, OnTouchListener<? extends View> listener) {
        return setOnTouchListener(findViewById(id), listener);
    }

    private X setOnTouchListener(View view, OnTouchListener<? extends View> listener) {
        // 当前是否设置了不可触摸，如果是就擦除掉
        removeWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        view.setEnabled(true);
        view.setOnTouchListener(new ViewTouchWrapper(this, listener));
        return (X) this;
    }

    /**
     * {@link Runnable}
     */
    @Override
    public void run() {
        cancel();
    }

    /**
     * {@link ScreenOrientationMonitor.OnScreenOrientationCallback}
     */
    @Override
    public void onScreenOrientationChange(int newOrientation) {
        if (!isShowing()) {
            return;
        }
        if (mDraggable == null) {
            return;
        }
        mDraggable.onScreenOrientationChange();
    }

    /**
     * View 的点击事件监听
     */
    public interface OnClickListener<V extends View> {

        /**
         * 点击回调
         */
        void onClick(FloatWindow<?> window, V view);
    }

    /**
     * View 的长按事件监听
     */
    public interface OnLongClickListener<V extends View> {

        /**
         * 长按回调
         */
        boolean onLongClick(FloatWindow<?> window, V view);
    }

    /**
     * View 的触摸事件监听
     */
    public interface OnTouchListener<V extends View> {

        /**
         * 触摸回调
         */
        boolean onTouch(FloatWindow<?> window, V view, MotionEvent event);
    }

    /**
     * 窗口生命周期监听
     */
    public interface OnWindowLifecycle {

        /**
         * 显示回调
         */
        default void onWindowShow(FloatWindow<?> window) {}

        /**
         * 消失回调
         */
        default void onWindowCancel(FloatWindow<?> window) {}

        /**
         * 回收回调
         */
        default void onWindowRecycle(FloatWindow<?> window) {}
    }
}
