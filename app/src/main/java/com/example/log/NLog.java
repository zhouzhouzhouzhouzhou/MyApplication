/*
 * Copyright (c) 1998-2012 TENCENT Inc. All Rights Reserved.
 *
 * FileName: QTLog.java
 *
 * Description: 日志输出工具类文件
 *
 * History:
 * 1.0	devilxie	2012-09-05	Create
 */
package com.example.log;

import android.text.TextUtils;

import java.io.File;

/**
 * 日志输出类，可控制调试与文件日志的控制
 *
 * @author devilxie
 * @version 1.0
 */
public final class NLog {
    public final static String LOG_FILENAME = "wsxc_logcat.log";
    private static boolean debug = false;            // 是否记录日志
    private static Logger logger = null;

    public static Logger getLogger() {
        return logger;
    }

    public static synchronized void setDebug(boolean d, int level) {
        boolean old = debug;
        if (old == d)
            return;


        if (old) {
            trace(Logger.TRACE_REALTIME, null);
        }

        debug = d;

        if (d) {
            if (logger == null) {
                logger = Logger.getLogger(null);
            }

            logger.setLevel(level);
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static synchronized boolean trace(int level, String path) {
        try {
            if (!debug)
                throw new IllegalStateException("you should enable log before modifing trace mode");

            if (logger == null) {
                logger = Logger.getLogger(null);
            }

            if (level == Logger.TRACE_ALL || level == Logger.TRACE_OFFLINE) {
                if (TextUtils.isEmpty(path))
                    throw new IllegalArgumentException("path should not be null for offline and all mode");

                File dir = new File(path);
                if (!dir.exists() || !dir.isDirectory()) {
                    boolean b = dir.mkdirs();
                    if (!b)
                        return false;
                }

                StringBuffer sb = new StringBuffer(path);
                sb.append(File.separator);
                sb.append(LOG_FILENAME);
                path = sb.toString();
            }
            return logger.trace(level, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String buildWholeMessage(String format, Object... args) {
        if (args == null || args.length == 0)
            return format;

        String msg = String.format(format, args);
        return msg;
    }

    public static void d(String tag, String format, Object... args) {
        if (debug && logger!=null) {
            logger.d(tag, buildWholeMessage(format, args));
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (debug&& logger!=null) {
            logger.i(tag, buildWholeMessage(format, args));
        }
    }

    public static void e(String tag, String format, Object... args) {
        if (debug&& logger!=null) {
            if (format != null && logger != null) {
                logger.e(tag, buildWholeMessage(format, args));
            }
        }
    }

    public static void e(String tag, Throwable e) {
        if (debug&& logger!=null) {
            logger.e(tag, e);
        }
    }

    public static void v(String tag, String format, Object... args) {
        if (debug&& logger!=null) {
            logger.v(tag, buildWholeMessage(format, args));
        }
    }

    public static void w(String tag, String format, Object... args) {
        if (debug&& logger!=null) {
            logger.w(tag, buildWholeMessage(format, args));
        }
    }

    public static void printStackTrace(Exception e) {
        if (debug&& logger!=null) {
            logger.e("TCLException", e);
        }
    }
}
