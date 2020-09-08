package cn.nlifew.dmzj.ui.browser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.material.appbar.AppBarLayout;

import org.chromium.customtabsdemos.CustomTabsHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.ui.BaseActivity;
import cn.nlifew.dmzj.utils.ClipboardUtils;
import cn.nlifew.dmzj.utils.ReflectUtils;
import cn.nlifew.dmzj.utils.ToastUtils;

@BrowserActivity.WebClientFactory
public class BrowserActivity extends BaseActivity {
    private static final String TAG = "BrowserActivity";


    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface WebClientFactory {
        Class<? extends WebClient> client() default WebClient.class;
        Class<? extends ChromeClient> chrome() default ChromeClient.class;
    }

    public static void openUrl(Activity activity, String url) {
        String chrome = CustomTabsHelper.getPackageNameToUse(activity);
        if (chrome == null) {
            Log.i(TAG, "openUrl: no chrome found, use default WebView");
            Intent intent = new Intent(activity, BrowserActivity.class);
            intent.setData(Uri.parse(url));
            activity.startActivity(intent);
        }
        else {
            Log.i(TAG, "openUrl: chrome found " + chrome);
            CustomTabsIntent intent = new CustomTabsIntent.Builder()
                    .setToolbarColor(activity.getColor(R.color.colorPrimary))
//                    .se
                    .build();
            intent.intent.setPackage(chrome);
            intent.launchUrl(activity, Uri.parse(url));
        }
    }


    @SuppressWarnings("SetJavaScriptEnabled")
    protected WebView onCreateWebView() {
        WebView webView = new WebView(getApplicationContext());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
//        settings.setSupportMultipleWindows(true);
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        WebClientFactory config = getClass().getAnnotation(WebClientFactory.class);

        if (config != null) {
            WebClient client = newInstance(config.client());
            if (client != null) webView.setWebViewClient(client);

            ChromeClient chrome = newInstance(config.chrome());
            if (chrome != null) webView.setWebChromeClient(chrome);
        }

        return webView;
    }

    private <T> T newInstance(Class<T> cls) {
        T t = ReflectUtils.newInstance(cls, getClass(), this);
        if (t == null) {
            t = ReflectUtils.newInstance(cls, BrowserActivity.class, this);
        }
        return t;
    }

    private static void destroyWebView(WebView webView) {
        ViewGroup parent = (ViewGroup) webView.getParent();
        if (parent != null) {
            parent.removeView(webView);
        }
        webView.stopLoading();
        webView.clearHistory();
        webView.removeAllViews();
        webView.destroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useDefaultLayout(null);


        AppBarLayout appBar = findViewById(R.id.activity_base_appBar);
        mProgressBar = LayoutInflater.from(this)
                .inflate(R.layout.activity_browser_progress, appBar, true)
                .findViewById(R.id.activity_browser_progress);

        mWebView = onCreateWebView();
        FrameLayout layout = findViewById(R.id.activity_base_host);
        layout.addView(mWebView, FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        Uri uri = Objects.requireNonNull(getIntent().getData());
        mWebView.loadUrl(uri.toString());
    }

    private ProgressBar mProgressBar;
    private WebView mWebView;

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyWebView(mWebView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_browser_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_browser_refresh:
                mWebView.reload();
                return true;
            case R.id.activity_browser_copy_url:
                ClipboardUtils.setPrimaryClip(this, mWebView.getUrl());
                return true;
            case R.id.activity_browser_clear_cache:
                mWebView.clearCache(true);
                ToastUtils.getInstance(this).show("清除缓存完成");
                return true;
            case R.id.activity_browser_clear_cookie:
                CookieManager cm = CookieManager.getInstance();
                cm.removeAllCookie();
                ToastUtils.getInstance(this).show("Cookie清除完成");
                return true;
            case R.id.activity_browser_open_in_browser:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mWebView.getUrl()));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "onOptionsItemSelected: ", e);
                    ToastUtils.getInstance(this).show("打开浏览器失败");
                }
                return true;
            case R.id.activity_browser_share:
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND)
                            .setType("text/plain")
                            .putExtra(Intent.EXTRA_SUBJECT, "分享链接")
                            .putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                    intent = Intent.createChooser(intent, "选择分享到的应用");
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "onOptionsItemSelected: ", e);
                    ToastUtils.getInstance(this).show(e.toString());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void onProgressChanged(WebView view, int newProgress) {
        mProgressBar.setVisibility(newProgress == 100 ? View.INVISIBLE : View.VISIBLE);
        mProgressBar.setProgress(newProgress);
    }

    void onReceivedTitle(WebView view, String title) {
        setTitle(title);
    }


    private static final int CODE_REQUEST_CHOOSER = 10;
    private ValueCallback<Uri[]> mFileChooserCallback;

    void openFileChooser(WebView webView, ValueCallback<Uri[]> callback,
                         WebChromeClient.FileChooserParams params) {
        mFileChooserCallback = callback;
        try {
            Intent intent = params.createIntent();
            startActivityForResult(intent, CODE_REQUEST_CHOOSER);
        } catch (Exception e) {
            Log.e(TAG, "openFileChooser: ", e);
            ToastUtils.getInstance(this).show("打开文档失败");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CODE_REQUEST_CHOOSER && mFileChooserCallback != null) {
            Uri[] uris = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
            mFileChooserCallback.onReceiveValue(uris);
            mFileChooserCallback = null;
        }
    }
}
