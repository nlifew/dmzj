package cn.nlifew.dmzj.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import cn.nlifew.dmzj.app.ThisApp;

public final class DisplayUtils {

    public static Point getDisplaySize(Context context, Point point) {
        if (point == null) {
            point = new Point();
        }
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(point);
        return point;
    }



    public static int dp2px(float dp) {
        float density = ThisApp
                .currentApplication
                .getResources()
                .getDisplayMetrics()
                .density;
        return (int) (dp * density + 0.5f);
    }
    
    public static int px2sp(Context context, float px) {
        float density = context
                .getResources()
                .getDisplayMetrics()
                .scaledDensity;
        return (int) (px / density + 0.5f);
    }

    public static int sp2px(Context context, float sp) {
        float density = context
                .getResources()
                .getDisplayMetrics()
                .scaledDensity;
        return (int) (sp * density + 0.5f);
    }

    public static int sp2px(float sp) {
        float density = ThisApp
                .currentApplication
                .getResources()
                .getDisplayMetrics()
                .scaledDensity;
        return (int) (sp * density + 0.5f);
    }
    
    public static int px2dp(float px) {
        float density = ThisApp
                .currentApplication
                .getResources()
                .getDisplayMetrics()
                .density;
        return (int) (px / density + 0.5f);
    }
}
