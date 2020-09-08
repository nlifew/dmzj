package cn.nlifew.dmzj.fragment.bookmark;

import android.util.Log;

import cn.nlifew.dmzj.fragment.loadmore.BaseViewModel;
import cn.nlifew.xdmzj.bean.comic.RecordBean;
import cn.nlifew.xdmzj.entity.Account;
import cn.nlifew.xdmzj.request.IComic;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import cn.nlifew.xdmzj.xDmzj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkViewModel extends BaseViewModel {
    private static final String TAG = "BookmarkViewModel";

    private int mBookId;

    void setBookId(int bookId) { mBookId = bookId; }

    int getBookId() { return mBookId; }

    @Override
    public void refreshDataList() {
        Account account = Account.getInstance();
        if (account == null) {
            mErrMsg.postValue("您还没有登录");
            return;
        }

        IComic detail = NetworkUtils.create(IComic.class);
        Call<RecordBean> call = detail.getComicRecord(account.id, mBookId);
        call.enqueue(new Callback<RecordBean>() {
            @Override
            public void onResponse(Call<RecordBean> call, Response<RecordBean> response) {
                RecordBean bean = response.body();
                mDataWrapper.postValue(new DataWrapper<>(
                        DataWrapper.TYPE_REFRESH,
                        new RecordBean[] { bean }));
            }

            @Override
            public void onFailure(Call<RecordBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    @Override
    public void loadMoreDataList() {
        throw new UnsupportedOperationException("loadMoreDataList");
    }
}
