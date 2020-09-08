package cn.nlifew.dmzj.ui.reading;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Arrays;

import cn.nlifew.dmzj.ui.comic.SubscribeViewModel;
import cn.nlifew.xdmzj.bean.comic.ComiclBean;
import cn.nlifew.xdmzj.bean.comic.RecordBean;
import cn.nlifew.xdmzj.bean.reading.ChapterCommentBean;
import cn.nlifew.xdmzj.bean.reading.ReadingBean;
import cn.nlifew.xdmzj.entity.Account;
import cn.nlifew.xdmzj.request.IComic;
import cn.nlifew.xdmzj.request.IReader;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import cn.nlifew.xdmzj.xDmzj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class ReadingViewModel extends SubscribeViewModel {
    private static final String TAG = "ReadingViewModel";

    static final class ChapterWrapper {
        int offset;
        ReadingBean bean;
        int page;

        ChapterWrapper(int offset, ReadingBean bean, int page) {
            this.offset = offset;
            this.bean = bean;
        }
    }

    static final class CommentWrapper {
        int offset;
        ChapterCommentBean[] list;

        CommentWrapper(int offset, ChapterCommentBean[] list) {
            this.offset = offset;
            this.list = list;
        }
    }

    public ReadingViewModel() {  }

    private final MutableLiveData<String> mErrMsg =
            new MutableLiveData<>(null);
    private final MutableLiveData<CommentWrapper> mCommentList
            = new MutableLiveData<>(null);
    private final MutableLiveData<ChapterWrapper> mChapter
            = new MutableLiveData<>(null);

    private int mChapterId, mComicId, mPageIndex;

    private int mChapterIndex = -1;
    private ComiclBean.ChapterDataType[] mChapterList;

    void setParam(String comicId, String chapterId, String pageIndex) {
        mComicId = Integer.parseInt(comicId);
        mChapterId = chapterId == null ? -1 : Integer.parseInt(chapterId);
        mPageIndex = pageIndex == null ? 0 : Integer.parseInt(pageIndex);
    }

    private void loadChapterImpl(int offset) {
        IReader reader = NetworkUtils.create(IReader.class);
        Call<ReadingBean> call = reader.getComicPictures(mComicId,
                mChapterList[mChapterIndex + offset].chapter_id);
        call.enqueue(new Callback<ReadingBean>() {
            @Override
            public void onResponse(Call<ReadingBean> call, Response<ReadingBean> response) {
                ReadingBean bean = response.body();
                mChapter.postValue(new ChapterWrapper(offset, bean, mPageIndex));
            }

            @Override
            public void onFailure(Call<ReadingBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    private void loadCurrentChapter() {
        IComic detail = NetworkUtils.create(IComic.class);
        Call<ComiclBean> call = detail.getComicDetail(mComicId);
        call.enqueue(new Callback<ComiclBean>() {
            @Override
            public void onResponse(Call<ComiclBean> call, Response<ComiclBean> response) {
                ComiclBean bean = response.body();
                Log.d(TAG, "onResponse: chapterId: " + mChapterId);

                // 如果未指定章节，尝试从书签中找到
                RecordBean bookMark;
                if (mChapterId == -1 && (bookMark = findBookMark()) != null) {
                    Log.d(TAG, "onResponse: found in bookmark: " + bookMark.chapter_id);

                    mChapterId = bookMark.chapter_id;
                    mPageIndex = bookMark.record;
                }

                // 尝试找到所选章节所在的组
                if ((mChapterList = findChapterList(bean, mChapterId)) == null) {
                    Log.d(TAG, "onResponse: missing chapterId, choose the first");
                    mChapterList = bean.chapters[0].data;
                    mChapterId = -1;
                    mPageIndex = 0;
                }

                // 先排序，因为 dmzj 返回数据中的顺序是乱的
                Arrays.sort(mChapterList, (o1, o2) -> o1.chapter_order - o2.chapter_order);

                // 尝试在组中找到所选章节
                mChapterIndex = mChapterId == -1 ? 0 : indexOf(mChapterList, mChapterId);

                loadChapterImpl(0);
            }

            private ComiclBean.ChapterDataType[] findChapterList(ComiclBean bean, int chapterId) {
                if (chapterId == -1) {
                    return null;
                }
                for (ComiclBean.ChapterType g : bean.chapters) {
                    if (indexOf(g.data, chapterId) != -1) {
                        return g.data;
                    }
                }
                return null;
            }

            private int indexOf(ComiclBean.ChapterDataType[] array, int id) {
                for (int i = 0; i < array.length; i++) {
                    if (id == array[i].chapter_id) {
                        return i;
                    }
                }
                return -1;
            }

            RecordBean findBookMark() {
                Account account = Account.getInstance();
                if (account == null) {
                    return null;
                }
                Call<RecordBean> c = detail.getComicRecord(account.id, mComicId);
                try {
                    Response<RecordBean> resp = c.execute();
                    return resp.body();
                } catch (Throwable e) {
                    Log.e(TAG, "findBookMark: " + c.request(), e);
                }
                return null;
            }

            @Override
            public void onFailure(Call<ComiclBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }


    void loadChapter(int offset) {
        if (mChapterList == null) {
            loadCurrentChapter();
            return;
        }

        if (mChapterIndex + offset < 0) {
            mErrMsg.postValue("没有更多数据");
        }
        else if (mChapterIndex + offset >= mChapterList.length) {
            mErrMsg.postValue("没有更多数据");
        }
        else {
            loadChapterImpl(offset);
        }
    }

    void loadComment(int offset) {
        // todo
    }

    LiveData<String> errMsg() { return mErrMsg; }

    LiveData<CommentWrapper> commentList() { return mCommentList; }

    LiveData<ChapterWrapper> chapter() { return mChapter; }
}
