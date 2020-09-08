package cn.nlifew.xdmzj.request;

import cn.nlifew.xdmzj.bean.space.CommentBean;
import cn.nlifew.xdmzj.bean.space.SpaceBean;
import cn.nlifew.xdmzj.interceptor.HeaderInterceptor;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ISpace {


    @GET("/UCenter/comics/{id}.json")
    Call<SpaceBean> getUserInfo(@Path("id") int uid);



    @GET("/UCenter/author/{id}.json")
    Call<SpaceBean> getAuthorInfo(@Path("id") int uid);



    @GET("/v3/old/comment/owner/0/{id}/{page}.json")
    Call<CommentBean[]> getUserCommentList(@Path("id") int uid,
                                           @Path("page") int page);
}
