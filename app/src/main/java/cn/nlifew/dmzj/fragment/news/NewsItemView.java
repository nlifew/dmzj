package cn.nlifew.dmzj.fragment.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.utils.DisplayUtils;

@Deprecated
public class NewsItemView extends RelativeLayout {
    private static final String TAG = "NewsItemView";

    public NewsItemView(Context context) {
        super(context);
    }

    public NewsItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    private TextView mTitleView;
//    private View mHeadView;
    private boolean mAllowRequestLayout = true;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        mTitleView = findViewById(R.id.fragment_news_item_title);
//        mHeadView = findViewById(R.id.fragment_news_item_head);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        Integer tag = (Integer) mTitleView.getTag(R.id.view_tag_1);
//        int oldLineCount = tag == null ? 0 : tag;
//        int newLineCount = mTitleView.getLineCount();
//        mTitleView.setTag(R.id.view_tag_1, newLineCount);
//
//        MarginLayoutParams lp;
//        if (newLineCount < 2 && oldLineCount == 2) {
//            mAllowRequestLayout = false;
//
//            lp = (MarginLayoutParams) mTitleView.getLayoutParams();
//            lp.topMargin += DisplayUtils.dp2px(5);
//            mTitleView.setLayoutParams(lp);
//
//            lp = (MarginLayoutParams) mHeadView.getLayoutParams();
//            lp.bottomMargin += DisplayUtils.dp2px(5);
//            mHeadView.setLayoutParams(lp);
//
//            mAllowRequestLayout = true;
//        }
//        else if (newLineCount == 2 && oldLineCount < 2) {
//            mAllowRequestLayout = false;
//
//            lp = (MarginLayoutParams) mTitleView.getLayoutParams();
//            lp.topMargin -= DisplayUtils.dp2px(5);
//            mTitleView.setLayoutParams(lp);
//
//            lp = (MarginLayoutParams) mHeadView.getLayoutParams();
//            lp.bottomMargin -= DisplayUtils.dp2px(5);
//            mHeadView.setLayoutParams(lp);
//
//            mAllowRequestLayout = true;
//        }
    }

    @Override
    public void requestLayout() {
        if (mAllowRequestLayout) {
            super.requestLayout();
        }
    }
}
