package cn.nlifew.dmzj.ui.reading;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.xdmzj.bean.reading.ChapterCommentBean;

class ViewHolder_Comment extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolder_Comment";

    private static View makeView(Activity activity, ViewGroup parent) {
        TextView tv = new TextView(activity);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    ViewHolder_Comment(Activity activity, ViewGroup parent) {
        super(makeView(activity, parent));
    }

    void onBindViewHolder(ChapterCommentBean[] list) {
        ((TextView) itemView).setText("这里是评论区");
    }
}
