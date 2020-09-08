package cn.nlifew.dmzj.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

public final class ViewUtils {
    private static final String TAG = "ViewUtils";

    public static int getMeasuredHeightWithMargins(@Nullable View view) {
        if (view == null) {
            return 0;
        }

        int h = view.getMeasuredHeight();
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) lp;
            h += p.topMargin + p.bottomMargin;
        }
        return h;
    }

    public static int getMeasuredWidthWithMargins(@Nullable View view) {
        if (view == null) {
            return 0;
        }

        int w = view.getMeasuredWidth();
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) lp;
            w += p.leftMargin + p.rightMargin;
        }
        return w;
    }
}
