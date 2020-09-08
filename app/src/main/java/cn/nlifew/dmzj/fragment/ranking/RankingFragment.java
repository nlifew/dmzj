package cn.nlifew.dmzj.fragment.ranking;

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
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.utils.ToastUtils;
import cn.nlifew.dmzj.widget.recyclerview.PaddingDividerDecoration;
import cn.nlifew.xdmzj.bean.ranking.RankingBean;

import static cn.nlifew.dmzj.fragment.ranking.RankingViewModel.ComicWrapper.TYPE_LOAD_MORE;
import static cn.nlifew.dmzj.fragment.ranking.RankingViewModel.ComicWrapper.TYPE_REFRESH;

public class RankingFragment extends BaseLoadMoreFragment implements
        RecyclerAdapterImpl.OnCategorySelectListener {
    private static final String TAG = "RankingFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        PaddingDividerDecoration decoration = new PaddingDividerDecoration(
                getContext(), PaddingDividerDecoration.VERTICAL);
        int DP10 = DisplayUtils.dp2px(10);
        decoration.setPadding(DP10, 0, DP10, 0);
        mRecyclerView.addItemDecoration(decoration);

        return view;
    }

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        return mRecyclerAdapter = new RecyclerAdapterImpl(this);
    }


    private RankingViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;


    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel = new ViewModelProvider(this)
                .get(RankingViewModel.class);
        mViewModel.errMsg().observe(owner, this::onErrMsgChanged);
        mViewModel.category().observe(owner, this::onCategoryChanged);
        mViewModel.comicList().observe(owner, this::onComicListChanged);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        mViewModel.loadCategory();
    }

    @Override
    public void onLoadMore() {
        setLoading(true);
        mViewModel.loadMoreComic();
    }

    private void onErrMsgChanged(String msg) {
        if (msg != null) {
            ToastUtils.getInstance(getContext()).show(msg);
            setLoading(false);
        }
    }

    private void onCategoryChanged(RankingBean[] bean) {
        if (bean == null) {
            return;
        }
        mRecyclerAdapter.updateCategory(bean);
        mViewModel.refreshComicList();
    }

    private void onComicListChanged(RankingViewModel.ComicWrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        switch (wrapper.type) {
            case TYPE_REFRESH:
                mRecyclerAdapter.updateComicList(wrapper.list);
                break;
            case TYPE_LOAD_MORE:
                mRecyclerAdapter.appendComicList(wrapper.list);
                break;
        }
        setLoading(false);
    }


    @Override
    public void onCategorySelect(RankingBean bean, int index) {
        setLoading(true);
        mViewModel.filter(bean, index);
        mViewModel.refreshComicList();
    }

    @Override
    public void setLoading(boolean loading) {
        super.setLoading(loading);
        mRecyclerView.setEnabled(loading);
    }
}
