package cn.nlifew.dmzj.fragment.square;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.nlifew.dmzj.fragment.loadmore.BaseLoadMoreFragment;
import cn.nlifew.dmzj.utils.ToastUtils;
import cn.nlifew.xdmzj.bean.SquareBean;

public class SquareFragment extends BaseLoadMoreFragment {
    private static final String TAG = "SquareFragment";


    private SquareViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(), DividerItemDecoration.VERTICAL
        ));

        return view;
    }

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        mRecyclerView.setLoadMoreEnabled(false);    // 禁用上滑加载
        return mRecyclerAdapter = new RecyclerAdapterImpl(this);
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel = new ViewModelProvider(this).get(SquareViewModel.class);
        mViewModel.getErrMsg().observe(owner, this::onErrMsgChanged);
        mViewModel.getSquareList().observe(owner, this::onSquareListChanged);
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        mViewModel.loadSquareList();
        mViewModel.loadRecommend();
        mViewModel.loadSubscribe();
    }

    @Override
    public void onLoadMore() {
        throw new UnsupportedOperationException("onLoadMore");
    }

    private void onSquareListChanged(List<SquareBean> squareList) {
        if (squareList == null) {
            onRefresh();
            return;
        }
        mRecyclerAdapter.updateSquareList(squareList);
        setLoading(false);
    }

    private void onErrMsgChanged(String msg) {
        if (msg != null) {
            ToastUtils.getInstance(getContext()).show(msg);
            setLoading(false);
        }
    }
}
