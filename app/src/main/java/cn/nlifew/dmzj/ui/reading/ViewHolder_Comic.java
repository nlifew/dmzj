package cn.nlifew.dmzj.ui.reading;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import cn.nlifew.xdmzj.utils.NetworkUtils;

class ViewHolder_Comic extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolder_Comic";

    private static View makeView(Activity activity, ViewGroup group) {
        ReadingView view = new ReadingView(activity);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return view;
    }

    ViewHolder_Comic(Activity activity, ViewGroup parent) {
        super(makeView(activity, parent));
        mActivity = activity;
    }

    private Activity mActivity;

    void onBindViewHolder(String url) {
        Glide.get(mActivity)
                .getRequestManagerRetriever()
                .get(mActivity)
                .asBitmap()
                .load(NetworkUtils.imageUrl(url))
                .into((ReadingView) itemView);
    }
}
