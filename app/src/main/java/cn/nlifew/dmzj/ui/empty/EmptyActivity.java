package cn.nlifew.dmzj.ui.empty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsService;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.ui.BaseActivity;
import cn.nlifew.dmzj.ui.browser.BrowserActivity;
import cn.nlifew.dmzj.ui.main.MainActivity;
import cn.nlifew.dmzj.ui.weekly.WeeklyActivity;

public class EmptyActivity extends BaseActivity {
    private static final String TAG = "EmptyActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        findViewById(R.id.activity_empty_btn_1)
                .setOnClickListener(this::onButton1Click);
    }

    private void onButton1Click(View view) {
        Intent intent = new Intent(this, WeeklyActivity.class);
        intent.setData(Uri.parse("dmzj://weekly?id=432&time=1597122483000"));

        startActivity(intent);
    }
}
