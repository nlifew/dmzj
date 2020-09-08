package cn.nlifew.dmzj.ui.reading;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.LinkedList;
import java.util.Queue;

import cn.nlifew.dmzj.app.ThisApp;
import cn.nlifew.xdmzj.bean.reading.ChapterCommentBean;
import cn.nlifew.xdmzj.bean.reading.ReadingBean;

import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static cn.nlifew.dmzj.ui.reading.DataSet_v2.TYPE_COMIC;
import static cn.nlifew.dmzj.ui.reading.DataSet_v2.TYPE_COMMENT;
import static cn.nlifew.dmzj.ui.reading.DataSet_v2.TYPE_WAITING;

final class PagerAdapterImpl_v2 extends RecyclerView.Adapter {
    private static final String TAG = "PagerAdapterImpl_v2";


    interface PagerCallback {
        void loadComicChapter(int offset);
        void scrollPagerBy(int delta);
    }

    private static final int FLAG_LOADING_NEXT_CHAPTER = 1 << 1;
    private static final int FLAG_LOADING_LAST_CHAPTER = 1 << 2;
    private static final int FLAG_LOADING_CURRENT_CHAPTER =
            FLAG_LOADING_LAST_CHAPTER | FLAG_LOADING_NEXT_CHAPTER;


    PagerAdapterImpl_v2(ReadingActivity activity) {
        mActivity = activity;
        if (! (activity instanceof PagerCallback)) {
            throw new UnsupportedOperationException("Activity must implement PagerCallback");
        }
        mCallback = (PagerCallback) activity;
    }

    private final PagerCallback mCallback;
    private final ReadingActivity mActivity;
    private final DataSet_v2 mDataSet = new DataSet_v2();
    private final PagerStateChangedListener mPageListener
            = new PagerStateChangedListener();

    private int mFlag;
    private ViewPager2 mViewPager;

    void attachToViewPager2(ViewPager2 pager2) {
        pager2.setAdapter(this);
        pager2.registerOnPageChangeCallback(mPageListener);

        mViewPager = pager2;
    }

    void clearFlag() { mFlag = 0; }

    void updateChapter(int index, ReadingBean bean, int page) {
        mPageListener.runWhenPagerStopped(() -> updateChapterImpl(index, bean, page));
    }

    private void updateChapterImpl(int index, ReadingBean bean, int page) {
        mDataSet.updateChapter(index, bean);

        int pagerIndex = mViewPager.getCurrentItem();
        int pagerCount = mDataSet.getItemCount();
        int curChapterIdx = mDataSet.getCurChapterIndex();

        if (pagerCount == 1) {
            mDataSet.hitChapterCache(0);
            notifyDataSetChanged();
            mViewPager.setCurrentItem(page + 1, false);
            mFlag &= ~FLAG_LOADING_CURRENT_CHAPTER;
            return;
        }

        if (index > curChapterIdx) {
            if (index == curChapterIdx + 1 && pagerIndex == pagerCount - 1) { // 手动触发一次更新
                mDataSet.hitChapterCache(index);
                notifyDataSetChanged();
                mViewPager.setCurrentItem(1, false);
            }
            mFlag &= ~FLAG_LOADING_NEXT_CHAPTER;
            return;
        }

        if (index < curChapterIdx) {
            if (index == curChapterIdx - 1 && pagerIndex == 0 ) {  // 手动触发一次更新
                mDataSet.hitChapterCache(index);
                notifyDataSetChanged();
                mViewPager.setCurrentItem(mDataSet.getItemCount() - 2, false);
            }
            mFlag &= ~FLAG_LOADING_LAST_CHAPTER;
        }
    }

    void updateComment(int index, ChapterCommentBean[] list) {
        mDataSet.updateComment(index, list);

        if (index == mDataSet.getCurChapterIndex()) {
            notifyItemChanged(mDataSet.getItemCount() - 2);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_COMIC:    return new ViewHolder_Comic(mActivity, parent);
            case TYPE_COMMENT:  return new ViewHolder_Comment(mActivity, parent);
            case TYPE_WAITING:  return new ViewHolder_Waiting(mActivity, parent);
        }
        throw new IllegalStateException("unknown viewType:" + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int type = mDataSet.getItemViewType(position);
        final Object obj = mDataSet.getItem(position);

        switch (type) {
            case TYPE_COMIC:
                ((ViewHolder_Comic) holder).onBindViewHolder((String) obj);
                break;
            case TYPE_COMMENT:
                ((ViewHolder_Comment) holder).onBindViewHolder((ChapterCommentBean[]) obj);
                break;
            case TYPE_WAITING:
                break;
        }
    }


    private final class PagerStateChangedListener extends ViewPager2.OnPageChangeCallback {
        private static final String TAG = "PagerStateChangedListen";

        private @Nullable Queue<Runnable> mTaskQueue;

        void runWhenPagerStopped(Runnable r) {
            if (mTaskQueue == null) {
                mTaskQueue = new LinkedList<>();
            }
            mTaskQueue.add(r);
            if (mViewPager.getScrollState() == SCROLL_STATE_IDLE) {
                ThisApp.mH.postAtFrontOfQueue(() -> {
                    Runnable rr;
                    while ((rr = mTaskQueue.poll()) != null) {
                        rr.run();
                    }
                });
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }


        private void updateDataSetIfReady() {
            final int position = mViewPager.getCurrentItem();
            final int count = mDataSet.getItemCount();
            final int chapterIndex = mDataSet.getCurChapterIndex();

            // 第一次加载数据
            if (count == 1) {
                if ((mFlag & FLAG_LOADING_CURRENT_CHAPTER) == 0) {
                    mFlag |= FLAG_LOADING_CURRENT_CHAPTER;
                    mCallback.loadComicChapter(0);
                }
                return;
            }

            // 当滑动到最后一页时
            if (position == count - 1) {
                if (mDataSet.hitChapterCache(chapterIndex + 1)) {
                    notifyDataSetChanged();
                    mViewPager.setCurrentItem(1, false);
                }
                else if ((mFlag & FLAG_LOADING_NEXT_CHAPTER) == 0) {
                    mFlag |= FLAG_LOADING_NEXT_CHAPTER;
                    mCallback.loadComicChapter(chapterIndex + 1);
                }
                return;
            }

            // 当滑动到第一页时
            if (position == 0) {
                if (mDataSet.hitChapterCache(chapterIndex - 1)) {
                    notifyDataSetChanged();
                    mViewPager.setCurrentItem(mDataSet.getItemCount() - 2, false);
                }
                else if ((mFlag & FLAG_LOADING_LAST_CHAPTER) == 0) {
                    mFlag |= FLAG_LOADING_LAST_CHAPTER;
                    mCallback.loadComicChapter(chapterIndex - 1);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected: " + position);
            if (position == 0 || position == mDataSet.getItemCount() - 1) {
                runWhenPagerStopped(this::updateDataSetIfReady);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.d(TAG, "onPageScrollStateChanged: " + state2String(state));

            if (state == SCROLL_STATE_IDLE
                    && mTaskQueue != null
                    && mTaskQueue.peek() != null) {

                // 即使是 SCROLL_STATE_IDLE，此时 RecyclerView 仍然在 scroll
                // 如果此时更新 Adapter 也是不可以的，所以只能依赖 Handler
                ThisApp.mH.postAtFrontOfQueue(() -> {
                    Runnable r;
                    while ((r = mTaskQueue.poll()) != null) {
                        r.run();
                    }
                });
            }
        }

        private String state2String(int state) {
            switch (state) {
                case SCROLL_STATE_IDLE: return "SCROLL_STATE_IDLE";
                case ViewPager2.SCROLL_STATE_DRAGGING: return "SCROLL_STATE_DRAGGING";
                case ViewPager2.SCROLL_STATE_SETTLING: return "SCROLL_STATE_SETTLING";
            }
            return "unknown state: " + state;
        }
    }
}
