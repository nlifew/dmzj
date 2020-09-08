package cn.nlifew.dmzj.fragment.weekly;

import android.util.Log;

import cn.nlifew.dmzj.fragment.loadmore.BaseViewModel;
import cn.nlifew.xdmzj.bean.weekly.SimpleWeeklyBean;
import cn.nlifew.xdmzj.request.IWeekly;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeeklyViewModel extends BaseViewModel {
    private static final String TAG = "WeeklyViewModel";

    @Override
    public void refreshDataList() {
        mPageIndex = 0;
        loadData(DataWrapper.TYPE_REFRESH, 0);
    }

    @Override
    public void loadMoreDataList() {
        loadData(DataWrapper.TYPE_LOAD_MORE, 1);
    }

    private void loadData(int type, int page) {
        NetworkUtils.create(IWeekly.class)
                .getWeeklyGroup(page)
                .enqueue(new Callback<SimpleWeeklyBean[]>() {
                    @Override
                    public void onResponse(Call<SimpleWeeklyBean[]> call, Response<SimpleWeeklyBean[]> response) {
                        SimpleWeeklyBean[] bean = response.body();
                        mDataWrapper.postValue(new DataWrapper<>(type, bean));
                        mPageIndex = page;
                    }

                    @Override
                    public void onFailure(Call<SimpleWeeklyBean[]> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + call.request(), t);
                        mErrMsg.postValue(t.toString());
                    }
                });
    }
}
