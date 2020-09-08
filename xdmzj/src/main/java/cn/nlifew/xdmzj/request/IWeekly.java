package cn.nlifew.xdmzj.request;

import cn.nlifew.xdmzj.bean.weekly.DetailWeeklyBean;
import cn.nlifew.xdmzj.bean.weekly.SimpleWeeklyBean;
import cn.nlifew.xdmzj.interceptor.HeaderInterceptor;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface IWeekly {

    @GET("/subject/0/{page}.json")
    Call<SimpleWeeklyBean[]> getWeeklyGroup(@Path("page") int page);


    @GET("/subject/{id}.json")
    Call<DetailWeeklyBean> getDetailWeekly(@Path("id") int id);
}
