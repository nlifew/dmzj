package cn.nlifew.dmzj.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public final class ClipboardUtils {
    private static final String TAG = "ClipboardUtils";


    public static void setPrimaryClip(Context context, CharSequence clip) {
        ClipboardManager cm = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText(context.getPackageName(), clip));
            ToastUtils.getInstance(context).show("已复制到剪贴板");
        }
        else {
            ToastUtils.getInstance(context).show("访问剪贴板失败");
        }
    }
}
