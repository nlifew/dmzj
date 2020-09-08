package cn.nlifew.dmzj.fragment.news;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.widget.BannerView;
import cn.nlifew.xdmzj.bean.news.NewsBean;
import cn.nlifew.xdmzj.bean.news.NewsHeaderBean;

final class HeaderViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "HeaderViewHolder";

    private static View makeView(ViewGroup parent) {
        BannerView view = new BannerView(parent.getContext());
        int DP10 = DisplayUtils.dp2px(10);
        view.setPadding(DP10, 0, DP10, 0);

        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
        ));
        view.post(() -> {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                lp.height = view.getMeasuredWidth() * 400 / 750;
                view.setLayoutParams(lp);
            }
        });
        return view;
    }

    HeaderViewHolder(Fragment fragment, ViewGroup parent) {
        super(makeView(parent));
    }

    private NewsHeaderBean mNewsHeader;

    void onBindViewHolder(NewsHeaderBean bean) {
        if (mNewsHeader == bean) {
            return;
        }
        mNewsHeader = bean;

        NewsHeaderBean.DataType[] old = bean.data;
        BannerView.BannerItemData[] wrapper = new BannerView.BannerItemData[old.length];
        for (int i = 0; i < old.length; i++) {
            BannerView.BannerItemData dst = wrapper[i] = new BannerView.BannerItemData();
            NewsHeaderBean.DataType src = old[i];

            dst.title = src.title;
            dst.cover = src.pic_url;
        }
        BannerView banner = (BannerView) itemView;
        banner.setDataSet(wrapper);
    }
}
