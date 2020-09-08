package cn.nlifew.dmzj.ui.browser;

import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class ChromeClient<T extends BrowserActivity> extends WebChromeClient {


    public ChromeClient(T activity) {
        mActivity = new WeakReference<>(activity);
    }

    private final WeakReference<T> mActivity;


    public @Nullable T getActivity() { return mActivity.get(); }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);

        BrowserActivity activity = mActivity.get();
        if (activity != null) {
            activity.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);

        BrowserActivity activity = mActivity.get();
        if (activity != null) {
            activity.onReceivedTitle(view, title);
        }
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        BrowserActivity activity = mActivity.get();
        if (activity != null) {
            activity.openFileChooser(webView, filePathCallback, fileChooserParams);
            return true;
        }
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }
}
