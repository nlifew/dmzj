package cn.nlifew.xdmzj;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class xDmzj {
    private static final String TAG = "xDmzj";

    public static Application sInstance;

    public static void install(Application app) {
        sInstance = app;
    }

    public static final String VERSION = "2.7.017";
    public static final String CHANNEL = "Android";
}
