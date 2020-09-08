package cn.nlifew.dmzj.ui.browser;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class WebClient<T extends BrowserActivity> extends WebViewClient {
    private static final String HISTORY_GO = "dmzj://history.go?index=";


    public WebClient(T activity) {
        mActivity = new WeakReference<>(activity);
    }

    private final WeakReference<T> mActivity;

    public @Nullable T getActivity() { return mActivity.get(); }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        final String url = request.getUrl().toString();

        if (url.startsWith(HISTORY_GO)) {
            handleHistoryGo(view, url);
            return true;
        }

        view.loadUrl(url);
        return true;
    }

    private void handleHistoryGo(WebView view, String url) {
        int index = Integer.parseInt(url.substring(HISTORY_GO.length()));
        if (index < 0 && view.canGoBack()) {
            view.goBack();
        }
        else if (index > 0 && view.canGoForward()) {
            view.goForward();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        injectHistoryGoJs(view);
    }

    private void injectHistoryGoJs(WebView view) {
        final String js = "(function() {\n" +
                "    \n" +
                "    window.history.go = function(idx) {\n" +
                "        window.location.href = '" + HISTORY_GO + "' + idx\n" +
                "    };\n" +
                "    \n" +
                "    \n" +
                "})();";
        view.evaluateJavascript(js, null);
    }
}
