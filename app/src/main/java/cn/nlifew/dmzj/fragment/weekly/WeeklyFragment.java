package cn.nlifew.dmzj.fragment.weekly;


import cn.nlifew.dmzj.fragment.loadmore.LoadMoreFragment;

@LoadMoreFragment.Factory(
        adapter = RecyclerAdapterImpl.class,
        viewModel = WeeklyViewModel.class
)
public class WeeklyFragment extends LoadMoreFragment {
    private static final String TAG = "WeeklyFragment";
}
