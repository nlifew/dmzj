package cn.nlifew.dmzj.ui.comic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.comic.DetailViewModel;
import cn.nlifew.dmzj.ui.comment.CommentActivity;
import cn.nlifew.dmzj.ui.reading.ReadingActivity;
import cn.nlifew.dmzj.utils.ToastUtils;
import cn.nlifew.xdmzj.bean.comic.ComiclBean;
import cn.nlifew.xdmzj.entity.Account;
import cn.nlifew.xdmzj.utils.NetworkUtils;

import static android.app.Activity.RESULT_OK;

final class Helper {
    private static final String TAG = "Helper";

    static final class EllipsisizeTitle implements AppBarLayout.OnOffsetChangedListener {

        EllipsisizeTitle(Activity activity) {
            mActivity = activity;
        }

        private final Activity mActivity;

        private int mLastVerticalOffset;
        private CollapsingToolbarLayout mToolbar;

        @Override
        public void onOffsetChanged(AppBarLayout appBar, int verticalOffset) {
            if (mToolbar == null) {
                mToolbar = mActivity.findViewById(R.id.activity_detail_collapse);
            }

            int threshold = (int) (appBar.getHeight() * 0.6f);

            if (-verticalOffset > threshold && -mLastVerticalOffset <= threshold) {
                CharSequence title = mToolbar.getTitle();
                if (title != null && title.length() > 8) {
                    mToolbar.setTag(R.id.view_tag_1, title);
                    mToolbar.setTitle(title.subSequence(0, 8) + "...");
                }
            }
            else if (-verticalOffset < threshold && -mLastVerticalOffset >= threshold) {
                CharSequence title = (CharSequence) mToolbar.getTag(R.id.view_tag_1);
                if (title != null) {
                    mToolbar.setTitle(title);
                }
            }
            mLastVerticalOffset = verticalOffset;
        }
    }


    Helper(ComicActivity activity) {
        mActivity = activity;
    }

    private final ComicActivity mActivity;

    void onCreate(String id) {
        ViewModelProvider provider = new ViewModelProvider(mActivity);

        mComicViewModel = provider.get(DetailViewModel.class);
        mComicViewModel.getDetailBean().observe(mActivity, this::onDetailHeaderChanged);

        mSubscribeViewModel = provider.get(SubscribeViewModel.class);
        mSubscribeViewModel.setId(id);
        mSubscribeViewModel.getSubscribeStatus().observe(mActivity, this::onSubscribeChanged);
        mSubscribeViewModel.getErrMsg().observe(mActivity, this::onErrMsgChanged);

        mSubscribeFab = mActivity.findViewById(R.id.activity_detail_subscribe_progress);
    }

    private void onDetailHeaderChanged(ComiclBean bean) {
        if (bean == null) {
            return;
        }
        CollapsingToolbarLayout toolbar = mActivity.findViewById(R.id.activity_detail_collapse);
        toolbar.setTitle(bean.title);

        ImageView iv = mActivity.findViewById(R.id.activity_detail_cover);
        iv.setVisibility(View.VISIBLE);
        Glide.get(mActivity)
                .getRequestManagerRetriever()
                .get(mActivity)
                .asBitmap()
                .load(NetworkUtils.imageUrl(bean.cover))
                .into(iv);

        TextView subtitle = mActivity.findViewById(R.id.activity_detail_subtitle);
        if (bean.types.length == 1) {
            subtitle.setText(bean.types[0].tag_name);
        }
        else {
            StringBuilder sb = new StringBuilder();
            for (ComiclBean.TypeType type : bean.types) {
                sb.append(type.tag_name).append(" - ");
            }
            sb.setLength(sb.length() - 3);
            subtitle.setText(sb);
        }
    }


    private DetailViewModel mComicViewModel;
    private SubscribeViewModel mSubscribeViewModel;

    private boolean mShowingAnimation;
    private CircleProgressFab mSubscribeFab;

    private void onErrMsgChanged(String msg) {
        if (msg != null) {
            ToastUtils.getInstance(mActivity).show(msg);
        }
    }

    private void onSubscribeChanged(Boolean b) {
        if (b == null) {
            mSubscribeViewModel.loadSubscribeStatus();
            return;
        }
        if (! mShowingAnimation) {  // 如果此时没有动画，我们直接更新 UI
            mSubscribeFab.setTitle(b ? "取消订阅" : "添加订阅");
            mSubscribeFab.setDrawableIcon(b ?
                    R.drawable.ic_playlist_add_check_white_24dp :
                    R.drawable.ic_playlist_add_white_24dp);
        }
        else {
            mSubscribeFab.beginFinalAnimation();
            mSubscribeFab.attachListener(() -> {
                String msg = b ? "成功订阅" : "成功取消订阅";
                Snackbar.make(mSubscribeFab, msg, BaseTransientBottomBar.LENGTH_SHORT)
                        .show();
                mShowingAnimation = false;
                mSubscribeFab.attachListener(null);

                mSubscribeFab.setTitle(b ? "取消订阅" : "添加订阅");
                mSubscribeFab.setDrawableIcon(b ?
                        R.drawable.ic_playlist_add_check_white_24dp :
                        R.drawable.ic_playlist_add_white_24dp);
            });
        }
    }

    void onSubscribeFabClick(View view) {
        if (mShowingAnimation) {
            return;
        }
        Boolean b = mSubscribeViewModel.getSubscribeStatus().getValue();
        if (b == null) {
            ToastUtils.getInstance(mActivity).show("获取订阅信息失败，您是否还未登录？");
            return;
        }
        mShowingAnimation = true;
        mSubscribeFab.show();
        if (b) {
            mSubscribeViewModel.unsubscribe();
        }
        else {
            mSubscribeViewModel.subscribe();
        }
    }

    void onReadingFabClick(View view) {
        ComiclBean comic = mComicViewModel.getDetailBean().getValue();
        if (comic == null) {
            ToastUtils.getInstance(mActivity).show("没有详细信息");
            return;
        }
        String uri = "dmzj://reading?id=" + comic.id;
        Intent intent = new Intent(mActivity, ReadingActivity.class);
        intent.setData(Uri.parse(uri));
        mActivity.startActivity(intent);
    }

    void onCommentViewClick(View view) {
        if (Account.getInstance() == null) {
            ToastUtils.getInstance(mActivity).show("您还未登录");
            return;
        }

        Intent intent = new CommentActivity.Builder()
                .setMaxImages(1)
                .build(mActivity);
        mActivity.startActivityForResult(intent, 21);
    }

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != 21 || resultCode != RESULT_OK) {
            return;
        }
        CommentActivity.Result result = new CommentActivity.Result(data);
        // todo
    }
}
