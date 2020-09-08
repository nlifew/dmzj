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

import com.google.android.material.tabs.TabLayout;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.adapter.BaseFragmentStatePagerAdapter;
import cn.nlifew.dmzj.fragment.BaseFragment;
import cn.nlifew.dmzj.fragment.FFragment;
import cn.nlifew.dmzj.fragment.container.BaseContainerFragment;

public class NewsFragment extends BaseContainerFragment {
    private static final String TAG = "NewsFragment";

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();

        PagerAdapterImpl adapter = new PagerAdapterImpl(getChildFragmentManager());
//        adapter.setFirstItemPosition(1);

        mViewPager.setAdapter(adapter);
//        mViewPager.setCurrentItem(1);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

    }

    private static final class FragmentConfig {
        int type, unknown;
        boolean showHeader;
        @StringRes int title;

        FragmentConfig(@StringRes int title, int type, int unknown, boolean showHeader) {
            this.title = title;
            this.type = type;
            this.unknown = unknown;
            this.showHeader = showHeader;
        }
    }

    private final class PagerAdapterImpl extends BaseFragmentStatePagerAdapter {

        PagerAdapterImpl(@NonNull FragmentManager fm) {
            super(fm);
        }

        private final FragmentConfig[] mFragments = new FragmentConfig[] {
                new FragmentConfig(R.string.tab_news_recommend, 0, 2, true),
                new FragmentConfig(R.string.tab_news_animator, 1, 3, false),
                new FragmentConfig(R.string.tab_news_comic, 2, 3, false),
                new FragmentConfig(R.string.tab_news_novel, 3, 3, false),
                new FragmentConfig(R.string.tab_news_picture, 8, 3, false),
                new FragmentConfig(R.string.tab_news_game, 7, 3, false),
                new FragmentConfig(R.string.tab_news_periphery, 4, 3, false),
                new FragmentConfig(R.string.tab_news_voice_actor, 5, 3, false),
                new FragmentConfig(R.string.tab_news_exhibition, 9, 3, false),
                new FragmentConfig(R.string.tab_news_music, 6, 3, false),
                new FragmentConfig(R.string.tab_news_others, 10, 3, false),
        };

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public BaseFragment createFragment(int position) {
            FragmentConfig config = mFragments[position];
            return cn.nlifew.dmzj.fragment.news.NewsFragment
                    .newInstance(config.type, config.unknown, config.showHeader);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getContext().getString(mFragments[position].title);
        }
    }
}
