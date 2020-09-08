package cn.nlifew.dmzj.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class InputUtils {

    public static void hideSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View view;
        if (imm != null && (view = activity.getCurrentFocus()) != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View view;
        if (imm != null && (view = activity.getCurrentFocus()) != null) {
            imm.showSoftInput(view, 0);
        }
    }
}
