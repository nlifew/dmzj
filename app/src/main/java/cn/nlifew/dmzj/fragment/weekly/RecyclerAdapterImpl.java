package cn.nlifew.dmzj.fragment.weekly;

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

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.loadmore.BaseRecyclerAdapter;
import cn.nlifew.dmzj.ui.weekly.WeeklyActivity;
import cn.nlifew.dmzj.utils.TimeUtils;
import cn.nlifew.xdmzj.bean.weekly.SimpleWeeklyBean;
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
                .inflate(R.layout.fragment_weekly_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        SimpleWeeklyBean bean = (SimpleWeeklyBean) mDataSet.get(position);
        Holder holder = (Holder) h;
        holder.onBindViewHolder(bean);
    }

    private final class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Holder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mCoverView = itemView.findViewById(R.id.fragment_weekly_item_cover);
            mTitleView = itemView.findViewById(R.id.fragment_weekly_item_title);
            mTimeView = itemView.findViewById(R.id.fragment_weekly_item_time);
        }

        private final ImageView mCoverView;
        private final TextView mTitleView;
        private final TextView mTimeView;

        void onBindViewHolder(SimpleWeeklyBean bean) {
            itemView.setTag(bean);

            mTitleView.setText(bean.title);
            mTimeView.setText(TimeUtils.formatDate(bean.create_time * 1000));
            Glide.get(mFragment.requireContext())
                    .getRequestManagerRetriever()
                    .get(mFragment)
                    .asBitmap()
                    .load(NetworkUtils.imageUrl(bean.small_cover))
                    .into(mCoverView);
        }

        @Override
        public void onClick(View v) {
            SimpleWeeklyBean bean = (SimpleWeeklyBean) v.getTag();
            String uri = "dmzj://weekly?id=" + bean.id
                    + "&time=" + bean.create_time * 1000;
            Intent intent = new Intent(mFragment.getContext(), WeeklyActivity.class);
            intent.setData(Uri.parse(uri));
            mFragment.startActivity(intent);
        }
    }
}
