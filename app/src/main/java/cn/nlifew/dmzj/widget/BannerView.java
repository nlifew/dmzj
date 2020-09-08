package cn.nlifew.dmzj.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.nlifew.xdmzj.utils.NetworkUtils;

/**
 * 这个类提供一个非常简单的无限轮播控件，本质上是基于 ViewPager 实现
 * 需要注意的是，不要试图通过在 {@link PagerAdapterImpl#getCount()}
 * 中返回 <code>Integer.MAX_VALUE </code> 实现无限轮播，
 * 这个方法容易引起 ANR，参考 https://www.jianshu.com/p/a7f9f5cdf6f3
 * 与之相对的，我使用了滑动窗口的思路，在 item 的左右分别还有一个缓冲用的 item，
 * 当滑动到缓冲区时，会通过 setPrimaryItem() 切换到 item 的中间位置
 * 举个例子：假设当前实际有 3 个 item，分别为 "item0", "item1", "item2"
 * index:       0       1      2        3       4
 * item:    "item2" "item0" "item1" "item2" "item0"
 * 当用户滑动到 getCurrentItem() == 0 时，因为展示的内容是 item2，仍然有动画效果，
 * 但随后会立即 setCurrentItem(3)，不影响后续滑动
 */

public class BannerView extends ViewPager {
    private static final String TAG = "BannerView";

    private static final int BANNER_PAUSE_TIME_MILLIS = 5000;


    public BannerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private boolean mShouldInit = true;

    private void init(Context context) {
        if (! mShouldInit) {
            return;
        }
        mShouldInit = false;

        mPagerAdapter = new PagerAdapterImpl();
        super.setAdapter(mPagerAdapter);

        addOnPageChangeListener(mPageChangedListener);
    }

    private PagerAdapterImpl mPagerAdapter;

    private final Handler mH = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (true) {
                return;
            }

            int nextItemPosition = getCurrentItem() + 1;

            if (nextItemPosition < mPagerAdapter.getCount()) {
                setCurrentItem(nextItemPosition, true);
            }
            else {
                nextItemPosition = Integer.MAX_VALUE / 8;
                setCurrentItem(nextItemPosition, false);
            }

            sendMessageDelayed(Message.obtain(), BANNER_PAUSE_TIME_MILLIS);
        }
    };

    private final OnPageChangeListener mPageChangedListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected: " + position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state != SCROLL_STATE_IDLE) {
                return;
            }
            int item = getCurrentItem();
            int n = mPagerAdapter.mDataSet.size();
            if (n == 0) {
                return;
            }

            if (item == 0) {
                setCurrentItem(n, false);
            } else if (item == n + 1) {
                setCurrentItem(1, false);
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow: start");
        super.onAttachedToWindow();

//        setCurrentItem(Integer.MAX_VALUE / 8, false);
//        mH.sendMessageDelayed(Message.obtain(), BANNER_PAUSE_TIME_MILLIS);
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow: start");
        super.onDetachedFromWindow();

        mH.removeCallbacksAndMessages(null);
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        throw new UnsupportedOperationException("I have my own PagerAdapter");
    }



    public void setDataSet(BannerItemData[] dataSet) {
        setDataSet(dataSet == null ? null : Arrays.asList(dataSet));
    }

    public void setDataSet(List<BannerItemData> dataSet) {
        mH.removeCallbacksAndMessages(null);
        mPagerAdapter.mDataSet.clear();

        boolean isEmpty = dataSet == null || dataSet.size() == 0;

        if (! isEmpty) {
            mPagerAdapter.mDataSet.addAll(dataSet);
        }

        mPagerAdapter.notifyDataSetChanged();

        if (! isEmpty) {
            setCurrentItem(1, false);
            setOffscreenPageLimit(dataSet.size()); // [1]
            mH.sendMessageDelayed(Message.obtain(), BANNER_PAUSE_TIME_MILLIS);
        }
        // [1]: 现在这个版本的实现有点问题，如果不这么设置，
        // 会有偶发性的卡顿问题，多见于页面创建及销毁的时候，
        // 原因不明。因此只能全部缓存进内存，防止反复创建和销毁。
        // 但这样设置也有问题，此时 page 会被一次性创建出来
        // 在数量较小时问题不大
    }

    public static class BannerItemData {
        public CharSequence title;
        public String cover;
        public OnClickListener cli;
    }

    private final class PagerAdapterImpl extends PagerAdapter {

        private final LinkedList<View> mCachedView = new LinkedList<>();
        private final List<BannerItemData> mDataSet = new ArrayList<>(8);


        private View onCreateView() {
            ImageView iv = new ImageView(getContext());
            iv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            return iv;
        }

        private void onBindView(View view, int position) {
            BannerItemData data = mDataSet.get(position);

            view.setOnClickListener(data.cli);

            Glide.with(getContext())
                    .asBitmap()
                    .load(NetworkUtils.imageUrl(data.cover))
                    .into((ImageView) view);
        }

        @Override
        public int getCount() {
            int n = mDataSet.size();
            return n == 0 ? 0 : n + 2;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view;
            if (mCachedView.size() == 0) {
                Log.d(TAG, "instantiateItem: view at " + position + " no cache found, create one");
                view = onCreateView();
            } else {
                Log.d(TAG, "instantiateItem: view at " + position + " found in cache, reuse");
                view = mCachedView.removeFirst();
            }

            int n = mDataSet.size();
            if (position == 0) {
                onBindView(view, n - 1);
            } else if (position == n + 1) {
                onBindView(view, 0);
            } else {
                onBindView(view, position - 1);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            Log.d(TAG, "destroyItem: destroy and recycle view at " + position);

            View view = (View) object;
            container.removeView(view);
            mCachedView.addLast(view);
        }
    }
}
