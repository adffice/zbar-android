package com.gmail.adffice.zbar.sample.common;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

/**
 * 兼容低版本的子线程开启任务
 */
public class Runnable {
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void execAsync(AsyncTask<?, ?, ?> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }

    }

}
