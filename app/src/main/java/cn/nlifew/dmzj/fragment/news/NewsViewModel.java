package cn.nlifew.dmzj.fragment.news;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import cn.nlifew.dmzj.fragment.loadmore.BaseViewModel;
import cn.nlifew.xdmzj.bean.news.NewsBean;
import cn.nlifew.xdmzj.bean.news.NewsHeaderBean;
import cn.nlifew.xdmzj.request.INews;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsViewModel extends BaseViewModel {
    private static final String TAG = "NewsViewModel";


    private int mType, mUnknown;
    private final MutableLiveData<NewsHeaderBean> mNewsHeader
            = new MutableLiveData<>(null);

    void setUrl(int type, int unknown) {
        mType = type;
        mUnknown = unknown;
    }

    void loadNewsHeader() {
        INews news = NetworkUtils.create(INews.class);
        Call<NewsHeaderBean> call = news.getNewsHeader();
        call.enqueue(new Callback<NewsHeaderBean>() {
            @Override
            public void onResponse(Call<NewsHeaderBean> call, Response<NewsHeaderBean> response) {
                NewsHeaderBean bean = response.body();
                if (bean.code != 0 || bean.data == null) {
                    mErrMsg.postValue(bean.code + " " + bean.msg);
                    return;
                }
                mNewsHeader.postValue(bean);
            }

            @Override
            public void onFailure(Call<NewsHeaderBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    @Override
    public void refreshDataList() {
        mPageIndex = 0;
        loadDataList(DataWrapper.TYPE_REFRESH, 0);
    }

    @Override
    public void loadMoreDataList() {
        loadDataList(DataWrapper.TYPE_LOAD_MORE, mPageIndex + 1);
    }

    private void loadDataList(int type, int page) {
        INews news = NetworkUtils.create(INews.class);
        Call<NewsBean[]> call = news.getNewsList(mType, mUnknown, page);
        call.enqueue(new Callback<NewsBean[]>() {
            @Override
            public void onResponse(Call<NewsBean[]> call, Response<NewsBean[]> response) {
                NewsBean[] beans = response.body();
                mDataWrapper.postValue(new DataWrapper<>(type, beans));
                mPageIndex = page;
            }

            @Override
            public void onFailure(Call<NewsBean[]> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    LiveData<NewsHeaderBean> newsHeader() {
        return mNewsHeader;
    }
}
