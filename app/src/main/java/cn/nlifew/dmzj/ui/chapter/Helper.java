package cn.nlifew.dmzj.ui.chapter;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
import java.util.List;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.chapter.ChapterViewModel;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.xdmzj.bean.comic.ComiclBean;

import static cn.nlifew.dmzj.ui.chapter.ChapterActivity.EXTRA_CHAPTERS;

final class Helper implements TabLayout.OnTabSelectedListener,
        ViewPager.OnPageChangeListener {
    private static final String TAG = "Helper";

    Helper(ChapterActivity activity) {
        mActivity = activity;
    }

    private final ChapterActivity mActivity;

    void onCreate() {
        ViewPager pager = mActivity.findViewById(R.id.activity_chapter_pager);
        pager.addOnPageChangeListener(this);
        pager.setAdapter(new FragmentAdapterImpl(mActivity));

        TabLayout tabLayout = mActivity.findViewById(R.id.activity_chapter_tab);
        tabLayout.addOnTabSelectedListener(this);
        tabLayout.setupWithViewPager(pager);

        mChapterList = mActivity.getIntent()
                .getParcelableArrayListExtra(EXTRA_CHAPTERS);
        if (mChapterList == null) {
            throw new NullPointerException("you should put EXTRA_CHAPTERS to Intent");
        }

        TabLayout.Tab tab;
        if (mChapterList.size() > 1 && (tab = tabLayout.getTabAt(0)) != null) {
            configChapterTab(tab);
        }

        mViewModel = new ViewModelProvider(mActivity)
                .get(ChapterViewModel.class);
        mViewModel.updateChapters(mChapterList.get(0).data);
    }

    private int mChapterListIndex = 0;
    private ChapterViewModel mViewModel;
    private List<ComiclBean.ChapterType> mChapterList;
    private @Nullable TextView mChapterTextView;
    private static Field sTabTextViewField;

    private void configChapterTab(TabLayout.Tab tab) {
        try {
            if (sTabTextViewField == null) {
                sTabTextViewField = TabLayout.TabView.class
                        .getDeclaredField("textView");
                sTabTextViewField.setAccessible(true);
            }
            mChapterTextView = (TextView) sTabTextViewField.get(tab.view);
        } catch (NoSuchFieldException|IllegalAccessException e) {
            Log.e(TAG, "configChapterTab: ", e);
        }

        Drawable d = mActivity.getDrawable(R.drawable.ic_keyboard_arrow_down_grey_700_24dp);

        if (mChapterTextView == null || d == null) {
            return;
        }

        d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
        mChapterTextView.setTag(d);
        mChapterTextView.setCompoundDrawables(null, null, d, null);
        mChapterTextView.setCompoundDrawablePadding(DisplayUtils.dp2px(10));
    }

    @Override
    public void onPageSelected(int position) {
        if (mChapterTextView == null) {
            return;
        }
        Drawable d = (Drawable) mChapterTextView.getTag();
        mChapterTextView.setCompoundDrawables(null, null,
                position == 0 ? d : null, null);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        if (mChapterList.size() <= 1) {
            return;
        }

        DialogInterface.OnClickListener cli = (dialog, which) -> {
            dialog.dismiss();
            mChapterListIndex = which;
            ComiclBean.ChapterType list = mChapterList.get(which);
            mViewModel.updateChapters(list.data);
        };

        CharSequence[] choices = new CharSequence[mChapterList.size()];
        for (int i = 0; i < choices.length; i++) {
            choices[i] = mChapterList.get(i).title;
        }
        new AlertDialog.Builder(mActivity)
                .setTitle("选择其它系列")
                .setSingleChoiceItems(choices, mChapterListIndex, cli)
                .show();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
