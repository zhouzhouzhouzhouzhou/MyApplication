package com.example.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class Size {
    public String getApplicationSize(Context context) {
        // 获取PackageManager对象
        PackageManager pm = context.getPackageManager();

        // 获取已安装的应用列表
        for (ApplicationInfo appInfo : pm.getInstalledApplications(PackageManager.GET_META_DATA)) {
            // 获取应用的名称
            String appName = appInfo.loadLabel(pm).toString();
            // 获取应用的包名
            String packageName = appInfo.packageName;

            // 获取应用的大小
            long appSize = 0;
            try {
                // 获取应用信息
                ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
                // 获取应用占用的存储空间大小，以字节为单位
                appSize = new java.io.File(info.sourceDir).length();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            // 将应用大小转换为更友好的格式（KB、MB、GB等）
            String formattedSize = formatSize(appSize);

            // 输出应用信息
            Log.d("AppSize", "App Name: " + appName + ", Package Name: " + packageName + ", Size: " + formattedSize);
            return formattedSize;
        }
        return "formattedSize";

    }

        // 将字节大小转换为更友好的格式（KB、MB、GB等）
        private String formatSize(long size) {
            String[] units = {"B", "KB", "MB", "GB", "TB"};
            int unitIndex = 0;
            double sizeInUnits = size;
            while (sizeInUnits >= 1024 && unitIndex < units.length - 1) {
                sizeInUnits /= 1024;
                unitIndex++;
            }
            return String.format("%.2f %s", sizeInUnits, units[unitIndex]);
        }
}
