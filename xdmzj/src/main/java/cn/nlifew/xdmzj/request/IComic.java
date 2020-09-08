package cn.nlifew.xdmzj.request;

import java.util.Map;

import cn.nlifew.xdmzj.bean.SimpleBean;
import cn.nlifew.xdmzj.bean.comic.CommentBean;
import cn.nlifew.xdmzj.bean.comic.ComiclBean;
import cn.nlifew.xdmzj.bean.comic.NoticeBean;
import cn.nlifew.xdmzj.bean.comic.RecordBean;
import cn.nlifew.xdmzj.interceptor.HeaderInterceptor;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface IComic {

    @GET("/comment2/getTopComment/4/4/{id}.json")
    Call<NoticeBean> getComicNotice(@Path("id") int id);

    @GET("/comic/comic_{id}.json")
    Call<ComiclBean> getComicDetail(@Path("id") int id);

    @GET("/subscribe/0/{uid}/{id}")
    Call<SimpleBean> getSubscribeState(@Path("uid") int uid,
                                       @Path("id") String id);

    /**
     * timestamp=1594184581
     * uid=106339990
     * channel=Android
     * type=mh
     * dmzj_token=cf200fc9062f658c42cf748bdc745911
     * version=2.7.017
     * obj_ids=50709
     */
    @GET("/subscribe/cancel")
    Call<SimpleBean> unsubscribe(@QueryMap Map<String, String> map);


    /**
     * type=mh
     * uid=106339990
     * dmzj_token=cf200fc9062f658c42cf748bdc745911
     * obj_ids=50709
     */
    @FormUrlEncoded
    @POST("/subscribe/add")
    Call<SimpleBean> subscribe(@FieldMap Map<String, String> map);


    @GET("http://v3comment.dmzj.com/v1/4/latest/{id}")
    Call<CommentBean> getComicComment(@Path("id") int id,
                                      @Query("page_index") int pageIndex,
                                      @Query("limit") int limit);

    /**
     * obj_id=55154
     * to_comment_id=28780389
     * to_uid=11005856
     * sender_terminal=1
     * dmzj_token=cf200fc9062f658c42cf748bdc745911
     * origin_comment_id=28780389
     * content=%E6%88%91%E4
     */
    @FormUrlEncoded
    @POST("http://v3comment.dmzj.com/v1/4/add/app")
    Call<SimpleBean> sendComment(@FieldMap Map<String, String> map);

    /**
     * obj_id=55154
     * channel=Android
     * comment_id=28783998
     * version=2.7.017
     * timestamp=1594207767
     */
    @GET("http://v3comment.dmzj.com/v1/4/like/{id}")
    @Deprecated
    Call<SimpleBean> starComment(@Path("id") int commentId,
                                 @QueryMap Map<String, String> map);



    @GET("http://v3comment.dmzj.com/v1/4/like/{id}")
    Call<SimpleBean> starComment(@Path("id") int commentId,
                                 @Query("obj_id") int comicId,
                                 @Query("comment_id") int commentId2);


    @GET("https://interface.dmzj.com/api/getReInfo/comic/{uid}/{comic_id}/0")
    Call<RecordBean> getComicRecord(@Path("uid") int uid,
                                    @Path("comic_id") int comic_id);
}
