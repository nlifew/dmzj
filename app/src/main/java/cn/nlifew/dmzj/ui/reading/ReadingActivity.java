package cn.nlifew.dmzj.ui.reading;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import cn.nlifew.dmzj.ui.BaseActivity;
import cn.nlifew.dmzj.utils.ToastUtils;


public class ReadingActivity extends BaseActivity implements
        PagerAdapterImpl_v2.PagerCallback {
    private static final String TAG = "ReadingActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int flag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(flag);

        initView();

        Uri uri = getIntent().getData();
        if (uri == null) {
            throw new NullPointerException("give me a data");
        }
        mViewModel = new ViewModelProvider(this)
                .get(ReadingViewModel.class);
        mViewModel.setParam(uri.getQueryParameter("id"),
                uri.getQueryParameter("chapter"),
                uri.getQueryParameter("page"));
        mViewModel.errMsg().observe(this, this::onErrMsgChanged);
        mViewModel.chapter().observe(this, this::onChapterBeanChanged);
        mViewModel.commentList().observe(this, this::onCommentListChanged);
    }

    private void initView() {
        ViewPager2 pager = new ViewPager2(this);
        pager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        mPagerAdapter = new PagerAdapterImpl_v2(this);
        mPagerAdapter.attachToViewPager2(pager);

        setContentView(pager);
    }

    private ReadingViewModel mViewModel;
    private PagerAdapterImpl_v2 mPagerAdapter;


    private void onErrMsgChanged(String msg) {
        if (msg != null) {
            ToastUtils.getInstance(this).show(msg);
            mPagerAdapter.clearFlag();
        }
    }

    private void onChapterBeanChanged(ReadingViewModel.ChapterWrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        mPagerAdapter.updateChapter(wrapper.offset, wrapper.bean, wrapper.page);
    }

    private void onCommentListChanged(ReadingViewModel.CommentWrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        mPagerAdapter.updateComment(wrapper.offset, wrapper.list);
    }

    @Override
    public void loadComicChapter(int offset) {
        mViewModel.loadChapter(offset);
    }

    @Override
    public void scrollPagerBy(int delta) {

    }
}
