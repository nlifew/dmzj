package cn.nlifew.dmzj.app;

import android.app.Application;
import android.os.Handler;

import cn.nlifew.xdmzj.xDmzj;

public class ThisApp extends Application {
    private static final String TAG = "ThisApp";

    public static ThisApp currentApplication;
    public static final Handler mH = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        currentApplication = this;
        xDmzj.install(this);
    }
}
