package cn.nlifew.dmzj.widget.recyclerview;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class LoadMoreRecyclerView extends RecyclerView {
    private static final String TAG = "LoadMoreRecyclerView";

    public LoadMoreRecyclerView(@NonNull Context context) {
        super(context);
        mScrollListener = new ScrollListenerImpl();
        addOnScrollListener(mScrollListener);
    }

    public LoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScrollListener = new ScrollListenerImpl();
        addOnScrollListener(mScrollListener);
    }

    public LoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScrollListener = new ScrollListenerImpl();
        addOnScrollListener(mScrollListener);
    }

    private class ScrollListenerImpl extends OnScrollListener {

        private int findMax(int[] ints) {
            int max = ints[0];
            for (int i = 1; i < ints.length; i++) {
                if (max < ints[i]) max = ints[i];
            }
            return max;
        }

        private int[] mLastPositions;

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (dy <= 0 || isLoadMoreWorking() || ! mLoadMoreEnabled) return;

            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
            int lastVisibleItemPosition = 0, totalItemCount = 0;

            if (lm instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager m = (StaggeredGridLayoutManager) lm;
                mLastPositions = m.findLastVisibleItemPositions(mLastPositions);
                lastVisibleItemPosition = findMax(mLastPositions);
                totalItemCount = m.getItemCount();
            }
            else if (lm instanceof LinearLayoutManager) {
                LinearLayoutManager m = (LinearLayoutManager) lm;
                lastVisibleItemPosition = m.findLastVisibleItemPosition();
                totalItemCount = m.getItemCount();
            }

            if (mLoadMoreThreshold == totalItemCount - lastVisibleItemPosition) {
                setLoadMoreWorking(true);
                if (mLoadMoreCallback != null) {
                    mLoadMoreCallback.onLoadMore();
                }
            }
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private ScrollListenerImpl mScrollListener;
    private boolean mLoadMoreEnabled = true;
    private int mLoadMoreThreshold = 1;
    private boolean mIsLoadMoreWorking = false;
    private OnLoadMoreListener mLoadMoreCallback;

    public boolean isLoadMoreWorking() {
        return mIsLoadMoreWorking;
    }

    public void setLoadMoreWorking(boolean working) {
        mIsLoadMoreWorking = working;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreCallback = listener;
    }

    public void setLoadMoreThreshold(int threshold) {
        mLoadMoreThreshold = threshold;
    }

    public void setLoadMoreEnabled(boolean enabled) {
        mLoadMoreEnabled = enabled;
    }
}
