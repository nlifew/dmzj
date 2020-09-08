package cn.nlifew.dmzj.ui.reading;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class ReadingView extends ImageView {
    public ReadingView(Context context) {
        super(context);
    }

    public ReadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ReadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private final PointF mTouchArea = new PointF();

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mTouchArea.x = event.getX();
        mTouchArea.y = event.getY();
        return super.dispatchTouchEvent(event);
    }

    public PointF getTouchArea() {
        return mTouchArea;
    }
}
