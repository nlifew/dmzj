package com.getbase.floatingactionbutton;


import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface FloatingActionButton {

  int SIZE_NORMAL = 0;
  int SIZE_MINI = 1;


  void setTitle(CharSequence text);

  CharSequence getTitle();

  void setTag(int key, Object tag);

  Object getTag(int key);

  TextView getLabelView();

  View toView();

  void setFabSize(int size);

  int getFabSize();

  void setDrawableIcon(@DrawableRes int icon);

  void setDrawableIcon(Drawable icon);

  int getColorNormal();

  void setColorNormalResId(@ColorRes int colorRes);

  void setColorNormal(int color);

  int getColorPressed();

  void setColorPressedResId(@ColorRes int colorRes);

  void setColorPressed(int color);

  int getColorDisabled();

  void setColorDisabledResId(@ColorRes int colorRes);

  void setColorDisabled(int color);

  void setStrokeVisible(boolean visible);

  boolean isStrokeVisible();

  void setVisibility(int visible);
}
