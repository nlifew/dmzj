package cn.nlifew.dmzj.fragment.news;

import android.os.Bundle;

import androidx.lifecycle.LifecycleOwner;

import cn.nlifew.dmzj.fragment.loadmore.LoadMoreFragment;
import cn.nlifew.xdmzj.bean.news.NewsHeaderBean;

@LoadMoreFragment.Factory(
        adapter = RecyclerAdapterImpl.class,
        viewModel = NewsViewModel.class)
public class NewsFragment extends LoadMoreFragment {

    private static final String PREFIX = NewsFragment.class.getName();
    private static final String ARGS_TYPE = PREFIX + ".ARGS_TYPE";
    private static final String ARGS_UNKNOWN = PREFIX + ".ARGS_UNKNOWN";
    private static final String ARGS_SHOW_HEADER = PREFIX + ".ARGS_SHOW_HEADER";


    public static NewsFragment newInstance(int type, int unknown, boolean showHeader) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_TYPE, type);
        bundle.putInt(ARGS_UNKNOWN, unknown);
        bundle.putInt(ARGS_SHOW_HEADER, showHeader ? 1 : 0);

        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateViewModel(Factory factory, LifecycleOwner owner) {
        super.onCreateViewModel(factory, owner);

        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new NullPointerException("use newInstance() to make a Fragment");
        }
        mShowHeader = bundle.getInt(ARGS_SHOW_HEADER) == 1;

        int type = bundle.getInt(ARGS_TYPE);
        int unknown = bundle.getInt(ARGS_UNKNOWN);

        NewsViewModel vm = (NewsViewModel) mViewModel;
        vm.setUrl(type, unknown);
        vm.newsHeader().observe(owner, this::onNewsHeaderChanged);
    }

    private boolean mShowHeader;

    private void onNewsHeaderChanged(NewsHeaderBean bean) {
        if (bean == null) {
            return;
        }
        ((RecyclerAdapterImpl) mRecyclerAdapter).updateNewsHeader(bean);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        if (mShowHeader) {
            ((NewsViewModel) mViewModel).loadNewsHeader();
        }
    }
}
