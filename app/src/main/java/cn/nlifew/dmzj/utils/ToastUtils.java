package cn.nlifew.dmzj.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

import cn.nlifew.dmzj.app.ThisApp;

public final class ToastUtils {
    private static final String TAG = "ToastUtils";

    private static ToastUtils sInstance;
    public static ToastUtils getInstance(Context c) {
        if (sInstance == null) {
            synchronized (ToastUtils.class) {
                if (sInstance == null) {
                    sInstance = new ToastUtils(c);
                }
            }
        }
        return sInstance;
    }

    private ToastUtils(Context c) {
        c = c.getApplicationContext();
        mToast = Toast.makeText(c, "", Toast.LENGTH_LONG);
    }

    private final Toast mToast;

    public void show(@StringRes int text) {
        mToast.setText(text);
        mToast.show();
    }

    public void show(@StringRes int text, int time) {
        mToast.setText(text);
        mToast.setDuration(time);
        mToast.show();
    }

    public void show(CharSequence text) {
        mToast.setText(text);
        mToast.show();
    }

    public void show(CharSequence text, int time) {
        mToast.setText(text);
        mToast.setDuration(time);
        mToast.show();
    }

    public void post(CharSequence text) {
        ThisApp.mH.post(() -> {
            mToast.setText(text);
            mToast.show();
        });
    }
}
