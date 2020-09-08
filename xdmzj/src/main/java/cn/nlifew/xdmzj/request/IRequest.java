package cn.nlifew.xdmzj.request;

import cn.nlifew.xdmzj.bean.BatchBean;
import cn.nlifew.xdmzj.bean.SquareBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IRequest {

    @GET("/recommend_new.json")
    Call<SquareBean[]> getSquareList();

    @GET("/recommend/batchUpdate")
    Call<BatchBean> getBatchUpdate(@Query("uid") Integer uid,    // [1]
                                   @Query("category_id") int category_id);
    // [1]: uid 的类型必须为 Integer 而不能为 int
    // 因为在用户没有登录时，这个参数是不能携带的，
    // 此时需要传入 null，这样 Retrofit 就会自动忽略掉它
}
