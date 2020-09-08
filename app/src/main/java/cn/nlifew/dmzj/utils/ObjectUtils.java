package cn.nlifew.dmzj.utils;

import android.util.Log;

public final class ObjectUtils {
    private static final String TAG = "ObjectUtils";

    public static void wait(Object object) {
        try {
            object.wait();
        } catch (InterruptedException e) {
            Log.e(TAG, "wait: " + object, e);
        }
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Log.e(TAG, "sleep: " + time, e);
        }
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }
}
