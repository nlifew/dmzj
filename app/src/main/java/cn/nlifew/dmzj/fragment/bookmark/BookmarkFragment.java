package cn.nlifew.dmzj.fragment.bookmark;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import cn.nlifew.dmzj.fragment.loadmore.LoadMoreFragment;
import cn.nlifew.dmzj.ui.reading.ReadingActivity;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.widget.recyclerview.PaddingDividerDecoration;
import cn.nlifew.xdmzj.bean.comic.RecordBean;

@LoadMoreFragment.Factory(
        adapter = RecyclerAdapterImpl.class,
        viewModel = BookmarkViewModel.class
)
public class BookmarkFragment extends LoadMoreFragment implements
        RecyclerAdapterImpl.OnItemClickListener {

    private static final String ARGS_COMIC_ID = "ARGS_COMIC_ID";

    public static BookmarkFragment newInstance(int bookId) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_COMIC_ID, bookId);

        BookmarkFragment fragment = new BookmarkFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 添加分割线
        PaddingDividerDecoration decoration = new PaddingDividerDecoration(
                requireContext(), PaddingDividerDecoration.VERTICAL
        );
        int DP15 = DisplayUtils.px2dp(15);
        decoration.setPadding(DP15, 0, DP15, 0);
        mRecyclerView.addItemDecoration(decoration);

        // 禁用上滑加载
        mRecyclerView.setLoadMoreEnabled(false);
    }

    @Override
    protected void onCreateViewModel(@Nullable Factory factory, LifecycleOwner owner) {
        super.onCreateViewModel(factory, owner);

        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new UnsupportedOperationException("call newInstance() to make a Fragment");
        }
        ((BookmarkViewModel) mViewModel).setBookId(bundle.getInt(ARGS_COMIC_ID));
    }

    @Override
    public void onItemClick(RecordBean data) {
        String uri = "dmzj://reading?id=" + ((BookmarkViewModel) mViewModel).getBookId()
                + "&chapter=" + data.chapter_id
                + "&page=" + data.record;
        Intent intent = new Intent(getContext(), ReadingActivity.class);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }
}
