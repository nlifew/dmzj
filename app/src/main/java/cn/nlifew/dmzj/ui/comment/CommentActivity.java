package cn.nlifew.dmzj.ui.comment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.ui.BaseActivity;
import cn.nlifew.dmzj.utils.InputUtils;

public class CommentActivity extends BaseActivity {
    private static final String TAG = "CommentActivity";

    public static final class Builder {
        private static final String PREFIX      = Builder.class.getName();
        private static final String TITLE       = PREFIX + ".EXTRA_TITLE";
        private static final String SUBMIT_TEXT = PREFIX + ".EXTRA_SUBMIT_TEXT";
        private static final String EDIT_HINT   = PREFIX + ".EXTRA_EDIT_HINT";
        private static final String MAX_IMAGES  = PREFIX + ".EXTRA_MAX_IMAGES";

        private final Bundle mBundle;

        public Builder() {
            mBundle = new Bundle();
        }

        Builder(Intent intent) {
            mBundle = intent.getExtras();
            if (mBundle == null) {
                throw new UnsupportedOperationException("use Builder.build() to start this Activity");
            }
        }

        public Builder setTitle(CharSequence title) {
            mBundle.putCharSequence(TITLE, title);
            return this;
        }

        CharSequence getTitle() {
            return mBundle.getCharSequence(TITLE);
        }

        public Builder setSubmitText(CharSequence text) {
            mBundle.putCharSequence(SUBMIT_TEXT, text);
            return this;
        }

        CharSequence getSubmitText() {
            return mBundle.getCharSequence(SUBMIT_TEXT);
        }

        public Builder setEditHint(CharSequence hint) {
            mBundle.putCharSequence(EDIT_HINT, hint);
            return this;
        }

        CharSequence getEditHint() {
            return mBundle.getCharSequence(EDIT_HINT);
        }

        public Builder setMaxImages(int max) {
            mBundle.putInt(MAX_IMAGES, max);
            return this;
        }

        int getMaxImages(int defValue) {
            return mBundle.getInt(MAX_IMAGES, defValue);
        }

        public Intent build(Context context) {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtras(mBundle);
            return intent;
        }
    }

    public static final class Result {
        private static final String PREFIX  = Result.class.getName();
        private static final String TEXT    = PREFIX + ".EXTRA_TEXT";
        private static final String IMAGES  = PREFIX + ".EXTRA_IMAGES";

        Result() {
            mBundle = new Bundle();
        }

        public Result(Intent intent) {
            if (intent == null || (mBundle = intent.getExtras()) == null) {
                throw new NullPointerException("null bundle");
            }
        }

        private final Bundle mBundle;

        Result setText(CharSequence text) {
            mBundle.putCharSequence(TEXT, text == null ? "" : text);
            return this;
        }

        public CharSequence getText() {
            return mBundle.getCharSequence(TEXT);
        }

        Result setImages(Uri[] images) {
            String[] ss = new String[images.length];
            for (int i = 0; i < ss.length; i++) {
                ss[i] = images[i].toString();
            }
            mBundle.putStringArray(IMAGES, ss);
            return this;
        }

        public Uri[] getImages() {
            String[] ss = mBundle.getStringArray(IMAGES);
            if (ss == null) {
                return null;
            }
            Uri[] images = new Uri[ss.length];
            for (int i = 0; i < ss.length; i++) {
                images[i] = Uri.parse(ss[i]);
            }
            return images;
        }

        Intent build() {
            Intent intent = new Intent();
            intent.putExtras(mBundle);
            return intent;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_in_top);

        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0x22000000));

        Builder builder = new Builder(getIntent());
        View view = mHelper.makeView(builder);
        setContentView(view);

        // 监听软键盘弹出，参考 https://www.jianshu.com/p/589a4b828cb6
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new Helper.KeyboardPopupListener(view)
        );
    }

    private final Helper mHelper = new Helper(this);


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_in_top);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mHelper.close();
        return true;
    }
}
