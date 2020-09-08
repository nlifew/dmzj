package cn.nlifew.dmzj.fragment.container;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.BaseFragment;

public abstract class BaseContainerFragment extends BaseFragment {

//    protected ViewPager2 mViewPager;
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_container, container, false);
        mTabLayout = v.findViewById(R.id.fragment_container_tab);
        mViewPager = v.findViewById(R.id.fragment_container_pager);
        return v;
    }
}
