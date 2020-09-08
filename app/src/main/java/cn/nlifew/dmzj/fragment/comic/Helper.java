package cn.nlifew.dmzj.fragment.comic;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;


import cn.nlifew.xdmzj.bean.comic.ComiclBean;

import static cn.nlifew.dmzj.utils.TimeUtils.DAY;
import static cn.nlifew.dmzj.utils.TimeUtils.HOUR;
import static cn.nlifew.dmzj.utils.TimeUtils.MINUTE;
import static cn.nlifew.dmzj.utils.TimeUtils.MONTH;
import static cn.nlifew.dmzj.utils.TimeUtils.SECOND;
import static cn.nlifew.dmzj.utils.TimeUtils.YEAR;

final class Helper {

    static SpannableStringBuilder toSpanText(SpannableStringBuilder sb,
                                             int num, String bottom) {
        sb.clear();

        String numberUnit = "";
        if (num >= 10000) {
            num = Math.round(num / 10000f);
            numberUnit = "万";
        }
        int i = sb.append(Integer.toString(num)).length();
        sb.setSpan(DP18, 0, i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(BOLD, 0, i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb.append(numberUnit).append('\n');
        sb.append(bottom, GRAY, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    private static final AbsoluteSizeSpan DP18 = new AbsoluteSizeSpan(18, true);
    private static final StyleSpan BOLD = new StyleSpan(Typeface.BOLD_ITALIC);
    private static final ForegroundColorSpan GRAY = new ForegroundColorSpan(Color.GRAY);


    static String toCategory(ComiclBean data) {
        int sum = 0;
        for (ComiclBean.ChapterType chapters : data.chapters) {
            sum += chapters.data.length;
        }

        StringBuilder sb = new StringBuilder(32);
        if ("已完结".equals(data.status[0].tag_name)) {
            return sb.append("已完结 - 共").append(sum).append("章").toString();
        }

        sb.append("连载至").append(data.last_update_chapter_name).append(" - ");

        long time = System.currentTimeMillis() - data.last_updatetime * 1000;

        if (time >= YEAR)
            sb.append((int) (time / YEAR)).append("年");
        else if (time >= MONTH)
            sb.append((int) (time / MONTH)).append("月");
        else if (time >= DAY)
            sb.append((int) (time / DAY)).append("天");
        else if (time >= HOUR)
            sb.append((int) (time / HOUR)).append("小时");
        else if (time >= MINUTE)
            sb.append((int) (time / MINUTE)).append("分钟");
        else
            sb.append((int) (time / SECOND)).append("秒");

        return sb.append("前更新").toString();
    }

    static String toAuthor(ComiclBean data) {
        if (data.authors.length == 1) {
            return data.authors[0].tag_name;
        }
        StringBuilder sb = new StringBuilder();
        for (ComiclBean.AuthorType author : data.authors) {
            sb.append(author.tag_name).append(' ');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    static ProgressDialog makeProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("请稍候");
        dialog.setMessage("正在发送 ...");
        dialog.setCancelable(false);
        return dialog;
    }
}
