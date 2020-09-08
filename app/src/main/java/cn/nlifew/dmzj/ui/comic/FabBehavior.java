package cn.nlifew.dmzj.ui.comic;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import cn.nlifew.dmzj.utils.DisplayUtils;

public class FabBehavior extends CoordinatorLayout.Behavior<FloatingActionsMenu> {
    public FabBehavior() {
        super();
    }

    public FabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull FloatingActionsMenu child,
                                       @NonNull View directTargetChild,
                                       @NonNull View target, int axes, int type) {
        return (axes & View.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                  @NonNull FloatingActionsMenu child,
                                  @NonNull View target,
                                  int dx, int dy,
                                  @NonNull int[] consumed, int type) {
        if (child.isExpanded()) {
            child.collapse();
        }
    }

    private float mMaxY, mCurY;

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                               @NonNull FloatingActionsMenu child,
                               @NonNull View target,
                               int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed,
                               int type, @NonNull int[] consumed) {
        if (mMaxY == 0) {
            // 只针对上下滚动 RecyclerView 适配
            mMaxY = coordinatorLayout.getHeight()
                    - child.getY() - child.getHeight()
                    + DisplayUtils.dp2px(54 + 20);
        }

        if (mCurY >= mMaxY && dyConsumed >= 0 || mCurY <= 0 && dyConsumed <= 0) {
            // 可能超出范围 ?
            return;
        }
        mCurY = Math.min(mMaxY, mCurY + dyConsumed);
        child.setTranslationY(mCurY);
    }
}