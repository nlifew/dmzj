package cn.nlifew.dmzj.ui.comic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.jorgecastilloprz.FABProgressCircle;



public class CircleProgressFab extends FABProgressCircle implements FloatingActionButton {
    private static final String TAG = "CircleProgressFab";

    public CircleProgressFab(Context context) {
        super(context);
    }

    public CircleProgressFab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleProgressFab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private FloatingActionButton mFabImpl;


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        for (int i = 0, n = getChildCount(); i < n; i++) {
            View view = getChildAt(i);
            if (view instanceof FloatingActionButton) {
                mFabImpl = ((FloatingActionButton) view);
                break;
            }
        }
        if (mFabImpl == null) {
            throw new UnsupportedOperationException("I need a FloatingActionButton impl");
        }
    }

    @Override
    public void setTitle(CharSequence text) {
        mFabImpl.setTitle(text);
    }

    @Override
    public CharSequence getTitle() {
        return mFabImpl.getTitle();
    }

    @Override
    public void setTag(int key, Object tag) {
        mFabImpl.setTag(key, tag);
    }

    @Override
    public Object getTag(int key) {
        return mFabImpl.getTag(key);
    }

    @Override
    public TextView getLabelView() {
        return mFabImpl.getLabelView();
    }

    @Override
    public View toView() {
        return this;
    }

    @Override
    public void setFabSize(int size) {
        mFabImpl.setFabSize(size);
    }

    @Override
    public int getFabSize() {
        return mFabImpl.getFabSize();
    }

    @Override
    public void setDrawableIcon(int icon) {
        mFabImpl.setDrawableIcon(icon);
    }

    @Override
    public void setDrawableIcon(Drawable icon) {
        mFabImpl.setDrawableIcon(icon);
    }

    @Override
    public int getColorNormal() {
        return mFabImpl.getColorNormal();
    }

    @Override
    public void setColorNormalResId(int colorRes) {
        mFabImpl.setColorNormalResId(colorRes);
    }

    @Override
    public void setColorNormal(int color) {
        mFabImpl.setColorNormal(color);
    }

    @Override
    public int getColorPressed() {
        return mFabImpl.getColorPressed();
    }

    @Override
    public void setColorPressedResId(int colorRes) {
        mFabImpl.setColorPressedResId(colorRes);
    }

    @Override
    public void setColorPressed(int color) {
        mFabImpl.setColorPressed(color);
    }

    @Override
    public int getColorDisabled() {
        return mFabImpl.getColorDisabled();
    }

    @Override
    public void setColorDisabledResId(int colorRes) {
        mFabImpl.setColorDisabledResId(colorRes);
    }

    @Override
    public void setColorDisabled(int color) {
        mFabImpl.setColorDisabled(color);
    }

    @Override
    public void setStrokeVisible(boolean visible) {
        mFabImpl.setStrokeVisible(visible);
    }

    @Override
    public boolean isStrokeVisible() {
        return mFabImpl.isStrokeVisible();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        mFabImpl.setVisibility(visibility);
    }
}
