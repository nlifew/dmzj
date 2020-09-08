package cn.nlifew.dmzj.fragment.chapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.loadmore.BaseLoadMoreFragment;
import cn.nlifew.dmzj.ui.reading.ReadingActivity;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.utils.ToastUtils;
import cn.nlifew.dmzj.widget.recyclerview.PaddingDividerDecoration;
import cn.nlifew.xdmzj.bean.comic.ComiclBean;

public class ChapterFragment extends BaseLoadMoreFragment implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener,
        RecyclerAdapterImpl.OnItemClickListener {
    private static final String TAG = "ChapterFragment";

    private static final String ARGS_COMIC_ID = "ARGS_COMIC_ID";

    public static ChapterFragment newInstance(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_COMIC_ID, id);
        ChapterFragment fragment = new ChapterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new UnsupportedOperationException("use newInstance() to make a instance");
        }
        mComicId = bundle.getInt(ARGS_COMIC_ID);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        PaddingDividerDecoration decoration = new PaddingDividerDecoration(
                getContext(), PaddingDividerDecoration.VERTICAL
        );
        int DP15 = DisplayUtils.px2dp(15);
        decoration.setPadding(DP15, 0, DP15, 0);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLoadMoreEnabled(false);    // 禁用上滑加载
        return view;
    }

    private int mComicId;
    private ChapterViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        return mRecyclerAdapter = new RecyclerAdapterImpl(this);
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel = new ViewModelProvider(getActivity()).get(ChapterViewModel.class);
        mViewModel.getErrMsg().observe(owner, this::onErrMsgChanged);
        mViewModel.getChapterList().observe(owner, this::onChapterListChanged);
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        mViewModel.refreshChapters();
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
            setLoading(false);
        }
    }

    private void onChapterListChanged(ComiclBean.ChapterDataType[] data) {
        if (data == null) {
            onRefresh();
            return;
        }
        mRecyclerAdapter.refreshDataSet(data);
        setLoading(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_chapter_options, menu);
        SearchView searchView = (SearchView) menu
                .findItem(R.id.fragment_chapter_search)
                .getActionView();
        searchView.setOnCloseListener(this);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fragment_chapter_sort) {
            if (! isLoading()) {
                setLoading(true);
                mViewModel.reverseChapters();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onClose() {
        if (! isLoading()) {
            setLoading(true);
            mViewModel.refreshChapters();
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (! isLoading()) {
            setLoading(true);
            mViewModel.filterChapters(query);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onItemClick(ComiclBean.ChapterDataType data) {
        String uri = "dmzj://reading?id=" + mComicId
                + "&chapter=" + data.chapter_id;

        Intent intent = new Intent(getContext(), ReadingActivity.class);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }
}
