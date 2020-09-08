package cn.nlifew.dmzj.ui.news;

import android.webkit.WebView;

public class WebClient extends cn.nlifew.dmzj.ui.browser.WebClient<NewsActivity> {
    private static final String TAG = "WebClient";

    public WebClient(NewsActivity activity) {
        super(activity);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

//        injectRemoveHeaderJs(view);
    }

    private void injectRemoveHeaderJs(WebView view) {
        final String js = "\n" +
                "(function() {\n" +
                "    var header = $('body > div.header');\n" +
                "    var header_prev;\n" +
                "    if (header != null && (header_prev = header.prev()) != null) {\n" +
                "        header.remove();\n" +
                "        header_prev.remove();\n" +
                "    }\n" +
                "    \n" +
                "    var topImg = $('body > div.newsmain > div.topImg')\n" +
                "    if (topImg != null) {\n" +
                "       topImg.remove();\n" +
                "    }\n" +
                "    var share = $('body > div.newsmain > div.news_content > div.shares.bdsharebuttonbox');\n" +
                "    var share_prev, share_next;\n" +
                "    if (share != null && (share_prev = share.prev()) != null \n" +
                "            && (share_next = share.next()) != null) {\n" +
                "        share.remove();\n" +
                "        share_prev.remove();\n" +
                "        share_next.remove();\n" +
                "    }\n" +
                "    \n" +
                "    var mood = $('#mood');\n" +
                "    if (mood != null) {\n" +
                "        mood.remove();\n" +
                "    }\n" +
                "    \n" +
                "})();";
        view.evaluateJavascript(js, null);
    }
}
