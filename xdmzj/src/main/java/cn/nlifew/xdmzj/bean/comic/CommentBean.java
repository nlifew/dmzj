package cn.nlifew.xdmzj.bean.comic;

import java.util.Map;

import cn.nlifew.xdmzj.bean.BeanSupport;

public class CommentBean extends BeanSupport {
    public String[] commentIds;
    public Map<String, CommentType> comments;

    public static final class CommentType {
        public String content;
        public int origin_comment_id;
        public int id;
        public int is_goods;
        public long create_time;
        public String sender_ip;
        public int sender_terminal;
        public int sender_uid;
        public int like_amount;
        public int obj_id;
        public String upload_images;
        public String avatar_url;
        public String nickname;
        public int sex;
    }
}
