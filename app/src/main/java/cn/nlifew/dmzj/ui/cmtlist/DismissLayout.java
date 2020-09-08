package cn.nlifew.dmzj.ui.cmtlist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.nlifew.dmzj.R;

public class DismissLayout extends LinearLayout {
    private static final String TAG = "DismissLayout";


    public DismissLayout(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public DismissLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DismissLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setOrientation(VERTICAL);

        if (mDismissView == null) {
            mDismissView = new View(context);
            mDismissView.setOnClickListener(v -> expand());
            addView(mDismissView, new DismissLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0
            ));
        }

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.DismissLayout);

        setDismissHeight(a.getDimensionPixelSize(R.styleable.DismissLayout_dismissHeight, 0));

        a.recycle();
    }


    private View mDismissView;

    public void setDismissHeight(int height) {
        DismissLayout.LayoutParams lp = (DismissLayout.LayoutParams)
                mDismissView.getLayoutParams();
        if (lp.height != height) {
            lp.height = height;
            mDismissView.setLayoutParams(lp);
        }
    }


    private void expand() {
        mScrolling = true;
        Log.d(TAG, "expand: start");

        ValueAnimator animator = ValueAnimator
                .ofFloat(mDismissView.getTranslationY(), getHeight() - mDismissView.getHeight());
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            Log.d(TAG, "expand: " + animation.getAnimatedValue());
            mDismissView.setTranslationY((float) animation.getAnimatedValue());
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mScrolling = false;
            }
        });
        animator.start();
    }

    private void collapse() {
        mScrolling = true;
        Log.d(TAG, "collapse: start");

        ValueAnimator animator = ValueAnimator
                .ofFloat(mDismissView.getTranslationY(), mDismissView.getHeight());
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(animation -> {
            mDismissView.setTranslationY((int) animation.getAnimatedValue());
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mScrolling = false;
            }
        });
        animator.start();
    }

    private boolean mScrolling;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mScrolling || super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScrolling) return true;

        final int action = event.getActionMasked();
        Log.d(TAG, "onTouchEvent: " + action2String(action));

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                int dy = (int) mDismissView.getTranslationY();
                if (-dy >= mDismissView.getHeight()) {
                    break;
                }
                mLastMotionY = event.getY(0);
                mLastActivePointerId = event.getPointerId(0);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                int idx = event.findPointerIndex(mLastActivePointerId);
                if (idx == -1) {
                    break;
                }
                float y = event.getY(idx);
                float oldTranslationY = mDismissView.getTranslationY();
                mDismissView.setTranslationY(oldTranslationY + y - mLastMotionY);
                mLastMotionY = y;
                return true;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                int idx = event.findPointerIndex(mLastActivePointerId);
                if (idx == -1) {
                    break;
                }
                float translationY = mDismissView.getTranslationY();
                if (translationY < 0) {
                    collapse();
                }
                else if (translationY > 0) {
                    expand();
                }
                mLastActivePointerId = -1;
                mLastMotionY = 0;
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private int mLastActivePointerId = -1;
    private float mLastMotionY;

    private static String action2String(int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN: return "ACTION_DOWN";
            case MotionEvent.ACTION_MOVE: return "ACTION_MOVE";
            case MotionEvent.ACTION_UP: return "ACTION_UP";
            case MotionEvent.ACTION_CANCEL: return "ACTION_CANCEL";
        }
        return Integer.toString(action);
    }
}
