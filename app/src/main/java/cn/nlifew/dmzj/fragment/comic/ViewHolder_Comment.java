package cn.nlifew.dmzj.fragment.comic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.ref.WeakReference;
import java.util.Map;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.ui.space.SpaceActivity;
import cn.nlifew.dmzj.utils.TimeUtils;
import cn.nlifew.xdmzj.bean.comic.CommentBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

final class ViewHolder_Comment extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "ViewHolder_Comment";

    interface Callback {
        void reply(ViewHolder_Comment holder, CommentBean.CommentType target);
        void star(ViewHolder_Comment holder, CommentBean.CommentType target);
        void list(ViewHolder_Comment holder, String[] list, Map<String, CommentBean.CommentType> map);
    }

    ViewHolder_Comment(Fragment fragment, ViewGroup parent, Callback callback) {
        super(LayoutInflater.from(fragment.getContext()).inflate(
                R.layout.fragment_comic_comment, parent, false
        ));
        mFragment = fragment;
        mCallback = callback;

        mHeadView = itemView.findViewById(R.id.fragment_detail_comment_head);
        mNameView = itemView.findViewById(R.id.fragment_detail_comment_name);
        mTextView = itemView.findViewById(R.id.fragment_detail_comment_text);
        mOriginView = itemView.findViewById(R.id.fragment_detail_comment_origin);
        mTimeView = itemView.findViewById(R.id.fragment_detail_comment_time);
        mStarView = itemView.findViewById(R.id.fragment_detail_comment_star);

        mHeadView.setOnClickListener(this);
        mStarView.setOnClickListener(this);
        mOriginView.setOnClickListener(this);
        mTextView.setOnClickListener(this);
    }

    private final Fragment mFragment;
    private final Callback mCallback;

    private ImageView mHeadView;
    private TextView mNameView;
    private TextView mTextView;
    private TextView mOriginView;
    private TextView mTimeView;
    private TextView mStarView;

    private String[] mCommentIds;
    private Map<String, CommentBean.CommentType> mCommentMap;

    private static WeakReference<Drawable> sRedStarIcon;
    private static WeakReference<Drawable> sGrayStarIcon;

    private static RequestOptions sGlideOptions = new RequestOptions()
            .error(R.drawable.ic_account_circle_grey_600_24dp);

    void onBindViewHolder(String s, Map<String, CommentBean.CommentType> map) {
        String[] ss = s.split(",");
        CommentBean.CommentType bean = map.get(ss[0]);
        if (bean == null) {
            itemView.setVisibility(View.GONE);
            return;
        }
        itemView.setVisibility(View.VISIBLE);

        mCommentIds = ss;
        mCommentMap = map;

        mNameView.setText(bean.nickname);
        mTextView.setText(bean.content);
        mTimeView.setText(TimeUtils.formatDate(bean.create_time * 1000));

        mStarView.setText(bean.like_amount == 0 ? "赞" : Integer.toString(bean.like_amount));
        mStarView.setCompoundDrawables(bean.is_goods == 1 ?
                redStarIcon() : grayStarIcon(), null, null, null);

        Glide.get(mFragment.getContext())
                .getRequestManagerRetriever()
                .get(mFragment)
                .asBitmap()
                .apply(sGlideOptions)
                .load(NetworkUtils.imageUrl(bean.avatar_url))
                .into(mHeadView);

        if (ss.length > 1 && (bean = map.get(ss[1])) != null) {
            mOriginView.setText(bean.nickname + ": " + bean.content);
            mOriginView.setVisibility(View.VISIBLE);
        }
        else {
            mOriginView.setVisibility(View.GONE);
        }
    }

    private Drawable redStarIcon() {
        Drawable d;
        if (sRedStarIcon == null || (d = sRedStarIcon.get()) == null) {
            d = mFragment.getContext().getDrawable(R.drawable.ic_thumb_up_grey_18dp);
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            sRedStarIcon = new WeakReference<>(d);
        }
        return d;
    }

    private Drawable grayStarIcon() {
        Drawable d;
        if (sGrayStarIcon == null || (d = sGrayStarIcon.get()) == null) {
            d = mFragment.getContext().getDrawable(R.drawable.ic_thumb_up_outline_grey600_18dp);
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            sGrayStarIcon = new WeakReference<>(d);
        }
        return d;
    }

    @Override
    public void onClick(View v) {
        CommentBean.CommentType bean = mCommentMap.get(mCommentIds[0]);
        if (bean == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.fragment_detail_comment_head:
                onHeadViewClick(bean);
                break;
            case R.id.fragment_detail_comment_text:
                onTextViewClick(bean);
                break;
            case R.id.fragment_detail_comment_origin:
                onOriginViewClick(bean);
                break;
            case R.id.fragment_detail_comment_star:
                onStarViewClick(bean);
                break;
        }
    }

    private void onHeadViewClick(CommentBean.CommentType bean) {
        String uri = "dmzj://user?id=" + bean.sender_uid;
        Intent intent = new Intent(mFragment.getContext(), SpaceActivity.class);
        intent.setData(Uri.parse(uri));
        mFragment.startActivity(intent);
    }

    private void onStarViewClick(CommentBean.CommentType bean) {
        mCallback.star(this, bean);
    }

    private void onOriginViewClick(CommentBean.CommentType bean) {
        mCallback.list(this, mCommentIds, mCommentMap);
    }

    private void onTextViewClick(CommentBean.CommentType bean) {
        mCallback.reply(this, bean);
    }
}
