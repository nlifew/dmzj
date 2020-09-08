package cn.nlifew.xdmzj.request;

import cn.nlifew.xdmzj.bean.login.LoginBean;
import cn.nlifew.xdmzj.bean.login.SendSmsBean;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ILogin {

    @POST("https://user.dmzj.com/loginV2/m_confirm")
    @Headers({"User-Agent: okhttp/3.12.1"})
    @FormUrlEncoded
    Call<LoginBean> loginByPassword(@Field("nickname") String nickname,
                                    @Field("passwd") String password);


    @GET("https://user.dmzj.com/register/send_tel_code")
    Call<SendSmsBean> sendSms(@Query("tel") int tel);
}
