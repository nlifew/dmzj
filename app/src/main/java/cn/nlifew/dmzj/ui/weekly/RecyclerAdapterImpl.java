package cn.nlifew.dmzj.ui.weekly;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;

import java.util.Objects;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.loadmore.BaseRecyclerAdapter;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.xdmzj.bean.weekly.DetailWeeklyBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

final class RecyclerAdapterImpl extends BaseRecyclerAdapter {
    private static final String TAG = "RecyclerAdapterImpl";

    public static final int VIEW_TYPE_DESCRIPTION   = 1;
    public static final int VIEW_TYPE_COMIC         = 2;


    public RecyclerAdapterImpl(Fragment fragment) {
        super(fragment);
    }

    private String mWeeklyDescription;

    @Override
    public void refreshDataSet(Object dataSet) {
        DetailWeeklyBean bean = (DetailWeeklyBean) dataSet;
        mWeeklyDescription = bean.description;

        super.refreshDataSet(bean.comics);
    }


    @Override
    public int getItemCount() {
        int n = mDataSet.size();
        if (mWeeklyDescription != null) {
            n ++;
        }
        return n;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && mWeeklyDescription != null ?
                VIEW_TYPE_DESCRIPTION :
                VIEW_TYPE_COMIC;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        Context context = mFragment.getContext();

        switch (viewType) {
            case VIEW_TYPE_DESCRIPTION:
                view = LayoutInflater.from(context)
                        .inflate(R.layout.activity_weekly_description, parent, false);
                return new DescriptionHolder(view);
            case VIEW_TYPE_COMIC:
                view = LayoutInflater.from(context)
                        .inflate(R.layout.activity_weekly_item, parent, false);
                return new ComicHolder(view);
        }
        throw new IllegalStateException("unknown viewType: " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        if (mWeeklyDescription != null) {
            if (position == 0) {
                DescriptionHolder holder = (DescriptionHolder) h;
                holder.setText(mWeeklyDescription);
                return;
            }
            position --;
        }
        ComicHolder holder = (ComicHolder) h;
        DetailWeeklyBean.ComicType comic = (DetailWeeklyBean.ComicType) mDataSet.get(position);
        holder.setComicType(comic);
    }

    private static final class DescriptionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static ForegroundColorSpan FOREGROUND_COLOR_SPAN;

        DescriptionHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTextView = (TextView) itemView;
            if (FOREGROUND_COLOR_SPAN == null) {
                @ColorInt int color = itemView
                        .getContext()
                        .getResources()
                        .getColor(R.color.colorPrimary, null);
                FOREGROUND_COLOR_SPAN = new ForegroundColorSpan(color);
            }
        }

        private TextView mTextView;
        private boolean mExtended;
        private boolean mExtendEnabled;

        /**
         * 用来设置当前周刊的描述内容。
         * 为了避免文本字数过多像老太太的裹脚布一样又臭又长，
         * 我们需要在合适的位置 “截断” 并在后面展示 “显示更多”。
         * 为了简化问题，我们设置为最多展示 60 个字符
         * @param text text
         */
        void setText(CharSequence text) {
            mExtended = false;
            mExtendEnabled = false;
            CharSequence charSequence = text;

            if (text.length() > 60) {
                mExtendEnabled = true;

                SpannableStringBuilder sb = new SpannableStringBuilder(text, 0, 60);
                sb.append("...");
                sb.append("查看更多", FOREGROUND_COLOR_SPAN, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                charSequence = sb;
            }
            mTextView.setTag(text);
            mTextView.setText(charSequence);
        }

        @Override
        public void onClick(View v) {
            if (!mExtendEnabled) {
                return;
            }

            final CharSequence originText = (String) v.getTag();

            if (mExtended) {
                mExtended = false;
                setText(originText);
            }
            else {
                mExtended = true;
                mTextView.setText(originText);
            }
        }
    }


    private final class ComicHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "ComicHolder";

        ComicHolder(@NonNull View itemView) {
            super(itemView);
            mCoverView = itemView.findViewById(R.id.activity_weekly_item_cover);
            mTitleView = itemView.findViewById(R.id.activity_weekly_item_title);
            mSubtitleView = itemView.findViewById(R.id.activity_weekly_item_subtitle);
            mDescView = itemView.findViewById(R.id.activity_weekly_item_desc);
            mSubscribeView = itemView.findViewById(R.id.activity_weekly_item_subscribe);

            mSubscribeView.setOnClickListener(this);
        }

        private final ImageView mCoverView;
        private final TextView mTitleView;
        private final TextView mSubtitleView;
        private final TextView mDescView;
        private final Button mSubscribeView;


        void setComicType(DetailWeeklyBean.ComicType comic) {
            mTitleView.setText(comic.name);
            mSubtitleView.setText(comic.recommend_brief);
            mDescView.setText(comic.recommend_reason);

            Glide.get(mFragment.requireContext())
                    .getRequestManagerRetriever()
                    .get(mFragment)
                    .asBitmap()
                    .load(NetworkUtils.imageUrl(comic.cover))
                    .into(mCoverView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
