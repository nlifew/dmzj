package cn.nlifew.dmzj.fragment.chapter;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import cn.nlifew.xdmzj.bean.comic.ComiclBean;


public final class ChapterViewModel extends ViewModel {
    private static final String TAG = "ChapterViewModel";


    public ChapterViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mChapters = new MutableLiveData<>(null);
    }

    private final MutableLiveData<String> mErrMsg;
    private final MutableLiveData<ComiclBean.ChapterDataType[]> mChapters;
    private ComiclBean.ChapterDataType[] mFilterBackup;

    void refreshChapters() {
        if (mFilterBackup != null) {
            mChapters.postValue(mFilterBackup);
        }
    }

    void reverseChapters() {
        ComiclBean.ChapterDataType[] chapters = mChapters.getValue();
        if (chapters == null) {
            mErrMsg.postValue("空空如也");
        }
        new ReverseChapters(mChapters).execute(chapters);
    }

    void filterChapters(String s) {
        ComiclBean.ChapterDataType[] chapters = mChapters.getValue();
        if (chapters == null) {
            mErrMsg.postValue("空空如也");
            return;
        }
        new FilterChapters(mChapters, s).execute(chapters);
    }

    private static final class ReverseChapters extends AsyncTask<
            ComiclBean.ChapterDataType, Void, Void> {

        ReverseChapters(MutableLiveData<ComiclBean.ChapterDataType[]> target) {
            mTarget = target;
        }

        private final MutableLiveData<ComiclBean.ChapterDataType[]> mTarget;

        @Override
        protected Void doInBackground(ComiclBean.ChapterDataType[] src) {
            ComiclBean.ChapterDataType[] dst = new ComiclBean.ChapterDataType[src.length];
            int i = 0, j = src.length - 1;
            while (i < src.length) {
                dst[j--] = src[i++];
            }
            mTarget.postValue(dst);
            return null;
        }
    }

    private static final class FilterChapters extends AsyncTask
            <ComiclBean.ChapterDataType, Void, Void> {

        FilterChapters(MutableLiveData<ComiclBean.ChapterDataType[]> target, String filter) {
            mTarget = target;
            mFilter = filter;
        }

        private final String mFilter;
        private final MutableLiveData<ComiclBean.ChapterDataType[]> mTarget;

        @Override
        protected Void doInBackground(ComiclBean.ChapterDataType... chapters) {
            List<ComiclBean.ChapterDataType> list = new ArrayList<>(chapters.length);
            for (ComiclBean.ChapterDataType chapter : chapters) {
                if (chapter.chapter_title.contains(mFilter)) {
                    list.add(chapter);
                }
            }
            chapters = new ComiclBean.ChapterDataType[list.size()];
            mTarget.postValue(list.toArray(chapters));

            return null;
        }
    }

    LiveData<String> getErrMsg() { return mErrMsg; }

    LiveData<ComiclBean.ChapterDataType[]> getChapterList() { return mChapters; }

    public void updateChapters(ComiclBean.ChapterDataType[] chapters) {
        mChapters.postValue(chapters);
        mFilterBackup = chapters;
    }
}
