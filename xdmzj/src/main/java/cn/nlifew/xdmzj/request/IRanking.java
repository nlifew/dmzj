package cn.nlifew.xdmzj.request;

import cn.nlifew.xdmzj.bean.ranking.RankingComicBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IRanking {


    @GET("/rank/{type}/{time}/{sort}/{page}.json")
    Call<RankingComicBean[]> getComicList(@Path("type") int type,
                                          @Path("time") int time,
                                          @Path("sort") int sort,
                                          @Path("page") int page);
}
