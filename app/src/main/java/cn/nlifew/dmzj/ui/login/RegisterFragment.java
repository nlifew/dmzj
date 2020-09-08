package cn.nlifew.dmzj.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.BaseFragment;

public class RegisterFragment extends BaseFragment {
    private static final String TAG = "RegisterFragment";

    interface OnViewStateChangeListener {
        int VIEW_CREATE = 1;
        int VIEW_VISIBLE = 2;
        int VIEW_DESTROY = 3;

        @IntDef({VIEW_CREATE, VIEW_VISIBLE, VIEW_DESTROY})
        @Retention(RetentionPolicy.SOURCE)
        @interface State {}

        void onViewStateChanged(View view, @State int state);
    }

    private List<OnViewStateChangeListener> mViewStateObservers;


    void registerViewStateChangeListener(OnViewStateChangeListener callback) {
        if (callback == null) {
            throw new NullPointerException("callback is null");
        }
        if (mViewStateObservers == null) {
            mViewStateObservers = new ArrayList<>(1);
        }
        mViewStateObservers.add(callback);
    }

    void removeViewStateChangeListener(OnViewStateChangeListener callback) {
        if (mViewStateObservers != null) {
            mViewStateObservers.remove(callback);
        }
    }

    private void performChangeViewState(View view, @OnViewStateChangeListener.State int state) {
        if (mViewStateObservers != null) {
            for (OnViewStateChangeListener listener : mViewStateObservers) {
                listener.onViewStateChanged(view, state);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
        /*
        view.setVisibility(View.INVISIBLE);
        view.post(() -> {
            Animator animator = ViewAnimationUtils.createCircularReveal(view,
                    view.getWidth() / 2, 0, 0, view.getHeight());
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(5000);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }
            });
            animator.start();
        });
         */
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        performChangeViewState(view, OnViewStateChangeListener.VIEW_CREATE);
        view.post(() -> performChangeViewState(view, OnViewStateChangeListener.VIEW_VISIBLE));
    }

    @Override
    public void onDestroyView() {
        performChangeViewState(getView(), OnViewStateChangeListener.VIEW_DESTROY);
        super.onDestroyView();
    }
}
