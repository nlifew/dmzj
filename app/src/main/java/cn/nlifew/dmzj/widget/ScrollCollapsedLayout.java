package cn.nlifew.dmzj.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.nlifew.dmzj.R;


@SuppressWarnings("WeakerAccess")
public class ScrollCollapsedLayout extends CollapsingToolbarLayout {
    private static final String TAG = "ScrollCollapsedLayout";

    private static Field sTmpRect;
    private static Field sToolBar;
    private static Field sLastInsets;

    static {
        try {
            Class<?> cls = CollapsingToolbarLayout.class;
            sTmpRect = cls.getDeclaredField("tmpRect");
            sTmpRect.setAccessible(true);

            sToolBar = cls.getDeclaredField("toolbar");
            sToolBar.setAccessible(true);

            sLastInsets = cls.getDeclaredField("lastInsets");
            sLastInsets.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.d(TAG, "static initializer: " + e);
        }
    }

    public ScrollCollapsedLayout(@NonNull Context context) {
        this(context, null, 0);
    }

    public ScrollCollapsedLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollCollapsedLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private AppBarOffsetListener mOffsetListener;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            if (mOffsetListener == null) {
                mOffsetListener = new AppBarOffsetListener();
            }
            ((AppBarLayout) parent).addOnOffsetChangedListener(mOffsetListener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mOffsetListener != null) {
            ((AppBarLayout) getParent()).removeOnOffsetChangedListener(mOffsetListener);
        }
        mOffsetListener = null;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout: [" + left + ", " + top + ", " + right + ", " + bottom + "]");
        updateScrollChild(left, top, right, bottom);
    }

    private void updateScrollChild(int l, int t, int r, int b) {
        int collapsedLeft = l;
        int collapsedTop = t;
        int collapsedRight = r;
        int collapsedBottom = b;

        Toolbar mToolbar = grabFromSuper(sToolBar);
        Rect mTmpRect = grabFromSuper(sTmpRect);

        if (mToolbar != null) {
            collapsedLeft = mTmpRect.left;
            collapsedTop = mTmpRect.top;
            collapsedRight = mTmpRect.right;
            collapsedBottom = mTmpRect.bottom;
        }

        for (int i = 0, z = getChildCount(); i < z; i++) {
            final View v = getChildAt(i);

            LayoutParam lp = (LayoutParam) v.getLayoutParams();
            if (lp.mScrollEnabled) {
                lp.setCollapsedBounds(collapsedLeft, collapsedTop, collapsedRight, collapsedBottom);
                lp.setExpandedBounds(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                lp.recalculate();
            }
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParam;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParam(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParam(getContext(), attrs);
    }

    @Override
    protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParam(p);
    }

    public static class LayoutParam extends CollapsingToolbarLayout.LayoutParams {
        public LayoutParam(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.ScrollCollapsedLayout_Layout);

            mScrollEnabled = a.getBoolean(R.styleable.ScrollCollapsedLayout_Layout_scrollEnabled, false);

            mCollapsedGravity = a.getInt(R.styleable.ScrollCollapsedLayout_Layout_collapsedGravity,
                    Gravity.START | Gravity.CENTER_VERTICAL);

            mCollapsedWidth = a.getDimensionPixelSize(R.styleable.ScrollCollapsedLayout_Layout_collapsedWidth,
                    -1);
            mCollapsedHeight = a.getDimensionPixelSize(R.styleable.ScrollCollapsedLayout_Layout_collapsedHeight,
                    -1);

            int collapsedMargin = a.getDimensionPixelSize(R.styleable.ScrollCollapsedLayout_Layout_collapsedMargin,
                    0);
            mCollapsedMargin.top = a.getDimensionPixelSize(R.styleable.ScrollCollapsedLayout_Layout_collapsedMarginTop,
                    collapsedMargin);
            mCollapsedMargin.bottom = a.getDimensionPixelSize(R.styleable.ScrollCollapsedLayout_Layout_collapsedMarginBottom,
                    collapsedMargin);
            mCollapsedMargin.left = a.getDimensionPixelSize(R.styleable.ScrollCollapsedLayout_Layout_collapsedMarginStart,
                    collapsedMargin);
            mCollapsedMargin.right = a.getDimensionPixelSize(R.styleable.ScrollCollapsedLayout_Layout_collapsedMarginEnd,
                    collapsedMargin);

            a.recycle();

            if (getCollapseMode() != COLLAPSE_MODE_OFF) {
                mScrollEnabled = false;
            }

            boolean isRTL = getLayoutDirection() == LAYOUT_DIRECTION_RTL;
            if (isRTL) {
                int tmp = mCollapsedMargin.left;
                mCollapsedMargin.left = mCollapsedMargin.right;
                mCollapsedMargin.right = tmp;
            }
        }

        boolean mScrollEnabled;
        private int mCollapsedGravity;
        private int mCollapsedHeight, mCollapsedWidth;

        private final Rect mCollapsedMargin = new Rect();
        private final Rect mCollapsedRect = new Rect();
        private final Rect mExpandedRect = new Rect();

        void setExpandedBounds(int l, int t, int r, int b) {
            mExpandedRect.left = l;
            mExpandedRect.top = t;
            mExpandedRect.right = r;
            mExpandedRect.bottom = b;
        }

        void setCollapsedBounds(int l, int t, int r, int b) {
            mCollapsedRect.left = l;
            mCollapsedRect.top = t;
            mCollapsedRect.right = r;
            mCollapsedRect.bottom = b;
        }

        void recalculate() {
            if (mCollapsedHeight == -1) {
                mCollapsedHeight = height;
            }
            if (mCollapsedWidth == -1) {
                mCollapsedWidth = width;
            }

            final int collapsedAbsGravity = Gravity.getAbsoluteGravity(
                    mCollapsedGravity, getLayoutDirection()
            );

            switch (collapsedAbsGravity & Gravity.VERTICAL_GRAVITY_MASK) {
                case Gravity.BOTTOM:
                    mCollapsedRect.bottom -= mCollapsedMargin.bottom;
                    mCollapsedRect.top = mCollapsedRect.bottom - mCollapsedHeight;
                    break;
                case Gravity.TOP:
                    mCollapsedRect.top += mCollapsedMargin.top;
                    mCollapsedRect.bottom = mCollapsedRect.top + mCollapsedHeight;
                    break;
                case Gravity.CENTER_VERTICAL:
                default:
                    mCollapsedRect.top = (mCollapsedRect.top + mCollapsedRect.bottom) /2
                            - mCollapsedHeight /2;
                    mCollapsedRect.bottom = mCollapsedRect.top + mCollapsedHeight;
                    break;
            }

            switch (collapsedAbsGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.RIGHT:
                    mCollapsedRect.right -= mCollapsedMargin.right;
                    mCollapsedRect.left = mCollapsedRect.right - mCollapsedWidth;
                    break;
                case Gravity.CENTER_HORIZONTAL:
                    mCollapsedRect.left = (mCollapsedRect.left + mCollapsedRect.right) /2
                            - width /2;
                    mCollapsedRect.right = mCollapsedRect.left + mCollapsedWidth;
                    break;
                case Gravity.LEFT:
                default:
                    mCollapsedRect.left += mCollapsedMargin.left;
                    mCollapsedRect.right = mCollapsedRect.left + mCollapsedWidth;
                    break;
            }
        }


        /* just for compat */

        public LayoutParam(int width, int height) { super(width, height); }

        public LayoutParam(int width, int height, int gravity) { super(width, height, gravity); }

        public LayoutParam(@NonNull ViewGroup.LayoutParams p) { super(p); }

        public LayoutParam(@NonNull MarginLayoutParams source) { super(source); }

        public LayoutParam(@NonNull FrameLayout.LayoutParams source) { super(source); }
    }


    private final class AppBarOffsetListener implements AppBarLayout.OnOffsetChangedListener {


        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            WindowInsetsCompat mLastInsets = grabFromSuper(sLastInsets);

            final int insetTop = mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
            final int expandRange = getHeight() - getMinimumHeight() - insetTop;
            final float percent = -1.0f * verticalOffset / expandRange;

            for (int i = 0, n = getChildCount(); i < n; i++) {
                final View child = getChildAt(i);
                final LayoutParam lp = (LayoutParam) child.getLayoutParams();

                if (! lp.mScrollEnabled) {
                    continue;
                }

                int l = lp.mExpandedRect.left + (int) (percent * (lp.mCollapsedRect.left - lp.mExpandedRect.left));
                int t = lp.mExpandedRect.top  + (int) (percent * (lp.mCollapsedRect.top - lp.mExpandedRect.top));
                int r = lp.mExpandedRect.right + (int) (percent * (lp.mCollapsedRect.right - lp.mExpandedRect.right));
                int b = lp.mExpandedRect.bottom  + (int) (percent * (lp.mCollapsedRect.bottom - lp.mExpandedRect.bottom));

                t += -verticalOffset;
                b += -verticalOffset;

                child.layout(l, t, r, b);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T grabFromSuper(Field field) {
        if (field != null) {
            try {
                return (T) field.get(this);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "quietGet: ", e);
            }
        }
        return null;
    }
}
