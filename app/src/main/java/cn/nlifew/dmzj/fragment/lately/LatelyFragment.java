package cn.nlifew.dmzj.fragment.lately;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.nlifew.dmzj.fragment.loadmore.LoadMoreFragment;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.widget.recyclerview.PaddingDividerDecoration;

@LoadMoreFragment.Factory(
        adapter = RecyclerAdapterImpl.class,
        viewModel = LatelyViewModel.class)
public class LatelyFragment extends LoadMoreFragment {
    private static final String TAG = "LatelyFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PaddingDividerDecoration decoration = new PaddingDividerDecoration(
                getContext(), PaddingDividerDecoration.VERTICAL);
        int DP10 = DisplayUtils.dp2px(10);
        decoration.setPadding(DP10, 0, DP10, 0);
        mRecyclerView.addItemDecoration(decoration);
    }
}
