package cn.nlifew.dmzj.ui.weekly;

import java.util.Calendar;

final class Helper {

    static StringBuilder formatDate(long time, StringBuilder sb) {
        Calendar now = Calendar.getInstance();
        Calendar old = Calendar.getInstance();
        old.setTimeInMillis(time);

        int year = old.get(Calendar.YEAR);
        if (year != now.get(Calendar.YEAR)) {
            sb.append(year).append("年");
        }
        sb.append(old.get(Calendar.MONTH) + 1).append("月");
        sb.append(old.get(Calendar.DAY_OF_MONTH)).append("日");
        return sb;
    }


}
