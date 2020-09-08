package cn.nlifew.dmzj.fragment.news;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.ui.news.NewsActivity;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.xdmzj.bean.news.NewsBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

final class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "ItemViewHolder";

    private static View makeView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_news_item, parent, false);
    }

    ItemViewHolder(Fragment fragment, ViewGroup parent) {
        super(makeView(parent));
        mFragment = fragment;
        itemView.setOnClickListener(this);

        mTitleView = itemView.findViewById(R.id.fragment_news_item_title);
        mCoverView = itemView.findViewById(R.id.fragment_news_item_cover);
        mHeadView = itemView.findViewById(R.id.fragment_news_item_head);
        mAuthorView = itemView.findViewById(R.id.fragment_news_item_author);
        mStarView = itemView.findViewById(R.id.fragment_news_item_star);
        mCommentView = itemView.findViewById(R.id.fragment_news_item_comment);
    }

    private final Fragment mFragment;

    private final TextView mTitleView;
    private final ImageView mCoverView;
    private final ImageView mHeadView;
    private final TextView mAuthorView;
    private final TextView mStarView;
    private final TextView mCommentView;

    void onBindViewHolder(NewsBean bean) {
        itemView.setTag(bean);

        mTitleView.setText(bean.title);
        mAuthorView.setText(bean.nickname);
        mStarView.setText(String.valueOf(bean.mood_amount));
        mCommentView.setText(String.valueOf(bean.comment_amount));

        RequestManager rm = Glide
                .get(mFragment.getContext())
                .getRequestManagerRetriever()
                .get(mFragment);

        rm.asBitmap()
                .load(NetworkUtils.imageUrl(bean.cover))
                .into(mHeadView);
        rm.asBitmap()
                .load(NetworkUtils.imageUrl(bean.row_pic_url))
                .into(mCoverView);
    }

    @Override
    public void onClick(View v) {
        NewsBean bean = (NewsBean) v.getTag();
        Intent intent = new Intent(mFragment.getContext(), NewsActivity.class);
        intent.setData(Uri.parse(bean.page_url));
        mFragment.startActivity(intent);
    }
}
