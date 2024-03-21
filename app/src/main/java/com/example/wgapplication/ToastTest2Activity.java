package com.example.wgapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.base.BaseActivity;
import com.example.log.NLog;
import com.example.toast.CustomToastManager;

public class ToastTest2Activity extends BaseActivity {
    private static final String TAG = "浮窗";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_toast_2);
        findViewById(R.id.showToast).setOnClickListener(view -> {
            NLog.i(TAG, "onCreate: ");
            finish();
            NLog.i(TAG, "run: " + BaseActivity.topActivity().getClass().getSimpleName());
            CustomToastManager.getInstance().showRemoveToast(true);
        });
        findViewById(R.id.finish).setOnClickListener(view -> {
            finish();
        });
    }

}
