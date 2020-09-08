package cn.nlifew.dmzj.ui.space.comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.dmzj.fragment.loadmore.BaseLoadMoreFragment;
import cn.nlifew.dmzj.ui.space.SpaceViewModel;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.utils.ToastUtils;
import cn.nlifew.dmzj.widget.recyclerview.PaddingDividerDecoration;
import cn.nlifew.xdmzj.bean.space.CommentBean;
import cn.nlifew.xdmzj.bean.space.SpaceBean;

import static cn.nlifew.dmzj.ui.space.comment.CommentViewModel.CommentWrapper.TYPE_LOAD_MORE;
import static cn.nlifew.dmzj.ui.space.comment.CommentViewModel.CommentWrapper.TYPE_REFRESH;

public class CommentSpaceFragment extends BaseLoadMoreFragment implements
        RecyclerAdapterImpl.Callback {
    private static final String TAG = "CommentSpaceFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        PaddingDividerDecoration decoration = new PaddingDividerDecoration(
                getContext(), PaddingDividerDecoration.VERTICAL);
        int DP15 = DisplayUtils.px2dp(15);
        decoration.setPadding(DP15, 0, DP15, 0);
        mRecyclerView.addItemDecoration(decoration);
        return view;
    }

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        return mRecyclerAdapter = new RecyclerAdapterImpl(this);
    }

    private RecyclerAdapterImpl mRecyclerAdapter;
    private CommentViewModel mViewModel;
    private SpaceBean mSpaceBean;

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        LifecycleOwner owner = getViewLifecycleOwner();
        ViewModelProvider provider = new ViewModelProvider(getActivity());

        mViewModel = provider.get(CommentViewModel.class);
        mViewModel.errMsg().observe(owner, this::onErrMsgChanged);
        mViewModel.commentList().observe(owner, this::onCommentListChanged);

        provider.get(SpaceViewModel.class)
                .userInfo()
                .observe(owner, this::onUserBeanChanged);
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        mViewModel.refreshCommentList();
    }

    @Override
    public void onLoadMore() {
        setLoading(true);
        mViewModel.loadMoreCommentList();
    }

    private void onUserBeanChanged(SpaceBean bean) {
        if ((mSpaceBean = bean) != null) {
            onRefresh();
        }
    }

    private void onErrMsgChanged(String msg) {
        if (msg == null) {
            return;
        }
        ToastUtils.getInstance(getContext()).show(msg);
        setLoading(false);
    }

    private void onCommentListChanged(CommentViewModel.CommentWrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        switch (wrapper.type) {
            case TYPE_REFRESH:
                mRecyclerAdapter.updateCommentList(wrapper.list);
                break;
            case TYPE_LOAD_MORE:
                mRecyclerAdapter.appendCommentList(wrapper.list);
                break;
        }
        setLoading(false);
    }

    @Override
    public String getUserHeadUrl() {
        return mSpaceBean == null ? "" : mSpaceBean.cover;
    }

    @Override
    public String getUserNickName() {
        return mSpaceBean == null ? "" : mSpaceBean.nickname;
    }

    @Override
    public void onStarComment(CommentBean bean) {

    }

    @Override
    public void onReplyComment(CommentBean bean) {

    }
}
