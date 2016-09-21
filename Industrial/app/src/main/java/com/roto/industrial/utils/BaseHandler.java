package com.roto.industrial.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.roto.industrial.base.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * Created by hardy on 16/9/21.
 */

public abstract class BaseHandler extends Handler {
    private WeakReference<BaseActivity> mOuter;

    public BaseHandler(BaseActivity activity) {
        mOuter = new WeakReference<BaseActivity>(activity);
    }

    public BaseHandler(BaseActivity activity, Looper looper) {
        super(looper);
        mOuter = new WeakReference<BaseActivity>(activity);
    }

    public BaseHandler(BaseActivity activity, Looper looper, Callback callback) {
        super(looper, callback);
        mOuter = new WeakReference<BaseActivity>(activity);
    }

    @Override
    public final void handleMessage(Message msg) {
        BaseActivity activity = mOuter.get();
        if (activity != null && !activity.isFinishing()) {
            handleRealMessage(msg);
        } else {
            removeCallbacksAndMessages(null);
        }
    }

    public abstract void handleRealMessage(Message msg);
}
