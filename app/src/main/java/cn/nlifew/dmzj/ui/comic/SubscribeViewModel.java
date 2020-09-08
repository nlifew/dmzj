package cn.nlifew.dmzj.ui.comic;

import android.util.ArrayMap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.Map;

import cn.nlifew.xdmzj.bean.SimpleBean;
import cn.nlifew.xdmzj.entity.Account;
import cn.nlifew.xdmzj.request.IComic;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import cn.nlifew.xdmzj.xDmzj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscribeViewModel extends androidx.lifecycle.ViewModel {
    private static final String TAG = "SubscribeViewModel";

    public SubscribeViewModel() {  }

    private final MutableLiveData<String> mErrMsg = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> mSubscribe = new MutableLiveData<>(null);
//    private final MutableLiveData<>

    LiveData<Boolean> getSubscribeStatus() {
        return mSubscribe;
    }

    LiveData<String> getErrMsg() {
        return mErrMsg;
    }

    SubscribeViewModel setId(String id) {
        mId = id;
        return this;
    }

    private String mId;

    void loadSubscribeStatus() {
        Account account = Account.getInstance();
        if (account == null) {
            return;
        }

        IComic detail = NetworkUtils.create(IComic.class);
        Call<SimpleBean> call = detail.getSubscribeState(
                account.id, mId);
        call.enqueue(new Callback<SimpleBean>() {
            @Override
            public void onResponse(Call<SimpleBean> call, Response<SimpleBean> response) {
                SimpleBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                if (bean.code == 0) {
                    mSubscribe.postValue(Boolean.TRUE);
                }
                else if (bean.code == 1) {
                    mSubscribe.postValue(Boolean.FALSE);
                }
                else {
                    mErrMsg.postValue("unknown code: " + bean.code + " " + bean.msg);
                }
            }

            @Override
            public void onFailure(Call<SimpleBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    void subscribe() {
        IComic detail = NetworkUtils.create(IComic.class);
        Call<SimpleBean> call = detail.subscribe(newSubscribeMap(mId));
        call.enqueue(new Callback<SimpleBean>() {
            @Override
            public void onResponse(Call<SimpleBean> call, Response<SimpleBean> response) {
                SimpleBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                if (bean.code != 0) {
                    mErrMsg.postValue(bean.code + " " + bean.msg);
                    return;
                }
                mSubscribe.postValue(Boolean.TRUE);
            }

            @Override
            public void onFailure(Call<SimpleBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    private static Map<String, String> newSubscribeMap(String id) {
        Account account = Account.getInstance();
        Map<String, String> map = new ArrayMap<>(6);
        map.put("type", "mh");
        map.put("uid", Integer.toString(account.id));
        map.put("dmzj_token", account.token);
        map.put("obj_ids", id);
        return map;
    }

    private static Map<String, String> newUnsubscribeMap(String id) {
        Account account = Account.getInstance();
        Map<String, String> map = new ArrayMap<>(8);
        map.put("timestamp", Long.toString(System.currentTimeMillis()));
        map.put("uid", Integer.toString(account.id));
        map.put("channel", xDmzj.CHANNEL);
        map.put("type", "mh");
        map.put("dmzj_token", account.token);
        map.put("version", xDmzj.VERSION);
        map.put("obj_ids", id);
        return map;
    }

    void unsubscribe() {
        IComic detail = NetworkUtils.create(IComic.class);
        Call<SimpleBean> call = detail.unsubscribe(newUnsubscribeMap(mId));
        call.enqueue(new Callback<SimpleBean>() {
            @Override
            public void onResponse(Call<SimpleBean> call, Response<SimpleBean> response) {
                SimpleBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                if (bean.code != 0) {
                    mErrMsg.postValue(bean.code + " " + bean.msg);
                    return;
                }
                mSubscribe.postValue(Boolean.FALSE);
            }

            @Override
            public void onFailure(Call<SimpleBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
