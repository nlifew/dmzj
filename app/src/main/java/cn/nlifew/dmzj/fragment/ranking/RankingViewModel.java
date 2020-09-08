package cn.nlifew.dmzj.fragment.ranking;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.ArrayMap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import cn.nlifew.dmzj.app.ThisApp;
import cn.nlifew.xdmzj.bean.category.CategoryBean;
import cn.nlifew.xdmzj.bean.category.CategoryComicBean;
import cn.nlifew.xdmzj.bean.ranking.RankingBean;
import cn.nlifew.xdmzj.bean.ranking.RankingComicBean;
import cn.nlifew.xdmzj.request.ICategory;
import cn.nlifew.xdmzj.request.IRanking;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import cn.nlifew.xdmzj.xDmzj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class RankingViewModel extends ViewModel {
    private static final String TAG = "RankingViewModel";

    static final class ComicWrapper {
        RankingComicBean[] list;
        int type;

        static final int TYPE_REFRESH = 1;
        static final int TYPE_LOAD_MORE = 2;

        ComicWrapper(int type, RankingComicBean[] list) {
            this.list = list;
            this.type = type;
        }
    }

    public RankingViewModel() {  }

    private final MutableLiveData<String> mErrMsg
            = new MutableLiveData<>(null);
    private final MutableLiveData<RankingBean[]> mCategory
            = new MutableLiveData<>(null);
    private final MutableLiveData<ComicWrapper> mComicList
            = new MutableLiveData<>();

    private int mPageIndex = 0;
    private Map<RankingBean, Integer> mFilter = new ArrayMap<>(6);

    void loadCategory() {
        mFilter.clear();
        mPageIndex = 0;

        // todo: dmzj 的配置是本地写死了的，这里就手动配置一个
        new LoadRankingTask(this).execute();
    }

    private static final class LoadRankingTask extends AsyncTask<Void, Void, Void> {
        LoadRankingTask(RankingViewModel vm) {
            mThis = new WeakReference<>(vm);
        }

        private final WeakReference<RankingViewModel> mThis;

        @Override
        protected Void doInBackground(Void... voids) {
            AssetManager asset = ThisApp.currentApplication.getAssets();

            Object obj;

            try (InputStream is = asset.open("ranking.json")) {
                Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

                Gson gson = NetworkUtils.gson();
                obj = gson.fromJson(reader, RankingBean[].class);
                reader.close();
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
                obj = e;
            }
            RankingViewModel vm = mThis.get();
            if (vm == null) {
                // nothing to do.
            }
            else if (obj instanceof Throwable) {
                vm.mErrMsg.postValue(obj.toString());
            }
            else {
                vm.mCategory.postValue((RankingBean[]) obj);
            }
            return null;
        }
    }

    void filter(RankingBean bean, int index) {
        mFilter.put(bean, index);
    }

    void refreshComicList() {
        loadComicList(ComicWrapper.TYPE_REFRESH, 0);
    }

    void loadMoreComic() {
        loadComicList(ComicWrapper.TYPE_LOAD_MORE, mPageIndex + 1);
    }

    private void loadComicList(int type, int page) {
        final RankingBean[] beans = mCategory.getValue();
        if (beans == null) {
            mErrMsg.postValue("未发现目录索引");
            return;
        }
        Call<RankingComicBean[]> call = newCall(beans, page);
        call.enqueue(new Callback<RankingComicBean[]>() {
            @Override
            public void onResponse(Call<RankingComicBean[]> call, Response<RankingComicBean[]> response) {
                RankingComicBean[] list = response.body();
                mPageIndex = page;
                mComicList.postValue(new ComicWrapper(type, list));
            }

            @Override
            public void onFailure(Call<RankingComicBean[]> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    private Call<RankingComicBean[]> newCall(RankingBean[] beans, int page) {
        int[] tmp = new int[3];
        for (int i = 0; i < tmp.length; i++) {
            Integer idx = mFilter.get(beans[i]);
            tmp[i] = idx == null ? 0 : beans[i].items[idx].tag_id;
        }
        IRanking ranking = NetworkUtils.create(IRanking.class);
        return ranking.getComicList(tmp[0], tmp[1], tmp[2], page);
    }

    LiveData<String> errMsg() { return mErrMsg; }

    LiveData<RankingBean[]> category() { return mCategory; }

    LiveData<ComicWrapper> comicList() { return mComicList; }
}
