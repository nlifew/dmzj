package cn.nlifew.dmzj.ui.space;

import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.nlifew.xdmzj.bean.space.SpaceBean;
import cn.nlifew.xdmzj.request.ISpace;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpaceViewModel extends ViewModel {
    private static final String TAG = "SpaceViewModel";

    public static final int ID_TYPE_USER = 1;
    public static final int ID_TYPE_AUTHOR = 2;


    @IntDef({ID_TYPE_AUTHOR, ID_TYPE_USER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IDType {}

    public SpaceViewModel(String uid, @IDType int idType) {
        mUid = Integer.parseInt(uid);
        mIdType = idType;
    }

    protected final int mUid;
    protected final @IDType int mIdType;

    protected final MutableLiveData<String> mErrMsg
            = new MutableLiveData<>(null);
    protected final MutableLiveData<SpaceBean> mSpaceBean
            = new MutableLiveData<>(null);


    void loadUserInfo() {
        ISpace iSpace = NetworkUtils.create(ISpace.class);
        Call<SpaceBean> call;

        switch (mIdType) {
            case ID_TYPE_USER:
                call = iSpace.getUserInfo(mUid);
                break;
            case ID_TYPE_AUTHOR:
                call = iSpace.getAuthorInfo(mUid);
                break;
            default:
                throw new IllegalStateException("invalid IDType: " + mIdType);
        }
        call.enqueue(new Callback<SpaceBean>() {
            @Override
            public void onResponse(Call<SpaceBean> call, Response<SpaceBean> response) {
                SpaceBean bean = response.body();
                mSpaceBean.postValue(bean);
            }

            @Override
            public void onFailure(Call<SpaceBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    public LiveData<String> errMsg() { return mErrMsg; }
    public LiveData<SpaceBean> userInfo() { return mSpaceBean; }
}
