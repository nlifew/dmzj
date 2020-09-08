package cn.nlifew.dmzj.ui.space.comment;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cn.nlifew.dmzj.ui.space.SpaceViewModel;
import cn.nlifew.xdmzj.bean.space.CommentBean;
import cn.nlifew.xdmzj.request.ISpace;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentViewModel extends SpaceViewModel {
    private static final String TAG = "CommentViewModel";

    static final class CommentWrapper {
        public static final int TYPE_REFRESH   = 1;
        public static final int TYPE_LOAD_MORE   = 2;

        public CommentBean[] list;
        public int type;

        CommentWrapper(int type, CommentBean[] list) {
            this.type = type;
            this.list = list;
        }
    }

    public CommentViewModel(String uid, int idType) {
        super(uid, idType);
    }


    private final MutableLiveData<CommentWrapper> mCommentList
            = new MutableLiveData<>(null);

    private int mPageIndex = 0;


    void refreshCommentList() {
        if (mIdType == ID_TYPE_AUTHOR) {
            mErrMsg.postValue("没有更多信息");
            return;
        }

        loadCommentList(CommentWrapper.TYPE_REFRESH, 0);
    }

    void loadMoreCommentList() {
        loadCommentList(CommentWrapper.TYPE_LOAD_MORE, mPageIndex + 1);
    }

    private void loadCommentList(int type, int page) {
        ISpace iSpace = NetworkUtils.create(ISpace.class);
        Call<CommentBean[]> call = iSpace.getUserCommentList(mUid, page);
        call.enqueue(new Callback<CommentBean[]>() {
            @Override
            public void onResponse(Call<CommentBean[]> call, Response<CommentBean[]> response) {
                CommentBean[] bean = response.body();
                mPageIndex = page;
                mCommentList.postValue(new CommentWrapper(type, bean));
            }

            @Override
            public void onFailure(Call<CommentBean[]> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    LiveData<CommentWrapper> commentList() { return mCommentList; }
}
