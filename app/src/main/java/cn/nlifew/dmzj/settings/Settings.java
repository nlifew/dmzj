package cn.nlifew.dmzj.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import cn.nlifew.xdmzj.utils.Singleton;

public final class Settings {
    private static final String TAG = "Settings";

    private static Settings sInstance;
    public static Settings getInstance(Context context) {
        if (sInstance == null) {
            synchronized (Settings.class) {
                if (sInstance == null) {
                    sInstance = new Settings(context);
                }
            }
        }
        return sInstance;
    }

    public static final String PREF_NAME = "settings";

    private Settings(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences mPref;

    public boolean isNightMode() {
        return AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_YES;
    }

    public void setNightMode(boolean night) {
        AppCompatDelegate.setDefaultNightMode(night ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
