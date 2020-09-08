package cn.nlifew.dmzj.ui.cmtlist;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.ui.BaseActivity;
import cn.nlifew.dmzj.utils.DisplayUtils;

public class CmtListActivity extends BaseActivity {
    private static final String TAG = "CmtListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmtlist);

        Toolbar toolbar = findViewById(R.id.activity_cmtlist_toolbar);
        setSupportActionBar(toolbar);

        RecyclerView v = findViewById(R.id.activity_cmtlist_recycler);
        v.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView tv = new TextView(CmtListActivity.this);
                tv.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        DisplayUtils.dp2px(100)
                ));
                tv.setGravity(Gravity.CENTER);

                return new RecyclerView.ViewHolder(tv) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView).setText(String.valueOf(position));
            }

            @Override
            public int getItemCount() {
                return 50;
            }
        });
    }
}
