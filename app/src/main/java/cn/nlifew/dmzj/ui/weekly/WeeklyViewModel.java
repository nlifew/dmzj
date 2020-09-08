package cn.nlifew.dmzj.ui.weekly;

import android.util.Log;

import cn.nlifew.dmzj.fragment.loadmore.BaseViewModel;
import cn.nlifew.xdmzj.bean.weekly.DetailWeeklyBean;
import cn.nlifew.xdmzj.request.IWeekly;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeeklyViewModel extends BaseViewModel {
    private static final String TAG = "WeeklyViewModel";


    private int mWeeklyId;

    void setId(String id) {
        mWeeklyId = Integer.parseInt(id, 10);
    }

    @Override
    public void refreshDataList() {
        NetworkUtils.create(IWeekly.class)
                .getDetailWeekly(mWeeklyId)
                .enqueue(new Callback<DetailWeeklyBean>() {
                    @Override
                    public void onResponse(Call<DetailWeeklyBean> call, Response<DetailWeeklyBean> response) {
                        DetailWeeklyBean bean = response.body();
                        mDataWrapper.postValue(new DataWrapper<>(DataWrapper.TYPE_REFRESH, bean));
                    }

                    @Override
                    public void onFailure(Call<DetailWeeklyBean> call, Throwable t) {
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
