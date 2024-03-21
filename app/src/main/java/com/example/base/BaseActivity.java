
package com.example.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * 所有Activity的基类，负责管理Activity生命周期
 */
public class BaseActivity extends FragmentActivity {
    protected String TAG = BaseActivity.this.getClass().getSimpleName();

    public static boolean isAutoSetting;


    public static final int TRANSFER_FILE_STATUS_SUCCESS = 3;
    public static final int TRANSFER_FILE_STATUS_FAILED = 2;
    private String mWebViewPath;

    public String getWebViewPath() {
        return mWebViewPath;
    }

    public void setWebViewPath(String mWebViewPath) {
        this.mWebViewPath = mWebViewPath;
    }



    public static void setAutoSetting(boolean isAutoSetting) {
        BaseActivity.isAutoSetting = isAutoSetting;

    }



    public static class ActivityReference extends WeakReference<Activity> {
        public ActivityReference(Activity r, ReferenceQueue<? super Activity> q) {
            super(r, q);
        }
    }

    /**
     * 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中）
     */
    public static ReferenceQueue<Activity> sReferenceQueue = new ReferenceQueue<Activity>();

    public static LinkedList<ActivityReference> sStack = new LinkedList<ActivityReference>();

    private boolean mVisible;
    private ActivityReference mRef;

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Utils.setWindowStatusBarColor(this, R.color.title_background);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        if (BaseActivity.this.getClass() != LaunchActivity.class
//                && BaseActivity.this.getClass() != MainActivity.class
//                && (BaseActivity.this.getClass() == WebviewActivity.class
//                && !TextUtils.isEmpty(WgUser.getUser().getToken()))) {
//            showAutoPasteDlg();
//        }
        mRef = new ActivityReference(this, sReferenceQueue);
        sStack.push(mRef);
    }


    protected void onRequestPermission() {

    }


    private static void clean() {
        ActivityReference ref = null;
        while ((ref = (ActivityReference) sReferenceQueue.poll()) != null) {
            sStack.remove(ref);
        }
    }


    public static void finishActivityStack() {
        clean();
        Iterator<ActivityReference> it = sStack.iterator();
        while (it.hasNext()) {
            ActivityReference ref = it.next();
            if (ref != null && ref.get() != null) {
                Activity activity = ref.get();
                if (!activity.isFinishing())
                    activity.finish();
            }
        }

        sStack.clear();
    }

    public static Activity topActivity() {
        Activity activity = null;
        ActivityReference ref = null;
        do {
            ref = sStack.peek();
            if (ref == null)
                break;
            if (ref.get() != null) {
                activity = ref.get();
                break;
            } else {
                sStack.pop();
            }
        } while (true);

        return activity;
    }

    public static Activity topWithoutShareActivity() {
        Activity activity = null;
        ActivityReference ref = null;
        do {
            ref = sStack.peek();
            if (ref == null)
                break;
            if (ref.get() != null) {
                activity = ref.get();
                String name = "com.truedian.dragon.activity.ShareActivity";
                if(!name.equals(activity.getClass().getName())){
                    break;
                }else{
                    sStack.pop();
                }
            } else {
                sStack.pop();
            }
        } while (true);

        return activity;
    }

    public static Activity bottomActivity() {
        Activity activity = null;
        ActivityReference ref = null;
        if (!sStack.isEmpty()) {
            ref = sStack.getLast();
        }
        if (ref == null)
            return null;
        if (ref.get() != null) {
            activity = ref.get();
        }

        return activity;
    }

    public static void finishActivityStackExcept(Activity a) {
        clean();
        Iterator<ActivityReference> it = sStack.iterator();
        while (it.hasNext()) {
            ActivityReference ref = it.next();
            if (ref != null && ref.get() != null) {
                Activity activity = ref.get();
                if (activity == a)
                    continue;

                if (!activity.isFinishing())
                    activity.finish();
            }
        }

        sStack.clear();
    }

    public static void finishActivity(Class clazz) {
        clean();
        Iterator<ActivityReference> it = sStack.iterator();
        while (it.hasNext()) {
            ActivityReference ref = it.next();
            if (ref != null && ref.get() != null) {
                Activity activity = ref.get();
                if (activity.getClass() == clazz && !activity.isFinishing()) {
                    activity.finish();
                    break;
                }
            }
        }
    }

    public static boolean checkActivityIsFinished(String name) {
        try {
            if(sStack !=null && sStack.size() > 0){
                for (ActivityReference ref : sStack) {
                    if (ref != null && ref.get() != null) {
                        Activity activity = ref.get();
                        if (activity.getClass().getName().equals(name)) {
                            return activity.isFinishing();
                        }
                    }
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkActivityExists(Class clazz) {
        try {
            if(sStack !=null && sStack.size() > 0){
                for (ActivityReference ref : sStack) {
                    if (ref != null && ref.get() != null) {
                        Activity activity = ref.get();
                        if (activity.getClass() == clazz) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    public static void finishActivityBetween(Class clazz) {
        try{
            int firstIndex = 0;
            for (int i = 0; i < sStack.size(); i++) {
                ActivityReference ref = sStack.get(i);
                if (ref != null && ref.get() != null) {
                    Activity activity = ref.get();
                    if (activity.getClass() == clazz){
                        firstIndex = i;
                    }
                }
            }
            if(firstIndex != 0){
                for (int i = 0; i <= firstIndex; i++) {
                    ActivityReference ref = sStack.get(i);
                    if (ref != null && ref.get() != null) {
                        Activity activity = ref.get();
                        if(activity.getClass() == clazz && i==0){
                            continue;
                        }
                        if (!activity.isFinishing()){
                            activity.finish();
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    protected boolean isVisible() {
        return mVisible;
    }

    @Override
    protected void onResume() {
        super.onResume();

//        int entryType = PrefsUtils.loadPrefInt(WgApp.app.getApp(), Constants.SHOW_GUIDE_OF_ENTRY_TYPE, 0);
//        if (entryType == Constants.SHARE_WXCIRCLE){
//            int count = PrefsUtils.loadPrefInt(WgApp.app.getApp(), Constants.PRE_FIRST_ANTI_GIF_BACK, 0);
//            getService().showVideoAntiFoldDialog(this, count);
//        }else if (entryType == Constants.SHARE_WX_VIDEO){
//            int wxVideoGuideCount = PrefsUtils.loadPrefInt(WgApp.app.getApp(), Constants.WX_VIDEO_GUIDE_FLAG_COUNT, 0);
//            getService().showWxVideoGuideDialog(this, wxVideoGuideCount);
//        }


//        if (WgApp.app.getApp().isShareAction()) {
//            EventEntity obj = new EventEntity(FunctionConfig.AUTO_PASTE_STOP);
//            RxBus.getDefault().post(obj);
//        }
//        Bugtags.onResume(this);
        mVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();


//        Bugtags.onPause(this);
        mVisible = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //注：回调 3
//        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

    /**
     * 只显示栈底的activity
     */
    public static void gotoBottomActivity() {
        if (sStack.size() <= 1) {
            return;
        }
        clean();
        while (sStack.size() != 1) {
            ActivityReference ref = sStack.getFirst();
            if (ref != null && ref.get() != null) {
                Activity activity = ref.get();
                if (!activity.isFinishing()) {
                    activity.finish();
                    sStack.remove(ref);
                }
            }
        }

    }

    public static void removeRef(ActivityReference mRef){
        if(sStack != null && sStack.size() > 0){
            sStack.remove(mRef);
        }
    }

    public static void pushRef(ActivityReference mRef){
        if(sStack != null){
            sStack.push(mRef);
        }
    }
}
