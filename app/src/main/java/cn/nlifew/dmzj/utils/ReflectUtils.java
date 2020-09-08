package cn.nlifew.dmzj.utils;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

public final class ReflectUtils {
    private static final String TAG = "ReflectUtils";

    public static <T> T newInstance(Class<T> cls, Class<?> paramClass, Object param) {
        if (cls == null) {
            return null;
        }

        try {
            Constructor<T> constructor = cls.getConstructor(paramClass);
            constructor.setAccessible(true);
            return constructor.newInstance(param);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "newInstance: is this your ordered constructor ?", e);
        } catch (InstantiationException e) {
            Log.w(TAG, "newInstance: is this a abstract class ?", e);
        } catch (IllegalAccessException e) {
            Log.w(TAG, "newInstance: is this a public class ?", e);
        } catch (InvocationTargetException e) {
            Log.w(TAG, "newInstance: a exception occurred when instance", e);
        }
        return null;
    }

    public static <T> T newInstance(Class<T> cls, Class<?>[] types, Object[] params) {
        if (cls == null) {
            return null;
        }
        try {
            Constructor<T> constructor = cls.getConstructor(types);
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "newInstance: no such constructor: " + Arrays.toString(types), e);
        }  catch (InstantiationException e) {
            Log.w(TAG, "newInstance: is this a abstract class ?", e);
        } catch (IllegalAccessException e) {
            Log.w(TAG, "newInstance: is this public ?", e);
        } catch (InvocationTargetException e) {
            Log.w(TAG, "newInstance: a exception occurred when instance", e);
        }
        return null;
    }

    public static <T> T newInstance(Class<T> cls) {
        if (cls == null) {
            return null;
        }
        try {
            return cls.newInstance();
        } catch (InstantiationException e) {
            Log.w(TAG, "newInstance: is this a abstract class ?", e);
        } catch (IllegalAccessException e) {
            Log.w(TAG, "newInstance: is this public ?", e);
        }
        return null;
    }

}
