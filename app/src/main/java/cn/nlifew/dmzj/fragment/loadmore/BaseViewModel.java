package cn.nlifew.dmzj.fragment.loadmore;

import androidx.annotation.IntDef;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class BaseViewModel extends ViewModel {
    private static final String TAG = "BaseViewModel";


    public static final class DataWrapper<T> {
        public static final int TYPE_REFRESH    = 1;
        public static final int TYPE_LOAD_MORE  = 2;

        @IntDef({TYPE_REFRESH, TYPE_LOAD_MORE})
        @Retention(RetentionPolicy.SOURCE)
        @SuppressWarnings("WeakerAccess")
        public @interface Type {  }

        public T data;
        public @Type int type;

        public DataWrapper(@Type int type, T data) {
            this.type = type;
            this.data = data;
        }
    }

    protected final MutableLiveData<DataWrapper> mDataWrapper
            = new MutableLiveData<>(null);
    protected final MutableLiveData<String> mErrMsg
            = new MutableLiveData<>(null);

    protected int mPageIndex, mTotalCount, mCurCount;

    public abstract void refreshDataList();

    public abstract void loadMoreDataList();

    public LiveData<DataWrapper> dataList() { return mDataWrapper; }
    public LiveData<String> errMsg() { return mErrMsg; }
}
