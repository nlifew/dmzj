package cn.nlifew.dmzj.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.adapter.BaseFragmentStatePagerAdapter;
import cn.nlifew.dmzj.fragment.BaseFragment;
import cn.nlifew.dmzj.fragment.FFragment;
import cn.nlifew.dmzj.fragment.category.CategoryFragment;
import cn.nlifew.dmzj.fragment.container.BaseContainerFragment;
import cn.nlifew.dmzj.fragment.lately.LatelyFragment;
import cn.nlifew.dmzj.fragment.ranking.RankingFragment;
import cn.nlifew.dmzj.fragment.weekly.WeeklyFragment;

public class ComicFragment extends BaseContainerFragment {
    private static final String TAG = "ComicFragment";

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();

        PagerAdapterImpl adapter = new PagerAdapterImpl(getChildFragmentManager());
        adapter.setFirstItemPosition(3);

        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(3);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private final class PagerAdapterImpl extends BaseFragmentStatePagerAdapter {

        PagerAdapterImpl(@NonNull FragmentManager fm) {
            super(fm);
        }

        private final Class<?>[] mFragments = new Class[] {
//                SquareFragment.class,
                LatelyFragment.class,
                CategoryFragment.class,
                RankingFragment.class,
                WeeklyFragment.class,
        };
        private final @StringRes int[] mTitles = new int[] {
//                R.string.tab_square,
                R.string.tab_update,
                R.string.tab_category,
                R.string.tab_ranking,
                R.string.tab_special,
        };

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public BaseFragment createFragment(int position) {
            try {

                return (BaseFragment) mFragments[position].newInstance();

            } catch (java.lang.InstantiationException |IllegalAccessException e) {
                Log.e(TAG, "createFragment: ", e);
                throw new UnsupportedOperationException(e);
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return getContext().getString(mTitles[position]);
        }
    }
}
