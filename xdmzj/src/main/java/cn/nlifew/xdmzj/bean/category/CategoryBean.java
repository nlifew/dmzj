package cn.nlifew.xdmzj.bean.category;

import cn.nlifew.xdmzj.bean.BeanSupport;

public class CategoryBean extends BeanSupport {

    public ItemType[] items;
    public String title;

    public static final class ItemType {
        public int tag_id;
        public String tag_name;

        public ItemType() { }
        public ItemType(int id, String name) {
            this.tag_id = id;
            this.tag_name = name;
        }
    }
}
