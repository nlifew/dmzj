package cn.nlifew.dmzj.ui.comic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class CommentBehavior extends CoordinatorLayout.Behavior<View> {
    private static final String TAG = "CommentBehavior";

    public CommentBehavior() {
        super();
    }

    public CommentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull View child,
                                       @NonNull View directTargetChild,
                                       @NonNull View target,
                                       int axes, int type) {
        return (axes & View.SCROLL_AXIS_VERTICAL) != 0;
    }

    private float mViewHeight;

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                               @NonNull View child,
                               @NonNull View target,
                               int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed,
                               int type, @NonNull int[] consumed) {
        /*
         * 逻辑处理放在 onNestedScroll() 而不是 onNestedPreScroll() 中
         * 只有在 AppBarLayout 完全隐藏后才能触发 EditText 弹出
         */

        if (mViewHeight == 0) {
            mViewHeight = child.getHeight();
        }
        float curHeight = -child.getTranslationY();
        float y = 0;
        if (dyConsumed > 0 && curHeight < mViewHeight) {
            y = -Math.min(mViewHeight - curHeight, dyConsumed);
        }
        else if (dyConsumed < 0 && curHeight > 0) {
            y = Math.min(curHeight, -dyConsumed);
        }
        child.setTranslationY(-curHeight + y);
    }
}
