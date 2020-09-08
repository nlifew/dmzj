package cn.nlifew.xdmzj.bean;

public class BatchBean extends BeanSupport {
    public int code;
    public String msg;
    public DataType data;

    public static final class DataType {
        public int category_id;
        public BookType[] data;
        public String title;
        public int sort;
    }

    public static final class BookType {
        public String authors;
        public String cover;
        public int id;
        public int num;
        public String status;
        public String title;
        public int sub_reader;
        public int uid;
    }
}
