package cn.nlifew.dmzj.fragment.loadmore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.BaseFragment;
import cn.nlifew.dmzj.utils.ToastUtils;
import cn.nlifew.dmzj.widget.recyclerview.LoadMoreRecyclerView;

import static cn.nlifew.dmzj.fragment.loadmore.BaseViewModel.DataWrapper.TYPE_LOAD_MORE;
import static cn.nlifew.dmzj.fragment.loadmore.BaseViewModel.DataWrapper.TYPE_REFRESH;
import static cn.nlifew.dmzj.utils.ReflectUtils.newInstance;

@LoadMoreFragment.Factory
public class LoadMoreFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        LoadMoreRecyclerView.OnLoadMoreListener {
    private final String TAG = getClass().getSimpleName();

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Factory {
        Class<? extends BaseRecyclerAdapter> adapter() default BaseRecyclerAdapter.class;
        Class<? extends BaseViewModel> viewModel() default BaseViewModel.class;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loadmore, container, false);

        mSwipeLayout = view.findViewById(R.id.fragment_loadmore_swipe);
        mSwipeLayout.setOnRefreshListener(this);

        mRecyclerView = view.findViewById(R.id.fragment_loadmore_recycler);
        mRecyclerView.setOnLoadMoreListener(this);

        Factory factory = getClass().getAnnotation(Factory.class);
        onCreateRecyclerAdapter(factory);
        return view;
    }

    protected SwipeRefreshLayout mSwipeLayout;
    protected LoadMoreRecyclerView mRecyclerView;
    protected BaseViewModel mViewModel;
    protected BaseRecyclerAdapter mRecyclerAdapter;


    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();

        if (mViewModel == null) {
            Factory factory = getClass().getAnnotation(Factory.class);
            onCreateViewModel(factory, getViewLifecycleOwner());
        }
        onRefresh();
    }

    protected void onCreateRecyclerAdapter(@Nullable Factory factory) {
        if (factory != null) {
            mRecyclerAdapter = newInstance(factory.adapter(), Fragment.class, this);
        }
        if (mRecyclerAdapter != null) {
            mRecyclerView.setAdapter(mRecyclerAdapter);
        }
    }

    protected void onCreateViewModel(@Nullable Factory factory, LifecycleOwner owner) {
        if (factory != null) {
            mViewModel = new ViewModelProvider(this).get(factory.viewModel());
        }

        if (mViewModel != null) {
            mViewModel.errMsg().observe(owner, this::onErrMsgChanged);
            mViewModel.dataList().observe(owner, this::onDataListChanged);
        }
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: start");
        if (mViewModel != null) {
            setLoading(true);
            mViewModel.refreshDataList();
        }
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore: start");
        if (mViewModel != null) {
            setLoading(true);
            mViewModel.loadMoreDataList();
        }
    }

    public void setLoading(boolean loading) {
        mSwipeLayout.setRefreshing(loading);
        mRecyclerView.setLoadMoreWorking(loading);
    }

    protected void onErrMsgChanged(String s) {
        if (s == null) {
            return;
        }
        ToastUtils.getInstance(getContext()).show(s);
        setLoading(false);
    }

    protected void onDataListChanged(BaseViewModel.DataWrapper wrapper) {
        if (wrapper == null) {
            return;
        }

        switch (wrapper.type) {
            case TYPE_REFRESH:
                mRecyclerAdapter.refreshDataSet(wrapper.data);
                break;
            case TYPE_LOAD_MORE:
                mRecyclerAdapter.appendDataSet(wrapper.data);
                break;
        }

        setLoading(false);
    }
}
