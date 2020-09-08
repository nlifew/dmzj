package cn.nlifew.xdmzj.bean;

import com.google.gson.annotations.SerializedName;

public class SquareBean extends BeanSupport {

    public int category_id;
    public DataType[] data;
    public int sort;
    public String title;

    public static final class DataType {
        public static final int TYPE_COMIC      = 1;
        public static final int TYPE_WEEKLY     = 5;
        public static final int TYPE_URL        = 6;
        public static final int TYPE_NEWS       = 7;
        public static final int TYPE_AUTHOR     = 8;
        public static final int TYPE_AD         = 10;

        public String cover;

        @SerializedName(value = "obj_id", alternate = {"id"})
        public int obj_id;

        public String status;
        public String title;

        @SerializedName(value = "sub_title", alternate = {"authors"})
        public String sub_title;

        public int type;
        public String url;
    }
}
