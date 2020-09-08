package cn.nlifew.dmzj.fragment.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.utils.ViewUtils;
import static cn.nlifew.dmzj.utils.ViewUtils.getMeasuredHeightWithMargins;

public class HorizontalComicLayout extends ConstraintLayout {
    private static final String TAG = "HorizontalComicLayout";

    public HorizontalComicLayout(Context context) {
        super(context);
    }

    public HorizontalComicLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalComicLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCoverView = findViewById(R.id.fragment_home_vertical_item_cover);
        mTitleView = findViewById(R.id.fragment_home_vertical_item_title);
        mSubtitleView = findViewById(R.id.fragment_home_vertical_item_about);
        mSummaryView = findViewById(R.id.fragment_home_vertical_item_description);
    }

    private ImageView mCoverView;
    private TextView mTitleView;
    private TextView mSubtitleView;
    private TextView mSummaryView;

    private static final int DP6 = DisplayUtils.px2dp(6);
    private static final int DP3 = DisplayUtils.px2dp(3);

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        Log.d(TAG, String.format("onLayout: [%d, %d, %d, %d]", left, top, right, bottom));
        super.onLayout(changed, left, top, right, bottom);

        int leftHeight = getMeasuredHeightWithMargins(mCoverView);
        int rightHeight = getMeasuredHeightWithMargins(mTitleView)
                + getMeasuredHeightWithMargins(mSubtitleView)
                + getMeasuredHeightWithMargins(mSummaryView);

        int dHeight = leftHeight - rightHeight;
        if (dHeight > DP6) {
            mTitleView.offsetTopAndBottom(DP3);
            mSummaryView.offsetTopAndBottom(dHeight - DP3);
            mSubtitleView.offsetTopAndBottom((dHeight - DP6) / 2);
        }
        else if (dHeight > 0) {
            mSummaryView.offsetTopAndBottom(dHeight);
            mSubtitleView.offsetTopAndBottom(dHeight / 2);
        }
        else if (dHeight < 0) {
            int l = mCoverView.getLeft();
            int r = mCoverView.getRight();
            int t = mCoverView.getTop();
            int b = mCoverView.getBottom();
            mCoverView.layout(l, t + dHeight / 2, r, b - dHeight / 2);
        }
    }
}
