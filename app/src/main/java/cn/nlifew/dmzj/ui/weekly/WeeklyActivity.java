package cn.nlifew.dmzj.ui.weekly;

import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.SimpleDateFormat;
import java.util.Objects;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.loadmore.BaseViewModel;
import cn.nlifew.dmzj.ui.BaseActivity;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.utils.TimeUtils;
import cn.nlifew.dmzj.widget.BlurTransformation;
import cn.nlifew.xdmzj.bean.weekly.DetailWeeklyBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

/**
 * 这个类负责显示周刊内容。你必须通过 {@link android.content.Intent#setData(Uri)}
 * 函数传递一个 Uri 格式的数据，其中必须包含：id 和 time。
 * 例如："dmzj://weekly?id=12345&time=1597759061"
 */

public class WeeklyActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "WeeklyActivity";

    private static final boolean BLUR_HEADER_BACKGROUND = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);

        setTitle("周刊详情");

        initViews();
        setupParams();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.activity_weekly_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mCommentView = findViewById(R.id.activity_weekly_comment);
        mTitleView = findViewById(R.id.activity_weekly_title);
        mSubtitleView = findViewById(R.id.activity_weekly_subtitle);
        mCollapsingToolbar = findViewById(R.id.activity_weekly_collapsing);

        AppBarLayout appBar = findViewById(R.id.activity_weekly_appbar);
        appBar.addOnOffsetChangedListener(new AppbarOffsetChangeListener());
    }

    private void setupParams() {
        Uri uri = Objects.requireNonNull(getIntent().getData());

        String id = Objects.requireNonNull(uri.getQueryParameter("id"));
        String time = Objects.requireNonNull(uri.getQueryParameter("time"));
        mWeeklyTime = Long.parseLong(time);

        WeeklyViewModel viewModel = new ViewModelProvider(this)
                .get(WeeklyViewModel.class);
        viewModel.setId(id);
        viewModel.dataList().observe(this, this::onWeeklyInfoChanged);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_weekly_host, new WeeklyFragment())
                .commit();
    }

    private long mWeeklyTime;
    private TextView mTitleView, mSubtitleView;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private TextView mCommentView;

    private void onWeeklyInfoChanged(BaseViewModel.DataWrapper wrapper) {
        if (wrapper == null) {
            return;
        }

        DetailWeeklyBean bean = (DetailWeeklyBean) wrapper.data;

        mTitleView.setText(bean.title);
        mCommentView.setText(bean.comment_amount == 0 ?
                "评论" : Integer.toString(bean.comment_amount));

        StringBuilder sb = new StringBuilder();
        sb.append(bean.comics.length).append("个作品 - ");
        mSubtitleView.setText(Helper.formatDate(mWeeklyTime, sb));

        if (BLUR_HEADER_BACKGROUND) {
            RequestOptions options = new RequestOptions()
                    .error(R.drawable.bg_detail_header)
                    .transform(new BlurTransformation(this))    // [1]
                    .dontAnimate();
            // [1]: 不要用 GlideTransformation 自带的实现
            // 这个库没有对 Glide 的高版本做适配，接口不兼容

            Glide.get(this)
                    .getRequestManagerRetriever()
                    .get(this)
                    .asBitmap()
                    .load(NetworkUtils.imageUrl(bean.mobile_header_pic))
                    .apply(options)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            ColorMatrixColorFilter matrix = new ColorMatrixColorFilter(new float[] {
                                    1, 0, 0, 0, -0x40,   // R
                                    0, 1, 0, 0, -0x40,   // G
                                    0, 0, 1, 0, -0x40,   // B
                                    0, 0, 0, 1, 0       // A
                            });
                            BitmapDrawable d = new BitmapDrawable(getResources(), resource);
                            d.setColorFilter(matrix);
                            mCollapsingToolbar.setBackground(d);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            Log.d(TAG, "onLoadCleared: " + placeholder);
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {

    }

    private final class AppbarOffsetChangeListener implements AppBarLayout.OnOffsetChangedListener {

        AppbarOffsetChangeListener() {
            mActionBar = getSupportActionBar();
        }

        private int mScrollRange;
        private ActionBar mActionBar;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (mScrollRange <= 0) {
                mScrollRange = appBarLayout.getTotalScrollRange();
            }
            if (mScrollRange <= 0 || mActionBar == null) {
                return;
            }

            boolean isTitleEnabled = mCollapsingToolbar.isTitleEnabled();
            boolean showTitle = mScrollRange + verticalOffset == 0;

            if (isTitleEnabled != showTitle) {
                mActionBar.setDisplayShowTitleEnabled(showTitle);
                mCollapsingToolbar.setTitleEnabled(showTitle);
            }
        }
    }
}

