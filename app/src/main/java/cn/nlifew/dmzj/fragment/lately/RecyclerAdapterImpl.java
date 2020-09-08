package cn.nlifew.dmzj.fragment.lately;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.loadmore.BaseRecyclerAdapter;
import cn.nlifew.dmzj.ui.comic.ComicActivity;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.utils.TimeUtils;
import cn.nlifew.xdmzj.bean.lately.LatelyBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

class RecyclerAdapterImpl extends BaseRecyclerAdapter {
    private static final String TAG = "RecyclerAdapterImpl";

    public RecyclerAdapterImpl(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext())
                .inflate(R.layout.fragment_home_horizontal_item, parent, false);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                view.getLayoutParams();

        lp.leftMargin = lp.rightMargin = DisplayUtils.dp2px(20);
        lp.topMargin = lp.bottomMargin = DisplayUtils.dp2px(8);
        view.setLayoutParams(lp);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final Holder holder = (Holder) h;
        final LatelyBean bean = (LatelyBean) mDataSet.get(position);

        StringBuilder sb = new StringBuilder(32);
        sb.append(bean.types).append(" - ").append(bean.authors);

        holder.itemView.setTag(bean);
        holder.mTitleView.setText(bean.title);
        holder.mAboutView.setText(sb.toString());

        sb.setLength(0);
        TimeUtils.formatDate(bean.last_updatetime * 1000, sb);
        sb.append(" - ").append(bean.last_update_chapter_name);
        holder.mDescView.setText(sb.toString());

        Glide.with(mFragment)
                .asBitmap()
                .load(NetworkUtils.imageUrl(bean.cover))
                .into(holder.mCoverView);
    }

    private final class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Holder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleView = itemView.findViewById(R.id.fragment_home_vertical_item_title);
            mAboutView = itemView.findViewById(R.id.fragment_home_vertical_item_about);
            mDescView = itemView.findViewById(R.id.fragment_home_vertical_item_description);
            mCoverView = itemView.findViewById(R.id.fragment_home_vertical_item_cover);
        }

        final TextView mTitleView;
        final TextView mAboutView;
        final TextView mDescView;
        final ImageView mCoverView;

        @Override
        public void onClick(View v) {
            final LatelyBean bean = (LatelyBean) v.getTag();
            String uri = "dmzj://detail?id=" + bean.id;
            Intent intent = new Intent(mFragment.getContext(), ComicActivity.class);
            intent.setData(Uri.parse(uri));
            mFragment.startActivity(intent);
        }
    }
}
