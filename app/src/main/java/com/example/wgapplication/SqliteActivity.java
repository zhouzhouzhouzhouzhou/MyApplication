package com.example.wgapplication;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.base.BaseActivity;
import com.example.db.MyDatabaseManager;
import com.example.utils.Size;

public class SqliteActivity extends BaseActivity {
    private MyDatabaseManager dbManager;
    long userId = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sqlite);
        dbManager = new MyDatabaseManager(this);
        dbManager.open();
        findViewById(R.id.add).setOnClickListener(view -> {
            new Thread(() -> {
                for (int i = 0; i < 100*10000; i++) {
                    // 插入数据
                    userId = dbManager.insertUser("AliceAliceAliceAliceAliceAliceAliceAliceAlice收到粉丝的风景哦四大卷发哦搭配的肌肤使肌肤i啊手机放屁啊就是佛i就死啊减肥", 194);
                }
            }).start();

        });
        findViewById(R.id.remove).setOnClickListener(view -> {
            new Thread(() -> {
                // 删除数据
                dbManager.deleteUser(194);

            }).start();

        });
        findViewById(R.id.show).setOnClickListener(view -> {
            TextView size = findViewById(R.id.text);
            size.setText(new Size().getApplicationSize(this));
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}

