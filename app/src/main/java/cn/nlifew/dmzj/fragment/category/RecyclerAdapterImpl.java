package cn.nlifew.dmzj.fragment.category;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.ui.comic.ComicActivity;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.widget.HorizontalListView;
import cn.nlifew.xdmzj.bean.category.CategoryBean;
import cn.nlifew.xdmzj.bean.category.CategoryComicBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

final class RecyclerAdapterImpl extends RecyclerView.Adapter {

    interface OnCategorySelectListener {
        void onCategorySelect(CategoryBean bean, int index);
    }

    private static final int TYPE_CATEGORY  = 1;
    private static final int TYPE_COMIC     = 2;


    RecyclerAdapterImpl(Fragment fragment) {
        if (! (fragment instanceof OnCategorySelectListener)) {
            throw new IllegalStateException("Fragment should implement OnCategorySelectListener");
        }
        mFragment = fragment;
        mCallback = ((OnCategorySelectListener) fragment);
    }

    private final Fragment mFragment;
    private final OnCategorySelectListener mCallback;

    private CategoryBean[] mCategoryList;
    private List<CategoryComicBean> mComicList = new ArrayList<>(32);

    void updateCategory(CategoryBean[] bean) {
        mComicList.clear();
        mCategoryList = bean;
        notifyDataSetChanged();
    }

    /**
     * 更新漫画列表
     * 注意：这里每对 DataSet 做一次操作，都必须通知 RecyclerView 更新
     * 详见： https://www.jianshu.com/p/2eca433869e9
     * @param list list
     */
    void updateComicList(CategoryComicBean[] list) {
        int n = mCategoryList.length;
        int nn = mComicList.size();

        mComicList.clear();
        notifyItemRangeRemoved(n, nn);

        mComicList.addAll(Arrays.asList(list));
        notifyItemRangeInserted(n, list.length);
    }

    void appendComicList(CategoryComicBean[] list) {
        int old = getItemCount();
        mComicList.addAll(Arrays.asList(list));
        notifyItemRangeInserted(old, list.length);
    }

    @Override
    public int getItemViewType(int position) {
        return mCategoryList != null && position < mCategoryList.length ?
                TYPE_CATEGORY : TYPE_COMIC;
    }

    @Override
    public int getItemCount() {
        int num = 0;
        if (mCategoryList != null) {
            num += mCategoryList.length;
        }
        num += mComicList.size();
        return num;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_CATEGORY: {
                HorizontalListView view = new HorizontalListView(mFragment.getContext());
                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        DisplayUtils.dp2px(30)
                );
                lp.leftMargin = lp.rightMargin = DisplayUtils.dp2px(10);
                lp.topMargin = lp.bottomMargin = DisplayUtils.dp2px(5);
                view.setLayoutParams(lp);
                return new CategoryHolder(view);
            }
            case TYPE_COMIC: {
                View view = LayoutInflater.from(mFragment.getContext())
                        .inflate(R.layout.fragment_home_horizontal_item, parent, false);
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                        view.getLayoutParams();
                lp.leftMargin = DisplayUtils.dp2px(20);
                lp.rightMargin = DisplayUtils.dp2px(15);
                lp.topMargin = lp.bottomMargin = DisplayUtils.dp2px(8);
                return new ComicHolder(view);
            }
        }
        throw new IllegalStateException("unknown viewType: " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_CATEGORY:
                ((CategoryHolder) holder).onBindViewHolder(mCategoryList[position]);
                break;
            case TYPE_COMIC:
                if (mCategoryList != null) {
                    position -= mCategoryList.length;
                }
                ((ComicHolder) holder).onBindViewHolder(mComicList.get(position));
                break;
        }
    }


    private final class CategoryHolder extends RecyclerView.ViewHolder implements
            HorizontalListView.OnItemSelectListener{

        CategoryHolder(@NonNull View itemView) {
            super(itemView);
            HorizontalListView view = ((HorizontalListView) itemView);
            view.setOnItemSelectListener(this);
        }

        void onBindViewHolder(CategoryBean bean) {
            if (bean == itemView.getTag()) {
                return;
            }
            itemView.setTag(bean);

            HorizontalListView view = (HorizontalListView) itemView;
            view.clearItems();
            for (int i = 0; i < bean.items.length; i++) {
                CategoryBean.ItemType item = bean.items[i];
                view.appendItem(item.tag_name, i);
            }
            view.commit();
        }

        @Override
        public void onItemSelect(HorizontalListView view, Object tag) {
            CategoryBean bean = (CategoryBean) itemView.getTag();
            mCallback.onCategorySelect(bean, (int) tag);
        }
    }

    private final class ComicHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{

        ComicHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mTitleView = itemView.findViewById(R.id.fragment_home_vertical_item_title);
            mAboutView = itemView.findViewById(R.id.fragment_home_vertical_item_about);
            mDescView = itemView.findViewById(R.id.fragment_home_vertical_item_description);
            mCoverView = itemView.findViewById(R.id.fragment_home_vertical_item_cover);
        }

        private final TextView mTitleView;
        private final TextView mAboutView;
        private final TextView mDescView;
        private final ImageView mCoverView;

        void onBindViewHolder(CategoryComicBean bean) {
            itemView.setTag(bean);

            mTitleView.setText(bean.title);
            mAboutView.setText(bean.types);
            mDescView.setText("作者：" + bean.authors);

            Glide.get(mFragment.getContext())
                    .getRequestManagerRetriever()
                    .get(mFragment)
                    .asBitmap()
                    .load(NetworkUtils.imageUrl(bean.cover))
                    .into(mCoverView);
        }

        @Override
        public void onClick(View v) {
            CategoryComicBean bean = (CategoryComicBean) v.getTag();
            String uri = "dmzj://detail?id=" + bean.id;
            Intent intent = new Intent(mFragment.getContext(), ComicActivity.class);
            intent.setData(Uri.parse(uri));
            mFragment.startActivity(intent);
        }
    }
}
