package cn.nlifew.dmzj.ui.reading;

import android.util.LruCache;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.nlifew.xdmzj.bean.reading.ChapterCommentBean;
import cn.nlifew.xdmzj.bean.reading.ReadingBean;

final class DataSet_v2 {
    private static final String TAG = "DataSet_v2";

    static final int TYPE_COMIC     = 1;
    static final int TYPE_COMMENT   = 2;
    static final int TYPE_WAITING   = 3;

    @IntDef({TYPE_COMIC, TYPE_COMMENT, TYPE_WAITING})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {  }

    private final LruCache<Integer, ChapterCommentBean[]> mCommentCache
            = new LruCache<>(4);
    private final LruCache<Integer, ReadingBean> mChapterCache
            = new LruCache<>(4);

    private int mChapterIndex;
    private ReadingBean mChapter;

    @ViewType int getItemViewType(int position) {
        int n = getItemCount();
        if (position == 0 || position == n - 1) {
            return TYPE_WAITING;
        }
        if (position == n - 2) {
            return TYPE_COMMENT;
        }
        return TYPE_COMIC;
    }

    int getItemCount() {
        return mChapter == null ? 1 : mChapter.page_url.length + 2;
    }

    Object getItem(int position) {
        int n = getItemCount();
        if (position == 0 || position == n - 1) {
            return null;
        }
        if (position == n - 2) {
            return mCommentCache.get(mChapterIndex);
        }
        return mChapter.page_url[position - 1];
    }


    boolean hitChapterCache(int index) {
        ReadingBean bean = mChapterCache.get(index);
        if (bean != null) {
            mChapter = bean;
            mChapterIndex = index;
            return true;
        }
        return false;
    }

    int getCurChapterIndex() {
        return mChapterIndex;
    }

    void updateChapter(int index, ReadingBean bean) {
        mChapterCache.put(index, bean);
    }

    void updateComment(int index, ChapterCommentBean[] list) {
        mCommentCache.put(index, list);
    }
}
