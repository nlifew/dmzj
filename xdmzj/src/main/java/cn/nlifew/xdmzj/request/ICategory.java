package cn.nlifew.xdmzj.request;

import cn.nlifew.xdmzj.bean.category.CategoryBean;
import cn.nlifew.xdmzj.bean.category.CategoryComicBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ICategory {

    @GET("/classify/filter.json")
    Call<CategoryBean[]> getCategory();

    @GET("/classify/{filter}/{sort}/{page}.json")
    Call<CategoryComicBean[]> getComicList(@Path("filter") String filter,
                                           @Path("sort") int sort,
                                           @Path("page") int page);
}
