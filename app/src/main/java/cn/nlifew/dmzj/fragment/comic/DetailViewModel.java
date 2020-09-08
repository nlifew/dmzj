package cn.nlifew.dmzj.fragment.comic;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import cn.nlifew.xdmzj.bean.SimpleBean;
import cn.nlifew.xdmzj.bean.comic.CommentBean;
import cn.nlifew.xdmzj.bean.comic.ComiclBean;
import cn.nlifew.xdmzj.bean.comic.NoticeBean;
import cn.nlifew.xdmzj.database.CommonDatabase;
import cn.nlifew.xdmzj.entity.Account;
import cn.nlifew.xdmzj.entity.Comment;
import cn.nlifew.xdmzj.request.IComic;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import cn.nlifew.xdmzj.xDmzj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class DetailViewModel extends ViewModel {
    private static final String TAG = "DetailViewModel";

    static final class CommentWrapper {
        static final int TYPE_REFRESH = 1;
        static final int TYPE_LOAD_MORE = 2;

        @IntDef({TYPE_LOAD_MORE, TYPE_REFRESH})
        @Retention(RetentionPolicy.SOURCE)
        @interface Type {}

        int type;
        CommentBean bean;

        CommentWrapper(@Type int type, CommentBean bean) {
            this.type = type;
            this.bean = bean;
        }
    }

    public DetailViewModel() {  }

    private MutableLiveData<String> mErrMsg = new MutableLiveData<>(null);
    private MutableLiveData<ComiclBean> mDetailBean = new MutableLiveData<>(null);
    private MutableLiveData<NoticeBean> mNoticeBean = new MutableLiveData<>(null);
    private MutableLiveData<CommentWrapper> mCommentBean = new MutableLiveData<>(null);

    LiveData<String> getErrMsg() {
        return mErrMsg;
    }

    public LiveData<ComiclBean> getDetailBean() {
        return mDetailBean;
    }

    LiveData<CommentWrapper> getCommentBean() {
        return mCommentBean;
    }

    LiveData<NoticeBean> getNoticeBean() {
        return mNoticeBean;
    }

    private int mId;
    private int mPageIndex = 1;
    private int mTotalCount, mCurrentCount;

    void setId(String id) {
        mId = Integer.parseInt(id);
    }

    int getId() { return mId; }

    void loadDetailInfo() {
        IComic detail = NetworkUtils.create(IComic.class);
        Call<ComiclBean> call = detail.getComicDetail(mId);
        call.enqueue(new Callback<ComiclBean>() {
            @Override
            public void onResponse(Call<ComiclBean> call, Response<ComiclBean> response) {
                ComiclBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                mTotalCount = bean.comment.comment_count;
                mDetailBean.postValue(bean);
            }

            @Override
            public void onFailure(Call<ComiclBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                if (t instanceof MalformedJsonException) {
                    // 可能是漫画不存在 ?
                    mErrMsg.postValue("漫画不存在");
                }
                else {
                    mErrMsg.postValue(t.toString());
                }
            }
        });
    }

    void loadNotice() {
        IComic detail = NetworkUtils.create(IComic.class);
        Call<NoticeBean> call = detail.getComicNotice(mId);
        call.enqueue(new Callback<NoticeBean>() {
            @Override
            public void onResponse(Call<NoticeBean> call, Response<NoticeBean> resp) {
                NoticeBean bean;
                if (! resp.isSuccessful() || (bean = resp.body()) == null) {
                    onFailure(call, new IOException(resp.code() + " " + resp.message()));
                    return;
                }
                mNoticeBean.postValue(bean);
            }

            @Override
            public void onFailure(Call<NoticeBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    void loadMoreComment() {
        if (mCurrentCount >= mTotalCount) {
            mErrMsg.postValue("没有更多数据");
            return;
        }
        loadComment(CommentWrapper.TYPE_LOAD_MORE);
    }

    void refreshComment() {
        mPageIndex = 1;
        mCurrentCount = 0;
        loadComment(CommentWrapper.TYPE_REFRESH);
    }

    private void loadComment(@CommentWrapper.Type int type) {
        final int pageIndex = mPageIndex;

        IComic detail = NetworkUtils.create(IComic.class);
        Call<CommentBean> call = detail.getComicComment(mId, pageIndex, 10);
        call.enqueue(new Callback<CommentBean>() {
            @Override
            public void onResponse(Call<CommentBean> call, Response<CommentBean> response) {
                CommentBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                mPageIndex = pageIndex + 1;
                mCurrentCount += bean.commentIds.length;

                // 遍历每条评论并向本地数据库查询
                // 因为大妈之家现在并没有返回“是否点赞过”这个信息
                // 如果本地数据库存在，则认为已经点赞过，并将 is_goods 置位
                Comment.Helper helper = CommonDatabase.getInstance().getCommentHelper();
                for (CommentBean.CommentType comment : bean.comments.values()) {
                    comment.is_goods = helper.findCommentById(comment.id) != null ?
                            1 : 0;
                }

                mCommentBean.postValue(new CommentWrapper(type, bean));
            }

            @Override
            public void onFailure(Call<CommentBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
