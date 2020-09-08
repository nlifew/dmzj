package com.github.jorgecastilloprz.completefab;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import cn.nlifew.thirdparty.R;


/**
 * We actually don't need to make two FrameLayout.
 * Only one ImageView is enough.
 *
 * @author nlifew
 */
public class CompleteFABView extends ImageView {

    private static final int RESET_DELAY = 3000;

    private int arcColor;
    private CompleteFABListener listener;
    private boolean viewsAdded;

    public CompleteFABView(Context context, Drawable iconDrawable, int arcColor) {
        super(context);
        this.arcColor = arcColor;

        setImageDrawable(iconDrawable != null ? iconDrawable :
                getResources().getDrawable(R.drawable.ic_done));
    }

    public void attachListener(CompleteFABListener listener) {
        this.listener = listener;
    }

    private void tintCompleteFabWithArcColor() {
        Drawable background = getResources().getDrawable(R.drawable.oval_complete);
        background.setColorFilter(arcColor, PorterDuff.Mode.SRC_ATOP);
        setBackground(background);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!viewsAdded) {
            setupContentSize();
            tintCompleteFabWithArcColor();
            viewsAdded = true;
        }
    }

    private void setupContentSize() {
        int contentSize = (int) getResources().getDimension(R.dimen.fab_content_size);
        int mContentPadding = (getMeasuredWidth() - contentSize) / 2;
        setPadding(mContentPadding, mContentPadding, mContentPadding, mContentPadding);
    }

    public void animate(AnimatorSet progressArcAnimator) {
        animate(progressArcAnimator, false);
    }

    private void animate(AnimatorSet progressArcAnimator, boolean inverse) {
        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator completeFabAnim = ObjectAnimator.ofFloat(this, "alpha", inverse ? 0 : 1);
        completeFabAnim.setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator());

        if (inverse) {
            animatorSet.playTogether(completeFabAnim);
            animatorSet.setStartDelay(RESET_DELAY);
            animatorSet.addListener(getInverseAnimatorListener());
        }
        else {
            Interpolator iconAnimInterpolator = new LinearInterpolator();

            ValueAnimator iconScaleAnimX = ObjectAnimator.ofFloat(this, "scaleX", 0, 1);
            ValueAnimator iconScaleAnimY = ObjectAnimator.ofFloat(this, "scaleY", 0, 1);

            iconScaleAnimX.setDuration(250).setInterpolator(iconAnimInterpolator);
            iconScaleAnimY.setDuration(250).setInterpolator(iconAnimInterpolator);

            animatorSet.playTogether(completeFabAnim, progressArcAnimator,
                    iconScaleAnimX, iconScaleAnimY);
            animatorSet.addListener(getAnimatorListener());
        }

        animatorSet.start();
    }

    private Animator.AnimatorListener getAnimatorListener() {
        return new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {
                setVisibility(View.VISIBLE);
            }

            @Override public void onAnimationEnd(Animator animator) {
                if (listener != null) {
                    listener.onCompleteFABAnimationEnd();
                }
            }

            @Override public void onAnimationCancel(Animator animator) {
            }

            @Override public void onAnimationRepeat(Animator animator) {
            }
        };
    }

    private Animator.AnimatorListener getInverseAnimatorListener() {
        return new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {
            }

            @Override public void onAnimationEnd(Animator animator) {
                setVisibility(View.GONE);
            }

            @Override public void onAnimationCancel(Animator animator) {
            }

            @Override public void onAnimationRepeat(Animator animator) {
            }
        };
    }

    public void reset() {
        animate(null, true);
    }

    /**
     * This view must block every touch event so the user cannot click on fab anymore if this view
     * is visible.
     */

    @Override public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
