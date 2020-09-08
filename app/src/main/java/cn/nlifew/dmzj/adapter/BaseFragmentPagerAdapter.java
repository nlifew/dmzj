package cn.nlifew.dmzj.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import cn.nlifew.dmzj.fragment.BaseFragment;

public abstract class BaseFragmentPagerAdapter extends FragmentPagerAdapter implements
        BaseFragment.OnLazyLoadListener {

    public BaseFragmentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    private int mFirstItemPosition;
    private BaseFragment mFirstItem;
    private boolean mLazyLoaded = false;

    public void setFirstItemPosition(int position) {
        if (mLazyLoaded || mFirstItem != null) {
            throw new UnsupportedOperationException("Fragments has been called after onLazyLoad()");
        }
        mFirstItemPosition = position;
    }

    public abstract BaseFragment createFragment(int position);

    @NonNull
    @Override
    public final Fragment getItem(int position) {
        BaseFragment fragment = createFragment(position);
        if (position == mFirstItemPosition) {
            mFirstItem = fragment;
        }
        fragment.setOnLazyLoadListener(this);
        return fragment;
    }

    @Override
    public boolean onLazyLoad(BaseFragment fragment) {
        if (mLazyLoaded) {
            return true;
        }
        if (mFirstItem == fragment) {
            mFirstItem = null;
            mLazyLoaded = true;
            return true;
        }
        return false;
    }
}
