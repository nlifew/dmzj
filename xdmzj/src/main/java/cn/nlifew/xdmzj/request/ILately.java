package cn.nlifew.xdmzj.request;

import cn.nlifew.xdmzj.bean.lately.LatelyBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ILately {


    @GET("/latest/100/{page}.json")
    Call<LatelyBean[]> getComicList(@Path("page") int page);
}
