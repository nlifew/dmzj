package cn.nlifew.dmzj.widget.recyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.dmzj.utils.DisplayUtils;

public class RoofItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "RoofItemDecoration";

    public interface Callback {
        int getItemType(int position);
        void draw(Canvas c, int l, int t, int r, int b, int type);
    }

    public RoofItemDecoration(@NonNull Callback helper) {
        mCallback = helper;
    }

    private final Callback mCallback;
    private final int DP30 = DisplayUtils.dp2px(30);

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        outRect.set(0, 0, 0, 0);

        int position = parent.getChildAdapterPosition(view);
        if (position < 0) { // 非法指针
            return;
        }

        int curType = mCallback.getItemType(position);
        int lastType = position == 0 ? -1 : mCallback.getItemType(position - 1);

//        Log.d(TAG, String.format("getItemOffsets: [pos, last, cur] = [%d, %d, %d]",
//                position, lastType, curType));

        if (curType != -1 && curType != lastType) {
            outRect.top = DP30;
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int itemCount = state.getItemCount();
        int lastItemType = -1;
        for (int i = 0, n = parent.getChildCount(); i < n; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);

            if (position < 0) { // 非法指针
                continue;
            }

            int curItemType = mCallback.getItemType(position);
            if (curItemType == -1 || lastItemType == curItemType) {
                continue;
            }
            lastItemType = curItemType;


            int bottom = Math.max(DP30, child.getTop());
            if (position + 1 < itemCount) {
                int b;
                int nextItemType = mCallback.getItemType(position + 1);
                if (nextItemType != curItemType && (b = child.getBottom()) <= DP30) {
                    bottom = b;
                }
            }
            int top = bottom - DP30;
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            mCallback.draw(c, left, top, right, bottom, curItemType);
        }
    }
}
