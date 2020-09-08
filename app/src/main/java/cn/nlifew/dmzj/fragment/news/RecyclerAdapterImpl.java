package cn.nlifew.dmzj.fragment.news;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Locale;

import cn.nlifew.dmzj.fragment.loadmore.BaseRecyclerAdapter;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.widget.recyclerview.RoofItemDecoration;
import cn.nlifew.xdmzj.bean.news.NewsBean;
import cn.nlifew.xdmzj.bean.news.NewsHeaderBean;

final class RecyclerAdapterImpl extends BaseRecyclerAdapter {
    private static final String TAG = "RecyclerAdapterImpl";

    private static final int VIEW_TYPE_HEADER   = 1;
    private static final int VIEW_TYPE_ITEM     = 2;


    public RecyclerAdapterImpl(Fragment fragment) {
        super(fragment);
    }


    void updateNewsHeader(NewsHeaderBean bean) {
        NewsHeaderBean old = mNewsHeader;
        mNewsHeader = bean;
        if (old != null) {
            notifyItemChanged(0);
        }
        else {
            notifyItemInserted(0);
        }
    }

    private NewsHeaderBean mNewsHeader;


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RoofItemDecoration decoration = new RoofItemDecoration(new RoofHelper());
        recyclerView.addItemDecoration(decoration);
    }

    @Override
    public int getItemCount() {
        int n = mDataSet.size();
        return mNewsHeader == null ? n : n + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && mNewsHeader != null ?
                VIEW_TYPE_HEADER :
                VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(mFragment, parent);
            case VIEW_TYPE_ITEM:
                return new ItemViewHolder(mFragment, parent);
        }
        throw new IllegalStateException("unknown viewType: " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mNewsHeader != null) {
            if (position == 0) {
                ((HeaderViewHolder) holder).onBindViewHolder(mNewsHeader);
                return;
            }
            position --;
        }
        ((ItemViewHolder) holder).onBindViewHolder((NewsBean) mDataSet.get(position));
    }

    private final class RoofHelper implements RoofItemDecoration.Callback {

        private final Paint mPaint;
        private final int mTextSize;
        private final Calendar mCalendar;

        RoofHelper() {
            mTextSize = DisplayUtils.sp2px(12);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setTextSize(mTextSize);
            mCalendar = Calendar.getInstance(Locale.CHINA);
        }

        @Override
        public int getItemType(int position) {
            if (mNewsHeader != null) {
                if (position == 0) {
                    return -1;
                }
                position --;
            }
            NewsBean bean = (NewsBean) mDataSet.get(position);

            mCalendar.setTimeInMillis(bean.create_time * 1000);
            return mCalendar.get(Calendar.DAY_OF_YEAR);
        }

        @Override
        public void draw(Canvas c, int l, int t, int r, int b, int type) {
            mCalendar.set(Calendar.DAY_OF_YEAR, type);

            String text = (mCalendar.get(Calendar.MONTH) + 1) + "月"
                    + mCalendar.get(Calendar.DAY_OF_MONTH) + "日 星期"
                    + WEEK[mCalendar.get(Calendar.DAY_OF_WEEK) - 1];

            mPaint.setColor(0xFFF5F7FA);
            c.drawRect(l, t, r, b, mPaint);

            mPaint.setColor(0xFF737373);
            c.drawText(text, DisplayUtils.dp2px(20),
                    (b + t) / 2 + mTextSize / 2, mPaint);
        }
    }

    private static final String[] WEEK = {"日", "一", "二", "三", "四", "五", "六"};
}
