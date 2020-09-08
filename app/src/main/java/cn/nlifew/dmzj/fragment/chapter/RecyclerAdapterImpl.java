package cn.nlifew.dmzj.fragment.chapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.nlifew.dmzj.R;
import cn.nlifew.xdmzj.bean.comic.ComiclBean;

final class RecyclerAdapterImpl extends RecyclerView.Adapter  {
    private static final String TAG = "RecyclerAdapterImpl";

    interface OnItemClickListener {
        void onItemClick(ComiclBean.ChapterDataType data);
    }

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
        if (! (fragment instanceof OnItemClickListener)) {
            throw new UnsupportedOperationException("The Fragment must implements OnItemClickListener");
        }
        mItemClickCallback = ((OnItemClickListener) fragment);
    }

    private final Fragment mFragment;
    private final OnItemClickListener mItemClickCallback;
    private final DateHelper mDateHelper = new DateHelper();
    private ComiclBean.ChapterDataType[] mDataSet;


    void refreshDataSet(ComiclBean.ChapterDataType[] data) {
        mDataSet = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.length;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext())
                .inflate(R.layout.fragment_chapter_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {

        final Holder holder = (Holder) h;
        final ComiclBean.ChapterDataType chapter = mDataSet[position];

        holder.mTitleView.setText(chapter.chapter_title);
        holder.mTimeView.setText(mDateHelper.format(chapter.updatetime * 1000));

        holder.itemView.setTag(chapter);
    }

    private final class Holder extends RecyclerView.ViewHolder implements
            View.OnClickListener{
        Holder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.title);
            mTimeView = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(this);
        }

        final TextView mTitleView;
        final TextView mTimeView;

        @Override
        public void onClick(View v) {
            ComiclBean.ChapterDataType chapter = (ComiclBean.ChapterDataType)
                    itemView.getTag();
            mItemClickCallback.onItemClick(chapter);
        }
    }

    private static final class DateHelper {
        private final Date mDate = new Date();
        private FieldPosition mFieldPosition = new FieldPosition(0);
        private final StringBuffer mTextBuilder = new StringBuffer(32);
        private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

        CharSequence format(long time) {
            mDate.setTime(time);
            mTextBuilder.setLength(0);
            mDateFormat.format(mDate, mTextBuilder, mFieldPosition);
            return mTextBuilder;
        }
    }
}
