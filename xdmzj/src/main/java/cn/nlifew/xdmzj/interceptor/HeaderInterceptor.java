package cn.nlifew.xdmzj.interceptor;

import android.webkit.WebView;

import java.io.IOException;

import cn.nlifew.xdmzj.BuildConfig;
import cn.nlifew.xdmzj.xDmzj;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    private static final String _IGNORE_ADD_HEADER = "ignore_add_header";
    public static final String IGNORE_ADD_HEADER = _IGNORE_ADD_HEADER + ": 1";


    private static final String USER_AGENT;

    static {
        if (BuildConfig.DEBUG) {
            USER_AGENT = "Version/117   Mozilla/5.0 (Linux; Android 6.0.1; ATH-CL00 Build/HONORATH-CL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/55.0.2883.91 Mobile Safari/537.36";
        }
        else {
            WebView webView = new WebView(xDmzj.sInstance);
            USER_AGENT = webView.getSettings().getUserAgentString();
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request old = chain.request();
        Request.Builder request = old.newBuilder();

        if (old.header("User-Agent") == null) {
            request.header("User-Agent", USER_AGENT);
        }

        if (! "GET".equalsIgnoreCase(old.method())) {
            // nothing to do.
        }
        else if (old.header(_IGNORE_ADD_HEADER) != null) {
            request.removeHeader(_IGNORE_ADD_HEADER);
        }
        else {
            HttpUrl url = old
                    .url()
                    .newBuilder()
                    .addQueryParameter("timestamp", Long.toString(System.currentTimeMillis()))
                    .addQueryParameter("channel", xDmzj.CHANNEL)
                    .addQueryParameter("version", xDmzj.VERSION)
                    .build();

            request.url(url);
        }

        return chain.proceed(request.build());
    }
}
