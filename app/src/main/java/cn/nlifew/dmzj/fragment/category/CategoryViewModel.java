package cn.nlifew.dmzj.fragment.category;

import android.util.ArrayMap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.Map;

import cn.nlifew.xdmzj.bean.category.CategoryBean;
import cn.nlifew.xdmzj.bean.category.CategoryComicBean;
import cn.nlifew.xdmzj.request.ICategory;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import cn.nlifew.xdmzj.xDmzj;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class CategoryViewModel extends ViewModel {
    private static final String TAG = "RankingViewModel";

    static final class ComicWrapper {
        CategoryComicBean[] list;
        int type;

        static final int TYPE_REFRESH = 1;
        static final int TYPE_LOAD_MORE = 2;

        ComicWrapper(int type, CategoryComicBean[] list) {
            this.list = list;
            this.type = type;
        }
    }

    public CategoryViewModel() {  }

    private final MutableLiveData<String> mErrMsg
            = new MutableLiveData<>(null);
    private final MutableLiveData<CategoryBean[]> mCategory
            = new MutableLiveData<>(null);
    private final MutableLiveData<ComicWrapper> mComicList
            = new MutableLiveData<>();

    private int mPageIndex = 0;
    private Map<CategoryBean, Integer> mFilter = new ArrayMap<>(6);

    void loadCategory() {
        mFilter.clear();
        mPageIndex = 0;

        ICategory category = NetworkUtils.create(ICategory.class);
        Call<CategoryBean[]> call = category.getCategory();
        call.enqueue(new Callback<CategoryBean[]>() {
            @Override
            public void onResponse(Call<CategoryBean[]> call, Response<CategoryBean[]> response) {
                CategoryBean[] beans = response.body();
                int n = beans.length;
                beans = Arrays.copyOf(beans,  n + 1);

                CategoryBean b = beans[n] = new CategoryBean();
                b.title = "排序方式";
                b.items = new CategoryBean.ItemType[]{
                        new CategoryBean.ItemType(0, "人气"),
                        new CategoryBean.ItemType(1, "更新")
                };
                mCategory.postValue(beans);
            }

            @Override
            public void onFailure(Call<CategoryBean[]> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    void filter(CategoryBean bean, int index) {
        mFilter.put(bean, index);
    }

    void refreshComicList() {
        loadComicList(ComicWrapper.TYPE_REFRESH, 0);
    }

    void loadMoreComic() {
        loadComicList(ComicWrapper.TYPE_LOAD_MORE, mPageIndex + 1);
    }


    private void loadComicList(int type, int page) {
        final CategoryBean[] beans = mCategory.getValue();
        if (beans == null) {
            mErrMsg.postValue("未发现目录索引");
            return;
        }
        Call<CategoryComicBean[]> call = newCall(beans, page);
        call.enqueue(new Callback<CategoryComicBean[]>() {
            @Override
            public void onResponse(Call<CategoryComicBean[]> call, Response<CategoryComicBean[]> response) {
                CategoryComicBean[] list = response.body();
                mPageIndex = page;
                mComicList.postValue(new ComicWrapper(type, list));
            }

            @Override
            public void onFailure(Call<CategoryComicBean[]> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    private Call<CategoryComicBean[]> newCall(CategoryBean[] beans, int page) {
        StringBuilder sb = null;
        for (int i = 0; i < beans.length - 1; i++) {
            Integer idx = mFilter.get(beans[i]);
            int tag_id;
            if (idx == null || (tag_id = beans[i].items[idx].tag_id) == 0) {
                continue;
            }
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append(tag_id).append('-');
        }
        String filter = "0";
        if (sb != null) {
            sb.setLength(sb.length() - 1);
            filter = sb.toString();
        }

        int order = 0;
        Integer tmp = mFilter.get(beans[beans.length - 1]);
        if (tmp != null) {
            order = beans[beans.length - 1].items[tmp].tag_id;
        }

        ICategory category = NetworkUtils.create(ICategory.class);
        return category.getComicList(filter, order, page);
    }

    LiveData<String> errMsg() { return mErrMsg; }

    LiveData<CategoryBean[]> category() { return mCategory; }

    LiveData<ComicWrapper> comicList() { return mComicList; }
}
