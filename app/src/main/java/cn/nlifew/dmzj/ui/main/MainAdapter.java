package cn.nlifew.dmzj.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.adapter.BaseFragmentPagerAdapter;
import cn.nlifew.dmzj.fragment.BaseFragment;
import cn.nlifew.dmzj.fragment.FFragment;

class MainAdapter extends BaseFragmentPagerAdapter implements
        TabLayout.OnTabSelectedListener {
    private static final String TAG = "MainAdapter";

    private static final int FIRST_ITEM_POSITION = 1;

    MainAdapter(FragmentActivity activity) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
    }

    private final Context mContext;

    void bind(TabLayout tabLayout, ViewPager pager) {
        pager.setAdapter(this);
        pager.setCurrentItem(FIRST_ITEM_POSITION);
        setFirstItemPosition(FIRST_ITEM_POSITION);

        tabLayout.setupWithViewPager(pager);
        tabLayout.addOnTabSelectedListener(this);

        // setUpWithViewPager 会实时更新，因此这里的 getTabCount() 没问题
        for (int i = 0, n = tabLayout.getTabCount(); i < n; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            onConfigureTab(tab, i);
            if (i == FIRST_ITEM_POSITION) {
                onTabSelected(tab);
            }
            else {
                onTabUnselected(tab);
            }
        }
    }

    @Override
    public BaseFragment createFragment(int position) {
        Log.d(TAG, "createBaseFragment: " + position);

        BaseFragment fragment;

        switch (position) {
//            case 0: fragment = new NewsFragment(); break;
            case 1: fragment = new ComicFragment(); break;

            default: fragment = new FFragment();
        }
//        fragment.setLazyLoadEnabled(false);
        return fragment;
    }


    private void onConfigureTab(TabLayout.Tab tab, int position) {
        Log.d(TAG, "onConfigureTab: " + position);

        TextView tv = new TextView(mContext);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        tv.setText(getPageTitle(position));

        tab.setCustomView(tv);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Log.d(TAG, "getPageTitle: " + position);
        switch (position) {
            case 0: return mContext.getString(R.string.tab_news);
            case 1: return mContext.getString(R.string.tab_comic);
            case 2: return mContext.getString(R.string.tab_novel);
        }
        return "";
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d(TAG, "onTabSelected: " + tab.getPosition());
        TextView tv = (TextView) tab.getCustomView();
        if (tv != null) {
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Log.d(TAG, "onTabUnselected: " + tab.getPosition());
        TextView tv = (TextView) tab.getCustomView();
        if (tv != null) {
            tv.setTextColor(0xFF006557);    // 0xFF5771cc
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
