package cn.nlifew.dmzj.ui.space.comic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.dmzj.fragment.loadmore.BaseLoadMoreFragment;
import cn.nlifew.dmzj.ui.space.SpaceViewModel;
import cn.nlifew.dmzj.widget.recyclerview.PaddingDividerDecoration;
import cn.nlifew.xdmzj.bean.space.SpaceBean;

public class ComicSpaceFragment extends BaseLoadMoreFragment {
    private static final String TAG = "ComicSpaceFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        PaddingDividerDecoration decoration = new PaddingDividerDecoration(getContext(),
                PaddingDividerDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLoadMoreEnabled(false);    // 禁用上滑加载
        return view;
    }

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        return mRecyclerAdapter = new RecyclerAdapterImpl(this);
    }

    private SpaceViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;


    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel = new ViewModelProvider(getActivity()).get(SpaceViewModel.class);
        mViewModel.userInfo().observe(owner, this::onComicListChanged);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        setLoading(false);
    }

    @Override
    public void onLoadMore() {
        throw new IllegalStateException("impossible here !");
    }

    private void onComicListChanged(SpaceBean bean) {
        if (bean == null) {
            return;
        }
        mRecyclerAdapter.updateComicList(bean.data);
        setLoading(false);
    }
}

