package cn.nlifew.dmzj.fragment.comic;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.nlifew.dmzj.app.ThisApp;
import cn.nlifew.dmzj.helper.CommentHelper;
import cn.nlifew.dmzj.utils.ToastUtils;
import cn.nlifew.xdmzj.bean.comic.CommentBean;
import cn.nlifew.xdmzj.bean.comic.ComiclBean;
import cn.nlifew.xdmzj.bean.comic.NoticeBean;
import cn.nlifew.xdmzj.entity.Account;

final class RecyclerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl";

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_NOTICE = 2;
    private static final int TYPE_COMMENT = 3;

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
        if (! (fragment instanceof ViewHolder_Comment.Callback)) {
            throw new IllegalStateException("Fragment must implement ViewHolder_Comment.Callback");
        }
        mCallback = ((ViewHolder_Comment.Callback) fragment);
    }

    private final Fragment mFragment;
    private final ViewHolder_Comment.Callback mCallback;

    private ComiclBean mHeaderBean;
    private NoticeBean mNoticeBean;
    private Map<String, CommentBean.CommentType> mCommentDepends = new HashMap<>(64);
    private List<String> mCommentList = new ArrayList<>(32);

    void updateHeader(ComiclBean bean) {
        mHeaderBean = bean;
        notifyItemChanged(0);
    }

    void updateNotice(NoticeBean bean) {
        mNoticeBean = bean;
        notifyItemChanged(1);
    }

    void appendComment(CommentBean bean) {
        int old = getItemCount();
        mCommentList.addAll(Arrays.asList(bean.commentIds));
        mCommentDepends.putAll(bean.comments);
        notifyItemRangeChanged(old, bean.commentIds.length);
    }

    void refreshComment(CommentBean bean) {
        mCommentList.clear();
        mCommentDepends.clear();
        appendComment(bean);
    }

    @Override
    public int getItemCount() {
        return mCommentList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0: return TYPE_HEADER;
            case 1: return TYPE_NOTICE;
        }
        return TYPE_COMMENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER: return new ViewHolder_Header(mFragment, parent);
            case TYPE_NOTICE: return new ViewHolder_Notice(mFragment, parent);
            case TYPE_COMMENT: return new ViewHolder_Comment(mFragment, parent, mCallback);
        }
        throw new UnsupportedOperationException("unknown viewType: " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((ViewHolder_Header) holder).onBindViewHolder(mHeaderBean);
                break;
            case TYPE_NOTICE:
                ((ViewHolder_Notice) holder).onBindViewHolder(mNoticeBean);
                break;
            case TYPE_COMMENT:
                ((ViewHolder_Comment) holder).onBindViewHolder(
                        mCommentList.get(position - 2), mCommentDepends);
                break;
        }
    }
}
