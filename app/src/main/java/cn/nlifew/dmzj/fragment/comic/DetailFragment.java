package cn.nlifew.dmzj.fragment.comic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Map;

import cn.nlifew.dmzj.fragment.BaseFragment;
import cn.nlifew.dmzj.helper.CommentHelper;
import cn.nlifew.dmzj.ui.comment.CommentActivity;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.utils.ToastUtils;
import cn.nlifew.dmzj.widget.recyclerview.LoadMoreRecyclerView;
import cn.nlifew.dmzj.widget.recyclerview.PaddingDividerDecoration;
import cn.nlifew.xdmzj.bean.comic.CommentBean;
import cn.nlifew.xdmzj.bean.comic.ComiclBean;
import cn.nlifew.xdmzj.bean.comic.NoticeBean;
import cn.nlifew.xdmzj.entity.Account;

import static android.app.Activity.RESULT_OK;
import static cn.nlifew.dmzj.fragment.comic.DetailViewModel.CommentWrapper.TYPE_LOAD_MORE;
import static cn.nlifew.dmzj.fragment.comic.DetailViewModel.CommentWrapper.TYPE_REFRESH;

public class DetailFragment extends BaseFragment implements
        LoadMoreRecyclerView.OnLoadMoreListener,
        ViewHolder_Comment.Callback {
    private static final String TAG = "DetailFragment";
    private static final String ARGS_ID = "ARGS_ID";

    public static DetailFragment newInstance(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_ID, id);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = getContext();
        mRecyclerView = new LoadMoreRecyclerView(context);
        mRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setOnLoadMoreListener(this);

        mRecyclerAdapter = new RecyclerAdapterImpl(this);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        int DP15 = DisplayUtils.dp2px(15);
        PaddingDividerDecoration divider = new PaddingDividerDecoration(
                context, PaddingDividerDecoration.VERTICAL);
        divider.setPadding(DP15, 0, DP15 / 4 * 3, 0);
        mRecyclerView.addItemDecoration(divider);

        return mRecyclerView;
    }

    private DetailViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;
    private LoadMoreRecyclerView mRecyclerView;

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new UnsupportedOperationException("use newInstance() to make a instance");
        }
        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel = new ViewModelProvider(getActivity()).get(DetailViewModel.class);
        mViewModel.setId(bundle.getString(ARGS_ID));
        mViewModel.getErrMsg().observe(owner, this::onErrMsgChanged);
        mViewModel.getDetailBean().observe(owner, this::onDetailBeanChanged);
        mViewModel.getNoticeBean().observe(owner, this::onNoticeBeanChanged);
        mViewModel.getCommentBean().observe(owner, this::onCommentBeanChanged);
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore: start");
        mViewModel.loadMoreComment();
    }

    private void onErrMsgChanged(String msg) {
        if (msg == null) {
            return;
        }
        mRecyclerView.setLoadMoreWorking(false);
        ToastUtils.getInstance(getContext()).show(msg);
        if (mWaitingDialog != null) {
            mWaitingDialog.dismiss();
        }
    }

    private void onDetailBeanChanged(ComiclBean bean) {
        if (bean == null) {
            mViewModel.loadDetailInfo();
            return;
        }
        mViewModel.loadNotice();
        mViewModel.refreshComment();
        mRecyclerAdapter.updateHeader(bean);
    }

    private void onCommentBeanChanged(DetailViewModel.CommentWrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        switch (wrapper.type) {
            case TYPE_REFRESH:
                mRecyclerAdapter.refreshComment(wrapper.bean);
                break;
            case TYPE_LOAD_MORE:
                mRecyclerAdapter.appendComment(wrapper.bean);
                break;
        }
        if (mWaitingDialog != null) {
            mWaitingDialog.dismiss();
        }
        mRecyclerView.setLoadMoreWorking(false);
    }

    private void onNoticeBeanChanged(NoticeBean bean) {
        if (bean == null) {
            return;
        }
        mRecyclerAdapter.updateNotice(bean);
    }

    @Override
    public void star(ViewHolder_Comment holder, CommentBean.CommentType target) {
        if (target.is_goods == 1) {
            ToastUtils.getInstance(getContext()).show("已经点过赞啦");
            return;
        }
        target.is_goods = 1;
        target.like_amount ++;
        mRecyclerAdapter.notifyItemChanged(holder.getPosition());
        CommentHelper.star(mViewModel.getId(), target.id, new CommentHelper.Callback() {
            @Override
            public void onSucceed() {
            }

            @Override
            public void onFailed(int errno, String msg) {
                ToastUtils.getInstance(getContext()).show(msg);
                target.is_goods = 0;
                target.like_amount --;
                mRecyclerAdapter.notifyItemChanged(holder.getPosition());
            }
        });
    }

    @Override
    public void reply(ViewHolder_Comment holder, CommentBean.CommentType target) {
        if (Account.getInstance() == null) {
            ToastUtils.getInstance(getContext()).show("您需要先登录");
            return;
        }
        mTargetComment = target;
        Intent intent = new CommentActivity.Builder()
                .setMaxImages(1)
                .build(getContext());
        startActivityForResult(intent, CODE_REQUEST_COMMENT);
    }

    @Override
    public void list(ViewHolder_Comment holder, String[] list, Map<String, CommentBean.CommentType> map) {
        // todo
    }

    private static final int CODE_REQUEST_COMMENT = 86;
    private ProgressDialog mWaitingDialog;
    private CommentBean.CommentType mTargetComment;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != CODE_REQUEST_COMMENT || resultCode != RESULT_OK) {
            return;
        }
        if (mWaitingDialog == null) {
            mWaitingDialog = Helper.makeProgressDialog(getContext());
        }
        mWaitingDialog.show();
        CommentActivity.Result result = new CommentActivity.Result(data);
        CommentHelper.reply(mViewModel.getId(), mTargetComment.id, mTargetComment.sender_uid,
                mTargetComment.origin_comment_id == 0 ? mTargetComment.id : mTargetComment.origin_comment_id,
                result.getText().toString(), new CommentHelper.Callback() {
                    @Override
                    public void onSucceed() {
                        mViewModel.refreshComment();
                    }

                    @Override
                    public void onFailed(int errno, String msg) {
                        mWaitingDialog.dismiss();
                        ToastUtils.getInstance(getContext()).show(msg);
                    }
                });
    }
}
