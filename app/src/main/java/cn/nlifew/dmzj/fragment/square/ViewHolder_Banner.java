package cn.nlifew.dmzj.fragment.square;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import java.util.LinkedList;

import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.utils.ToastUtils;
import cn.nlifew.dmzj.widget.BannerView;
import cn.nlifew.xdmzj.bean.SquareBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

final class ViewHolder_Banner extends RecyclerView.ViewHolder
        /* implements LifecycleEventObserver */{
    private static final String TAG = "ViewHolder_Banner";

//    private static final long BANNER_PAUSE_TIME_MILLIS = 5000;

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

    ViewHolder_Banner(Fragment fragment, ViewGroup parent) {
        super(makeView(parent));
//        mFragment = fragment;
    }

//    private final Fragment mFragment;
    private SquareBean mDataSet;

    void onBindViewHolder(SquareBean bean) {
        if (bean == mDataSet) {
            return;
        }
        mDataSet = bean;

        SquareBean.DataType[] array = bean.data;
        BannerView.BannerItemData[] wrapper = new BannerView.BannerItemData[array.length];
        for (int i = 0; i < array.length; i++) {
            BannerView.BannerItemData dst = wrapper[i]
                    = new BannerView.BannerItemData();
            SquareBean.DataType src = array[i];

            dst.title = src.title;
            dst.cover = src.cover;
        }
        BannerView view = (BannerView) itemView;
        view.setDataSet(wrapper);
    }

    /*
    private final class BannerAdapter extends PagerAdapter {
        private final LinkedList<View> mCachedView = new LinkedList<>();


        private View onCreateView() {
            ImageView iv = new ImageView(mFragment.getContext());
            iv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            return iv;
        }

        private void onBindView(View view, int position) {
            SquareBean.DataType data = mDataSet[position];

            view.setOnClickListener(this::onBannerViewClick);

            Glide.get(mFragment.getContext())
                    .getRequestManagerRetriever()
                    .get(mFragment)
                    .asBitmap()
                    .load(NetworkUtils.imageUrl(data.cover))
                    .into((ImageView) view);
        }

        private void onBannerViewClick(View view) {
            ToastUtils.getInstance(mFragment.getContext()).show("click !");
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = mCachedView.size() == 0 ? onCreateView() : mCachedView.removeFirst();
            int n;
            if (mDataSet != null && (n = mDataSet.length) != 0) {
                onBindView(view, position % n);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
            mCachedView.addLast(view);
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_START:
                mH.sendMessageDelayed(Message.obtain(), BANNER_PAUSE_TIME_MILLIS);
                break;
            case ON_STOP:
                mH.removeCallbacksAndMessages(null);
                break;
            case ON_DESTROY:
                mFragment.getLifecycle().removeObserver(this);
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mH = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
//            Log.d(TAG, "handleMessage: " + System.currentTimeMillis());

            ViewPager pager = (ViewPager) itemView;

            int nextItemPosition = pager.getCurrentItem() + 1;
            if (nextItemPosition != mBannerAdapter.getCount()) {
                pager.setCurrentItem(nextItemPosition, true);
            }
            else {
                nextItemPosition = Integer.MAX_VALUE / 8;
                pager.setCurrentItem(nextItemPosition, false);
            }

            sendMessageDelayed(Message.obtain(), BANNER_PAUSE_TIME_MILLIS);
        }
    };
     */
}
