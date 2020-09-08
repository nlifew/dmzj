package cn.nlifew.dmzj.ui.chapter;

import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.ui.BaseActivity;

public class ChapterActivity extends BaseActivity {
    private static final String TAG = "ChapterActivity";

    private static final String PREFIX = ChapterActivity.class.getName();
    public static final String EXTRA_CHAPTERS  = PREFIX + ".EXTRA_CHAPTERS";
    public static final String EXTRA_BOOK_ID   = PREFIX + ".EXTRA_BOOK_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        mHelper.onCreate();
    }

    private final Helper mHelper = new Helper(this);

    private void initView() {
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
        setContentView(R.layout.activity_chapter);

        Toolbar toolbar = findViewById(R.id.activity_chapter_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("章节");
    }
}
