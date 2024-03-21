package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseManager {
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;

    public MyDatabaseManager(Context context) {
        dbHelper = new MyDBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // 插入数据
    public long insertUser(String name, int age) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("age", age);
        return db.insert("users", null, values);
    }

    // 删除数据
    public void deleteUser(long age) {
        db.delete("users", "age=?", new String[]{String.valueOf(age)});
    }


}
