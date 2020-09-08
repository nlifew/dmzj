package cn.nlifew.dmzj.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.nlifew.dmzj.R;

@SuppressLint("AppCompatCustomView")
public class PercentImageView extends ImageView {
    private static final String TAG = "PercentImageView";

    public static final int FLAG_ALIGN_NULL = 0;
    public static final int FLAG_ALIGN_WIDTH = 0x1;
    public static final int FLAG_ALIGH_HEIGHT = 0x2;


    @IntDef({FLAG_ALIGH_HEIGHT, FLAG_ALIGN_WIDTH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlignSite{}

    public PercentImageView(Context context) {
        super(context);
        init(context, null);
    }

    public PercentImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PercentImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private boolean mShouldInit = true;
    private float mAlignPercent;
    private @AlignSite int mAlignSite = FLAG_ALIGN_NULL;

    private void init(Context context, AttributeSet attrs) {
        if (!mShouldInit) {
            return;
        }
        mShouldInit = false;

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PercentImageView);

        mAlignSite = a.getInt(R.styleable.PercentImageView_alignSide, 0);
        mAlignPercent = a.getFloat(R.styleable.PercentImageView_alignPercent, 1);

        a.recycle();
    }

    public void setAlignPercent(float percent) {
        mAlignPercent = percent;
    }

    public void setAlignSite(@AlignSite int site) {
        mAlignSite = site;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth(), h = getMeasuredHeight();

        if (mAlignSite == FLAG_ALIGN_WIDTH) {
            h = Math.round(w * mAlignPercent);
        }
        else if (mAlignSite == FLAG_ALIGH_HEIGHT) {
            w = Math.round(h * mAlignPercent);
        }
        setMeasuredDimension(w, h);
    }
}
