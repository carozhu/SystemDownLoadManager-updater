package com.android.updater;

import android.util.Log;


public class UpdateLogger {

    private static final String TAG = "UpdateLogger";

    private static UpdateLogger instance;

    private boolean isShowLog;

    public static UpdateLogger get() {
        if (instance == null) {
            instance = new UpdateLogger();
        }
        return instance;
    }

    public void setShowLog(boolean log) {
        isShowLog = log;
    }

    public boolean getShowLog() {
        return isShowLog;
    }

    public void i(String log) {
        if (isShowLog) {
            Log.i(TAG, log);
        }
    }

    public void d(String log) {
        if (isShowLog) {
            Log.d(TAG, log);
        }
    }

    public void e(String log) {
        if (isShowLog) {
            Log.e(TAG, log);
        }
    }


}
