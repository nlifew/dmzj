package cn.nlifew.dmzj.ui.space;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.adapter.BaseFragmentPagerAdapter;
import cn.nlifew.dmzj.fragment.BaseFragment;
import cn.nlifew.dmzj.ui.BaseActivity;
import cn.nlifew.dmzj.ui.space.comic.ComicSpaceFragment;
import cn.nlifew.dmzj.ui.space.comment.CommentSpaceFragment;
import cn.nlifew.dmzj.utils.ToastUtils;
import cn.nlifew.xdmzj.bean.space.SpaceBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

import static cn.nlifew.dmzj.ui.space.SpaceViewModel.ID_TYPE_AUTHOR;
import static cn.nlifew.dmzj.ui.space.SpaceViewModel.ID_TYPE_USER;

/**
 * 这个 Activity 用来展示和用户有关系的内容
 *
 * 调用者应该通过 Intent 传递一个 Uri，这个 Uri 允许有以下两种形式：
 * [1]. dmzj://user?id=12345
 * [2]. dmzj://author?id=54321
 * 二者的区别在于：[2] 表示这只是个作者号，[1] 表示这个是用户账号
 * 实际的请求 url 是不同的
 * 例如：
 * Context context = getContext();
 * Intent intent = new Intent(context, SpaceActivity.class);
 * intent.setData(Uri.parseUri("dmzj://user?id=12345"));
 * context.startActivity(intent);
 */

public class SpaceActivity extends BaseActivity {
    private static final String TAG = "SpaceActivity";

    private static final class UriConfig {
        UriConfig(Uri uri) {
            Objects.requireNonNull(uri);
            uid = uri.getQueryParameter("id");

            String host = Objects.requireNonNull(uri.getHost());

            switch (host) {
                case "user": idType = ID_TYPE_USER; break;
                case "author": idType = ID_TYPE_AUTHOR; break;
                default: throw new IllegalStateException("unknown host: " + host);
            }
        }

        final String uid;
        final @SpaceViewModel.IDType int idType;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space);

        mUriConfig = new UriConfig(getIntent().getData());

        initView();
        initViewModel();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.activity_space_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mToolbar = findViewById(R.id.activity_space_collapsed);
        mHeadImageView = mToolbar.findViewById(R.id.activity_space_head);
        mSignView = mToolbar.findViewById(R.id.activity_space_sign);

        ImageView iv = mToolbar.findViewById(R.id.activity_space_bg);
        Glide.get(this)
                .getRequestManagerRetriever()
                .get(this)
                .asBitmap()
                .load(R.drawable.bg_space)
                .into(iv);

        TabLayout tab = findViewById(R.id.activity_space_tab);
        ViewPager pager = findViewById(R.id.activity_space_pager);
        tab.setupWithViewPager(pager);
        pager.setAdapter(new FragmentAdapterImpl());
    }

    private final class FragmentAdapterImpl extends BaseFragmentPagerAdapter {
        private static final String TAG = "FragmentAdapterImpl";

        FragmentAdapterImpl() {
            super(getSupportFragmentManager());

            switch (mUriConfig.idType) {
                case ID_TYPE_USER: {
                    mFragments = new Class[] {
                            CommentSpaceFragment.class,
                            ComicSpaceFragment.class,
                    };
                    mTitles = new CharSequence[] {
                            "评论", "作品"
                    };
                    break;
                }
                case ID_TYPE_AUTHOR: {
                    mFragments = new Class[] {
                            ComicSpaceFragment.class,
                            CommentSpaceFragment.class,
                    };
                    mTitles = new CharSequence[] {
                            "作品", "评论"
                    };
                    break;
                }
                default: {
                    throw new IllegalStateException("unknown type: " + mUriConfig.idType);
                }
            }
        }

        private final Class<?>[] mFragments;
        private final CharSequence[] mTitles;

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public BaseFragment createFragment(int position) {
            try {
                return (BaseFragment) mFragments[position].newInstance();
            } catch (IllegalAccessException|InstantiationException e) {
                Log.e(TAG, "createFragment: " + position, e);
                throw new RuntimeException(e);
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }


    private void initViewModel() {
        SpaceViewModel viewModel = new ViewModelProvider(this)
                .get(SpaceViewModel.class);
        viewModel.userInfo()
                .observe(this, this::onUserInfoChanged);
        viewModel.errMsg()
                .observe(this, this::onErrMsgChanged);
        viewModel.loadUserInfo();
    }

    private CollapsingToolbarLayout mToolbar;
    private ImageView mHeadImageView;
    private TextView mSignView;
    private UriConfig mUriConfig;


    private void onErrMsgChanged(String s) {
        if (s == null) {
            return;
        }
        ToastUtils.getInstance(this).show(s);
    }

    private void onUserInfoChanged(SpaceBean bean) {
        if (bean == null) {
            return;
        }
        mToolbar.setTitle(bean.nickname);
        mSignView.setText(TextUtils.isEmpty(bean.description) ?
                getString(R.string.default_sign) :
                bean.description);

        Glide.get(this)
                .getRequestManagerRetriever()
                .get(this)
                .asBitmap()
                .apply(RequestOptions.errorOf(R.drawable.ic_account_circle_white_48dp))
                .load(NetworkUtils.imageUrl(bean.cover))
                .into(mHeadImageView);
    }


    private ViewModelProvider.Factory mViewModelFactory;

    @NonNull
    @Override
    public ViewModelProvider.Factory getDefaultViewModelProviderFactory() {
        if (mViewModelFactory == null) {
            mViewModelFactory = new SpaceViewModelFactory();
        }
        return mViewModelFactory;
    }

    private final class SpaceViewModelFactory implements ViewModelProvider.Factory {

        SpaceViewModelFactory() {
            mOriginFactory = SpaceActivity.super.getDefaultViewModelProviderFactory();
        }
        private final ViewModelProvider.Factory mOriginFactory;

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            Constructor<T> constructor = findConstructor(modelClass);
            if (constructor != null) {
                try {
                    return constructor.newInstance(mUriConfig.uid, mUriConfig.idType);
                } catch (IllegalAccessException|InstantiationException|InvocationTargetException e) {
                    Log.e(TAG, "create: " + modelClass, e);
                }
            }
            return mOriginFactory.create(modelClass);
        }

        private <T> Constructor<T> findConstructor(Class<T> cls) {
            if (! SpaceViewModel.class.isAssignableFrom(cls)) {
                return null;
            }
            try {
                return cls.getConstructor(String.class, int.class);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "findConstructor: " + cls, e);
            }
            return null;
        }
    }
}
