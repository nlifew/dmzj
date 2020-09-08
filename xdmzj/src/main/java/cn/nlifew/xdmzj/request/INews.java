package cn.nlifew.xdmzj.request;

import cn.nlifew.xdmzj.bean.news.NewsBean;
import cn.nlifew.xdmzj.bean.news.NewsHeaderBean;
import cn.nlifew.xdmzj.interceptor.HeaderInterceptor;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface INews {

    @GET("/v3/article/list/{type}/{unknown}/{page}.json")
    Call<NewsBean[]> getNewsList(@Path("type") int type,
                                 @Path("unknown") int unknown,
                                 @Path("page") int page);


    @GET("/v3/article/recommend/header.json")
    Call<NewsHeaderBean> getNewsHeader();
}
