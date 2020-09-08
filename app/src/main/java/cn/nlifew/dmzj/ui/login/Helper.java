package cn.nlifew.dmzj.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.app.ThisApp;
import cn.nlifew.dmzj.utils.InputUtils;

import static cn.nlifew.dmzj.ui.login.RegisterFragment.OnViewStateChangeListener.VIEW_CREATE;
import static cn.nlifew.dmzj.ui.login.RegisterFragment.OnViewStateChangeListener.VIEW_VISIBLE;

class Helper {
    private static final String TAG = "Helper";

    static final class OrderedQueue {
        private final LinkedList<Runnable> mList = new LinkedList<>();
        private final Handler mH = new Handler();

        void clear() {
            mList.clear();
            mH.removeCallbacksAndMessages(null);
        }

        void run() {
            scheduleNext();
        }

        private void scheduleNext() {
            if (mList.size() > 0) {
                mH.post(mList.removeFirst());
            }
        }

        void post(Animator a) {
            a.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    scheduleNext();
                }
            });
            mList.addLast(a::start);
        }

        void post(Runnable r) {
            mList.addLast(() -> {
                try {
                    r.run();
                } finally {
                    scheduleNext();
                }
            });
        }
    }

    Helper(LoginActivity activity) {
        mActivity = activity;
    }

    private final LoginActivity mActivity;
    private final OrderedQueue mOrderedQueue = new OrderedQueue();

    private boolean mShowingAnimator;
    private RegisterFragment mRegisterFragment;

    private float mFabX0, mFabY0, mFabX1, mFabY1;
    private int mFabRotate0 = 0, mFabRotate1 = 135;
    private FloatingActionButton mFab;

    void changeFragment() {
        if (mShowingAnimator) {
            return;
        }
        if (mRegisterFragment == null) {
            pushRegisterFragment();
        }
        else {
            popRegisterFragment();
        }
    }

    boolean onBackPress() {
        if (mShowingAnimator) {
            return true;                // 如果正在展示动画，消费掉此次事件
        }
        if (mRegisterFragment != null) {
            popRegisterFragment();      // 如果当前 Fragment 是 RegisterFragment，移除掉
            return true;
        }
        return false;
    }

    boolean isShowingAnimator() {
        return mShowingAnimator;
    }

    void onDestroy() {
        mOrderedQueue.clear();
    }

    private void pushRegisterFragment() {
        mShowingAnimator = true;

        // 1. 关闭输入法
        InputUtils.hideSoftInput(mActivity);

        // 2. 提交 Fragment
        mRegisterFragment = new RegisterFragment();
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_login_host, mRegisterFragment)
                .commit();

        mRegisterFragment.registerViewStateChangeListener(new RegisterFragment.OnViewStateChangeListener() {
            @Override
            public void onViewStateChanged(View view, int state) {
                switch (state) {
                    case VIEW_CREATE:
                        view.setVisibility(View.INVISIBLE); // 暂时隐藏 view，防止闪烁
                        break;
                    case VIEW_VISIBLE:
                        // 3. 展示 Fab 动画
                        mOrderedQueue.post(makeFabAnimator(0, 1));

                        // 4. 展示揭露动画
                        mOrderedQueue.post(makeCircleAnimation(view, 0, view.getHeight()));

                        mOrderedQueue.post(() -> mShowingAnimator = false);
                        mOrderedQueue.run();

                        // 5. 获取输入法焦点
                        EditText ev = view.findViewById(R.id.fragment_register_phone);
                        ev.setFocusable(true);
                        ev.requestFocus();

                        mRegisterFragment.removeViewStateChangeListener(this);
                        break;
                }
            }
        });
    }

    private void measureFabCoordinate() {
        if (mFab == null) {
            mFab = mActivity.findViewById(R.id.activity_login_fab);
            CardView cardView = mRegisterFragment.getView().findViewById(R.id.fragment_register_card);
            mFabX0 = mFab.getX();
            mFabY0 = mFab.getY();
            mFabX1 = cardView.getX() + cardView.getWidth() / 2f - mFab.getWidth() /2f;
            mFabY1 = cardView.getY() - mFab.getHeight() /2f;
        }
    }

    private Animator makeFabAnimator(float start, float end) {
        measureFabCoordinate();

        ValueAnimator.AnimatorUpdateListener callback = animation -> {
            final float val = (float) animation.getAnimatedValue();
            mFab.setX(mFabX0 + val * (mFabX1 - mFabX0));
            mFab.setY(mFabY0 + val * (mFabY1 - mFabY0));
            mFab.setRotation(mFabRotate0 + val * (mFabRotate1 - mFabRotate0));
        };

        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(callback);
        return animator;
    }

    private Animator makeCircleAnimation(View view, float startRadius, float endRadius) {
        AnimatorListenerAdapter callback = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        };
        Animator animator = ViewAnimationUtils.createCircularReveal(view,
                view.getWidth() /2, 0, startRadius, endRadius);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(callback);
        return animator;
    }

    private void popRegisterFragment() {
        mShowingAnimator = true;

        // 1. 关闭输入法
        InputUtils.hideSoftInput(mActivity);

        // 2. 展示折叠动画
        View view = mRegisterFragment.getView();
        Animator animator = makeCircleAnimation(view, view.getHeight(), 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
            }
        });
        mOrderedQueue.post(animator);

        // 3. 移除 RegisterFragment
        mOrderedQueue.post(() -> {
            mActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mRegisterFragment)
                    .commit();
            mRegisterFragment = null;
        });

        // 4. 移动 Fab
        mOrderedQueue.post(makeFabAnimator(1, 0));

        mOrderedQueue.post(() -> mShowingAnimator = false);
        mOrderedQueue.run();
    }

//    void setViewBackground(View view, @DrawableRes int res) {
//
//    }
}
