package com.example.wgapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.base.BaseActivity;
import com.example.log.NLog;
import com.example.toast.CustomToastManager;
import com.example.wgapplication.R;

public class ToastTestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_toast);
        findViewById(R.id.toOtherActivity).setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(this, ToastTest2Activity.class);
            startActivity(intent);

        });
    }
}
