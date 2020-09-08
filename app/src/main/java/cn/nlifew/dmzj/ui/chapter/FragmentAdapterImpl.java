package cn.nlifew.dmzj.ui.chapter;

import androidx.annotation.Nullable;

import cn.nlifew.dmzj.adapter.BaseFragmentPagerAdapter;
import cn.nlifew.dmzj.fragment.BaseFragment;
import cn.nlifew.dmzj.fragment.bookmark.BookmarkFragment;
import cn.nlifew.dmzj.fragment.chapter.ChapterFragment;

class FragmentAdapterImpl extends BaseFragmentPagerAdapter {
    private static final String TAG = "FragmentAdapterImpl";

    FragmentAdapterImpl(ChapterActivity activity) {
        super(activity.getSupportFragmentManager());
        mActivity = activity;
    }

    private final ChapterActivity mActivity;

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public BaseFragment createFragment(int position) {
        int id = mActivity.getIntent().getIntExtra(
                ChapterActivity.EXTRA_BOOK_ID, 0);
        switch (position) {
            case 0: return ChapterFragment.newInstance(id);
            case 1: return BookmarkFragment.newInstance(id);
        }
        return null;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "章节列表";
            case 1: return "书签列表";
        }
        return null;
    }
}
