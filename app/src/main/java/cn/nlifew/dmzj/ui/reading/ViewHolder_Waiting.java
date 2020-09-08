package cn.nlifew.dmzj.ui.reading;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

class ViewHolder_Waiting extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolder_Waiting";

    private static View makeView(Activity activity, ViewGroup parent) {
        TextView tv = new TextView(activity);
        tv.setText("加载中 ...");
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return tv;
    }

    ViewHolder_Waiting(Activity activity, ViewGroup parent) {
        super(makeView(activity, parent));
    }
}
