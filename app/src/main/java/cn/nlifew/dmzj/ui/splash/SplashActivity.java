package cn.nlifew.dmzj.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Calendar;

import cn.nlifew.dmzj.app.ThisApp;
import cn.nlifew.dmzj.ui.BaseActivity;
import cn.nlifew.dmzj.ui.main.MainActivity;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";

    private TextView mTextView;
    private final Handler mH = ThisApp.mH;

    private final Runnable mTimeTask = new Runnable() {

        private int mLeftTimeMs = 2500;

        private final StringBuilder mBuilder = new StringBuilder("这是一个启动页\n(～￣▽￣)～\n ");

        @Override
        public void run() {
            Log.d(TAG, "run: " + mLeftTimeMs);

            mBuilder.setLength(mBuilder.length() - 1);
            mBuilder.append(mLeftTimeMs / 1000 + 1);
            mTextView.setText(mBuilder);

            if (! onTimeLeft(mLeftTimeMs)) {
                mLeftTimeMs -= 500;
                mH.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        mTextView = new TextView(this);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        setContentView(mTextView);
        mH.post(mTimeTask);
    }


    @Override
    protected void onDestroy() {
        mH.removeCallbacks(mTimeTask);
        super.onDestroy();
    }

    private boolean onTimeLeft(int leftTimeMs) {
        switch (leftTimeMs) {
            case 2000:
                return false;
            case 500:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
        }
        return false;
    }
}
