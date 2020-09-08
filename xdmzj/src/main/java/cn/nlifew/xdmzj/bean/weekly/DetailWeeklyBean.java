package cn.nlifew.xdmzj.bean.weekly;

import cn.nlifew.xdmzj.bean.BeanSupport;

public class DetailWeeklyBean extends BeanSupport {
    private static final String TAG = "DetailWeeklyBean";

    public int comment_amount;
    public String description;
    public String mobile_header_pic;
    public String page_url;
    public String title;
    public ComicType[] comics;

    public static final class ComicType {
        public String alias_name;
        public String cover;
        public int id;
        public String name;
        public String recommend_brief;
        public String recommend_reason;
    }
}
