package cn.nlifew.xdmzj.bean.news;

import cn.nlifew.xdmzj.bean.BeanSupport;

public class NewsHeaderBean extends BeanSupport {
    public int code;
    public String msg;
    public DataType[] data;

    public static final class DataType {
        public int id;
        public int object_id;
        public String object_url;
        public String pic_url;
        public String title;
    }
}
