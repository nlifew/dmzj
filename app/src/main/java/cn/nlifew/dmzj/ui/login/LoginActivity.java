package cn.nlifew.dmzj.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.ui.BaseActivity;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(0xFFFFCC00);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_login);

//        View view = findViewById(R.id.activity_login_host);
//        mHelper.setViewBackground(view, R.drawable.bg_login);

        FloatingActionButton fab = findViewById(R.id.activity_login_fab);
        fab.setOnClickListener(v -> mHelper.changeFragment());

        LoginFragment fragment = new LoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_login_host, fragment)
                .commit();
    }

    private final Helper mHelper = new Helper(this);


    @Override
    protected void onDestroy() {
        mHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (! mHelper.onBackPress()) {
            super.onBackPressed();
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mHelper.isShowingAnimator() || super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mHelper.isShowingAnimator() || super.onTouchEvent(event);
    }
}
