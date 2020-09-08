package cn.nlifew.dmzj.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import cn.nlifew.dmzj.app.ThisApp;

public class BaseFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    public interface OnLazyLoadListener {
        boolean onLazyLoad(BaseFragment fragment);
    }

    private OnLazyLoadListener mLazyLoadListener;
    private boolean mLazyLoadEnabled = true;
    private boolean mLazyLoaded = false;

    @Override
    public void onResume() {
        super.onResume();
        lazyLoadIfReady();
    }

    @Override
    public void onDestroyView() {
        mLazyLoaded = false;
        super.onDestroyView();
    }

    private void lazyLoadIfReady() {
        if (mLazyLoadEnabled && ! mLazyLoaded &&
                (mLazyLoadListener == null || mLazyLoadListener.onLazyLoad(this))) {
            mLazyLoaded = true;
            onLazyLoad();
        }
    }

    @CallSuper
    protected void onLazyLoad() {
        Log.d(TAG, "onLazyLoad: start " + this);
    }

    public void setLazyLoadEnabled(boolean enabled) {
        mLazyLoadEnabled = enabled;
    }

    public void setOnLazyLoadListener(OnLazyLoadListener listener) {
        mLazyLoadListener = listener;
    }

}
