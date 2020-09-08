package cn.nlifew.dmzj.ui.cmtlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DismissView extends View implements
        CoordinatorLayout.AttachedBehavior {
    private static final String TAG = "DismissView";

    public DismissView(Context context) {
        super(context);
    }

    public DismissView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DismissView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    public CoordinatorLayout.Behavior getBehavior() {
        return new DismissBehavior();
    }

    public interface OnStateChangedListener {

        int STATE_PREPARE_COLLAPSE = 1;
        int STATE_FINISH_COLLAPSE = 2;
        int STATE_PREPARE_EXPAND = 3;
        int STATE_FINISH_EXPAND = 4;

        @IntDef({STATE_FINISH_COLLAPSE,
                STATE_FINISH_EXPAND,
                STATE_PREPARE_COLLAPSE,
                STATE_PREPARE_EXPAND})
        @Retention(RetentionPolicy.SOURCE)
        @interface State {  }

        void onStateChanged(DismissView view, @State int state);
    }

    private OnStateChangedListener mStateCallback;

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        mStateCallback = listener;
    }

    public OnStateChangedListener getOnStateChangedListener() {
        return mStateCallback;
    }

    void performChangeState(@OnStateChangedListener.State int state) {
        if (mStateCallback != null) {
            mStateCallback.onStateChanged(this, state);
        }
    }

    OnClickListener mSupportClickListener;

    public void setSupportClickListener(OnClickListener cli) {
        mSupportClickListener = cli;
    }

    public OnClickListener getSupportClickListener() {
        return mSupportClickListener;
    }
}
