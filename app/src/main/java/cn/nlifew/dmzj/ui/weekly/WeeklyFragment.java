package cn.nlifew.dmzj.ui.weekly;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import java.util.Objects;

import cn.nlifew.dmzj.fragment.loadmore.BaseViewModel;
import cn.nlifew.dmzj.fragment.loadmore.LoadMoreFragment;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.widget.recyclerview.PaddingDividerDecoration;
import cn.nlifew.xdmzj.bean.weekly.DetailWeeklyBean;

@LoadMoreFragment.Factory(
        adapter = RecyclerAdapterImpl.class,
        viewModel = WeeklyViewModel.class
)
public class WeeklyFragment extends LoadMoreFragment {
    private static final String TAG = "WeeklyFragment";


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置分隔线
        PaddingDividerDecoration decoration = new PaddingDividerDecoration(
                requireContext(), PaddingDividerDecoration.VERTICAL
        );
        int DP15 = DisplayUtils.dp2px(15);
        decoration.setPadding(DP15, 0, DP15, 0);
        mRecyclerView.addItemDecoration(decoration);

        // 禁用上滑加载
        mRecyclerView.setLoadMoreEnabled(false);
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        // 使用 Activity 来创建 ViewModel，
        // 这样就能在 Activity 里初始化参数
        return requireActivity().getViewModelStore();
    }

    @NonNull
    @Override
    public ViewModelProvider.Factory getDefaultViewModelProviderFactory() {
        return requireActivity().getDefaultViewModelProviderFactory();
    }
}
