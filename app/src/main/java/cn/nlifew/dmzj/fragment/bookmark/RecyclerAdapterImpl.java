package cn.nlifew.dmzj.fragment.bookmark;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.loadmore.BaseRecyclerAdapter;
import cn.nlifew.dmzj.utils.TimeUtils;
import cn.nlifew.xdmzj.bean.comic.RecordBean;

class RecyclerAdapterImpl extends BaseRecyclerAdapter {
    private static final String TAG = "RecyclerAdapterImpl";

    interface OnItemClickListener {
        void onItemClick(RecordBean data);
    }

    public RecyclerAdapterImpl(Fragment fragment) {
        super(fragment);
        if (! (fragment instanceof OnItemClickListener)) {
            throw new UnsupportedOperationException("The Fragment must implements OnItemClickListener");
        }
        mItemClickCallback = (OnItemClickListener) fragment;
    }


    private final OnItemClickListener mItemClickCallback;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext())
                .inflate(R.layout.fragment_chapter_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final Holder holder = ((Holder) h);
        final RecordBean r = (RecordBean) mDataSet.get(position);

        holder.mTitleView.setText(r.chapter_name);
        holder.mTimeView.setText(TimeUtils.formatDate(r.viewing_time * 1000));
        holder.itemView.setTag(r);
    }

    private final class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Holder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.title);
            mTimeView = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(this);
        }

        private TextView mTitleView;
        private TextView mTimeView;

        @Override
        public void onClick(View v) {
            RecordBean r = (RecordBean) v.getTag();
            mItemClickCallback.onItemClick(r);
        }
    }
}
