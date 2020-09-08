package cn.nlifew.xdmzj.request;

import cn.nlifew.xdmzj.bean.reading.ChapterCommentBean;
import cn.nlifew.xdmzj.bean.reading.ReadingBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IReader {

    @GET("/chapter/{comic_id}/{chapter_id}.json")
    Call<ReadingBean> getComicPictures(@Path("comic_id") int comicId,
                                       @Path("chapter_id") int chapterId);

    @GET("/viewPoint/0/{comic_id}/{chapter_id}.json")
    Call<ChapterCommentBean[]> getComicChapterComment(@Path("comic_id") int comicId,
                                                      @Path("chapter_id") int chapterId);
}
