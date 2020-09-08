package cn.nlifew.dmzj.fragment.lately;

import android.util.Log;

import cn.nlifew.dmzj.fragment.loadmore.BaseViewModel;
import cn.nlifew.xdmzj.bean.lately.LatelyBean;
import cn.nlifew.xdmzj.request.ILately;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LatelyViewModel extends BaseViewModel {
    private static final String TAG = "LatelyViewModel";

    @Override
    public void refreshDataList() {
        mPageIndex = 0;
        loadComicList(BaseViewModel.DataWrapper.TYPE_REFRESH, 0);
    }

    @Override
    public void loadMoreDataList() {
        loadComicList(BaseViewModel.DataWrapper.TYPE_LOAD_MORE, mPageIndex + 1);
    }

    private void loadComicList(int type, int page) {
        ILately lately = NetworkUtils.create(ILately.class);
        Call<LatelyBean[]> call = lately.getComicList(page);
        call.enqueue(new Callback<LatelyBean[]>() {
            @Override
            public void onResponse(Call<LatelyBean[]> call, Response<LatelyBean[]> response) {
                LatelyBean[] list = response.body();
                mPageIndex = page;
                mDataWrapper.postValue(new DataWrapper<>(type, list));
            }

            @Override
            public void onFailure(Call<LatelyBean[]> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
