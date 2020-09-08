package cn.nlifew.dmzj.fragment.square;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cn.nlifew.dmzj.utils.ObjectUtils;
import cn.nlifew.xdmzj.xDmzj;
import cn.nlifew.xdmzj.bean.BatchBean;
import cn.nlifew.xdmzj.bean.SquareBean;
import cn.nlifew.xdmzj.entity.Account;
import cn.nlifew.xdmzj.request.IRequest;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class SquareViewModel extends ViewModel {
    private static final String TAG = "SquareViewModel";

    public SquareViewModel() {  }


    private final MutableLiveData<String> mErrMsg = new MutableLiveData<>(null);
    private final LiveDataImpl<List<SquareBean>> mSquareList = new LiveDataImpl<>(null);

    void loadSquareList() {
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<SquareBean[]> call = request.getSquareList();
        call.enqueue(new Callback<SquareBean[]>() {
            @Override
            public void onResponse(Call<SquareBean[]> call, Response<SquareBean[]> response) {
                SquareBean[] bean;
                if (!response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                List<SquareBean> list = new ArrayList<>(bean.length + 2);
                list.addAll(Arrays.asList(bean)); // [1]
                Collections.sort(list, (o1, o2) -> o1.sort - o2.sort);
                // [1]: 不直接使用 Arrays.asList()，这样返回的 List 不能扩容

                synchronized (mSquareList) {
                    mSquareList.postValue(list);
                    mSquareList.notifyAll();
                }
            }

            @Override
            public void onFailure(Call<SquareBean[]> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    void loadRecommend() {
        loadBatchBean(50);
    }

    void loadSubscribe() {
        if (Account.getInstance() != null) {
            loadBatchBean(49);
        }
    }

    private void loadBatchBean(int category_id) {
        Account account = Account.getInstance();
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<BatchBean> call = request.getBatchUpdate(
                account == null ? null : account.id, category_id);
        call.enqueue(new Callback<BatchBean>() {
            @Override
            public void onResponse(Call<BatchBean> call, Response<BatchBean> response) {
                BatchBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                if (bean.code != 0 || bean.data == null) {
                    mErrMsg.postValue(bean.code + " " + bean.msg);
                    return;
                }

                // 将 BatchBean 包装为 SquareBean
                SquareBean wrapper = wrap(bean);

                // 等待通用列表加载完成
                synchronized (mSquareList) {
                    List<SquareBean> list;
                    while ((list = mSquareList.mCurValue) == null) {
                        ObjectUtils.wait(mSquareList);
                    }
                    list.add(wrapper);
                    Collections.sort(list, (o1, o2) -> o1.sort - o2.sort);
                    mSquareList.postValue(list);
                }
            }

            private SquareBean wrap(BatchBean bean) {
                SquareBean wrapper = new SquareBean();
                wrapper.title = bean.data.title;
                wrapper.sort = bean.data.sort;
                wrapper.category_id = bean.data.category_id;
                wrapper.data = new SquareBean.DataType[bean.data.data.length];
                for (int i = 0; i < wrapper.data.length; i++) {
                    BatchBean.BookType src = bean.data.data[i];
                    SquareBean.DataType dst = wrapper.data[i] = new SquareBean.DataType();

                    dst.title = src.title;
                    dst.status = src.status;
                    dst.cover = src.cover;
                    dst.sub_title = src.authors;
                    dst.type = SquareBean.DataType.TYPE_COMIC;
                }
                return wrapper;
            }

            @Override
            public void onFailure(Call<BatchBean> call, Throwable t) {
                Log.d(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    LiveData<String> getErrMsg() { return mErrMsg; }

    LiveData<List<SquareBean>> getSquareList() { return mSquareList; }

    private static final class LiveDataImpl<T> extends LiveData<T> {
        LiveDataImpl(T value) {
            super(value);
        }

        volatile T mCurValue;

        @Override
        protected void postValue(T value) {
            super.postValue(value);
            mCurValue = value;
        }
    }
}
