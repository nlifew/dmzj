package cn.nlifew.xdmzj.bean.space;

import cn.nlifew.xdmzj.bean.BeanSupport;

public class SpaceBean extends BeanSupport {
    public int ammount;
    public String birthday;
    public int blood;
    public String constellation;
    public String cover;
    public ComicType[] data;
    public String description;
    public String nickname;
    public int sex;

    public static final class ComicType {
        public String cover;
        public int id;
        public String name;
        public String status;
    }
}
