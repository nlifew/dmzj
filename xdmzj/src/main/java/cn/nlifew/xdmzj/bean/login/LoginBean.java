package cn.nlifew.xdmzj.bean.login;

import cn.nlifew.xdmzj.bean.BeanSupport;

public class LoginBean extends BeanSupport {
    public int result;
    public String msg;
    public DataType data;

    public static final class DataType {
        public String uid;
        public String nickname;
        public String dmzj_token;
        public String photo;
        public String bind_phone;
        public String email;
        public String passwd;
    }
}
