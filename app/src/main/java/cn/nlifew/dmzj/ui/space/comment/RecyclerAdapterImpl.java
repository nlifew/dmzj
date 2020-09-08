package cn.nlifew.dmzj.ui.space.comment;

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
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.ui.comic.ComicActivity;
import cn.nlifew.dmzj.utils.TimeUtils;
import cn.nlifew.xdmzj.bean.space.CommentBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

final class RecyclerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl";

    interface Callback {
        String getUserHeadUrl();
        String getUserNickName();
        void onStarComment(CommentBean bean);
        void onReplyComment(CommentBean bean);
    }

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;

        if (! (fragment instanceof Callback)) {
            throw new IllegalStateException("The Fragment must implement Callback");
        }
        mCallback = ((Callback) fragment);
    }

    private final Callback mCallback;
    private final Fragment mFragment;
    private List<CommentBean> mCommentList = new ArrayList<>(64);

    void updateCommentList(CommentBean[] list) {
        mCommentList.clear();
        mCommentList.addAll(Arrays.asList(list));
        notifyDataSetChanged();
    }

    void appendCommentList(CommentBean[] list) {
        int old = mCommentList.size();
        mCommentList.addAll(Arrays.asList(list));
        notifyItemRangeInserted(old, list.length);
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext()).inflate(
                R.layout.fragment_space_comment, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final CommentBean bean = mCommentList.get(position);
        final CommentHolder holder = ((CommentHolder) h);
        holder.onBindViewHolder(bean);
    }


    private final class CommentHolder extends RecyclerView.ViewHolder {

        CommentHolder(@NonNull View itemView) {
            super(itemView);

            mNameView = itemView.findViewById(R.id.fragment_space_comment_name);
            mHeadView = itemView.findViewById(R.id.fragment_space_comment_head);
            mTextView = itemView.findViewById(R.id.fragment_space_comment_text);
            mStarView = itemView.findViewById(R.id.fragment_space_comment_star);
            mReplyView = itemView.findViewById(R.id.fragment_space_comment_reply);
            mTimeView = itemView.findViewById(R.id.fragment_space_comment_time);
            mFromView = itemView.findViewById(R.id.fragment_space_comment_from);

            mTextView.setOnClickListener(this::performReplyComment);
            mStarView.setOnClickListener(this::performStarComment);
            mReplyView.setOnClickListener(this::performReplyComment);
            mFromView.setOnClickListener(this::performCatFromComic);
        }

        private final TextView mNameView;
        private final ImageView mHeadView;
        private final TextView mFromView;
        private final TextView mTextView;

        private final TextView mStarView;
        private final TextView mReplyView;
        private final TextView mTimeView;

        private CommentBean mBean;

        void onBindViewHolder(CommentBean bean) {
            mBean = bean;

            mNameView.setText(mCallback.getUserNickName());

            mTextView.setText(bean.content);
            mFromView.setText(bean.obj_name);
            mStarView.setText(bean.like_amount == 0 ? "赞" : Integer.toString(bean.like_amount));
            mReplyView.setText(bean.reply_amount == 0 ? "评论" : Integer.toString(bean.reply_amount));
            mTimeView.setText(TimeUtils.formatDate(bean.create_time * 1000));

            Glide.get(mFragment.getContext())
                    .getRequestManagerRetriever()
                    .get(mFragment)
                    .asBitmap()
                    .apply(RequestOptions.errorOf(R.drawable.ic_account_circle_grey_600_24dp))
                    .load(NetworkUtils.imageUrl(mCallback.getUserHeadUrl()))
                    .into(mHeadView);
        }

        private void performReplyComment(View view) {
            mCallback.onReplyComment(mBean);
        }

        private void performStarComment(View view) {
            mCallback.onStarComment(mBean);
        }

        private void performCatFromComic(View view) {
            Intent intent = new Intent(mFragment.getContext(), ComicActivity.class);
            intent.setData(Uri.parse("dmzj://comic?id=" + mBean.obj_id));
            mFragment.startActivity(intent);
        }
    }
}
