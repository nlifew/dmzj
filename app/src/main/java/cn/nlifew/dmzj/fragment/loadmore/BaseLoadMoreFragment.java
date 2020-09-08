package cn.nlifew.dmzj.fragment.loadmore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.BaseFragment;
import cn.nlifew.dmzj.widget.recyclerview.LoadMoreRecyclerView;

public abstract class BaseLoadMoreFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        LoadMoreRecyclerView.OnLoadMoreListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loadmore, container, false);

        mSwipeLayout = view.findViewById(R.id.fragment_loadmore_swipe);
        mSwipeLayout.setOnRefreshListener(this);

        mRecyclerView = view.findViewById(R.id.fragment_loadmore_recycler);
        mRecyclerView.setOnLoadMoreListener(this);

        RecyclerView.Adapter adapter = newRecyclerAdapter();
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    protected SwipeRefreshLayout mSwipeLayout;
    protected LoadMoreRecyclerView mRecyclerView;

    protected abstract RecyclerView.Adapter newRecyclerAdapter();

    @Override
//    @CallSuper
    public void onRefresh() {
//        setLoading(true);
    }

    @Override
//    @CallSuper
    public void onLoadMore() {
//        setLoading(true);
    }

    public void setLoading(boolean loading) {
        mSwipeLayout.setRefreshing(loading);
        mRecyclerView.setLoadMoreWorking(loading);
    }

    public boolean isLoading() { return mRecyclerView.isLoadMoreWorking(); }
}
