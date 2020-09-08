package cn.nlifew.xdmzj.bean.space;

import cn.nlifew.xdmzj.bean.BeanSupport;

public class CommentBean extends BeanSupport {
    public int comment_id;
    public String content;
    public long create_time;
    public int like_amount;
    public MasterType masterComment;
    public String obj_cover;
    public int obj_id;
    public String obj_name;
    public int origin_comment_id;
    public String page_url;
    public int reply_amount;
    public int to_comment_id;

    public static final class MasterType {
        public String content;
        public long create_time;
        public int id;
        public int like_amount;
        public String nickname;
        public int reply_amount;
        public int sender_uid;
    }
}
