package cn.nlifew.dmzj.ui.comic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.comic.DetailFragment;
import cn.nlifew.dmzj.ui.BaseActivity;

/**
 * 这个类用来打开对应漫画的详情页
 * 调用者必须提供一个 uri 格式的 data，内含对应漫画的 id
 * 例如：
 * Context context = getContext();
 * Intent intent = new Intent(context, ComicActivity.class);
 * intent.setData(Uri.parseUri("dmzj://detail?id=50475"));
 * context.startActivity(intent);
 *
 * ComicActivity 保留了显示封面、子标题、悬浮按钮的作用，
 * 其余的内容显示则全部交给了 {@link DetailFragment}。
 * 这部分内容需要监听 DetailViewModel 的变化。
 *
 * 另外需要注意的是，在布局文件 R.layout.activity_detail 中，
 * 可以看到 Fragment 所依附的 View 是一个 FrameLayout 而不是 NestedScrollView，
 * 这是因为 NovelFragment 中如果存在 RecyclerView，会和 NestedScrollView
 * 产生滑动冲突，即前者的复用机制失效，参考 https://www.jianshu.com/p/801f5255b28a
 * 因此原则上要求，DetailFragment 的根布局是一个 RecyclerView，其余的 View 作为
 * RecyclerView 的不同 Type 显示出来。
 *
 */

public class ComicActivity extends BaseActivity {
    private static final String TAG = "ComicActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 1. 初始化 View

        Toolbar toolbar = findViewById(R.id.activity_detail_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        AppBarLayout appBar = findViewById(R.id.activity_detail_appbar);
        appBar.addOnOffsetChangedListener(new Helper.EllipsisizeTitle(this));

        // 2. 检查 Uri 并添加 Fragment

        Uri uri = getIntent().getData();
        if (uri == null) {
            throw new UnsupportedOperationException("give me a data");
        }

        String id = uri.getQueryParameter("id");
        DetailFragment fragment = DetailFragment.newInstance(id);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_detail_host, fragment)
                .commit();

        // 3. 观察 SubscribeViewModel
        mHelper.onCreate(id);
    }

    private final Helper mHelper = new Helper(this);

    public void onSubscribeFabClick(View view) {
        mHelper.onSubscribeFabClick(view);
    }

    public void onReadingFabClick(View view) {
        mHelper.onReadingFabClick(view);
    }

    public void onCommentViewClick(View view) {
        mHelper.onCommentViewClick(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHelper.onActivityResult(requestCode, resultCode, data);
    }
}
