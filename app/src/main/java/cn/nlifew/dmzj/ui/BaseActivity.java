package cn.nlifew.dmzj.ui;

import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Map;

import cn.nlifew.dmzj.R;

public abstract class BaseActivity extends AppCompatActivity {
    
    
    public void useDefaultLayout(String title) {
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
        setContentView(R.layout.activity_base);

        Toolbar toolbar = findViewById(R.id.activity_base_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
    }

    private static final int CODE_REQUEST_PERMISSION_CALLBACK = 50;

    private Map<String, OnPermRequestListener> mPermMap;

    public interface OnPermRequestListener {
        void onPermRequestFinish(String perm, int resultCode);
    }

    public void requestPermission(String perm, OnPermRequestListener callback) {
        if (checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED) {
            callback.onPermRequestFinish(perm, PackageManager.PERMISSION_GRANTED);
            return;
        }
        if (shouldShowRequestPermissionRationale(perm)) {
            callback.onPermRequestFinish(perm, PackageManager.PERMISSION_DENIED);
            return;
        }
        if (mPermMap == null) {
            mPermMap = new ArrayMap<>(4);
        }
        mPermMap.put(perm, callback);
        requestPermissions(new String[] {perm}, CODE_REQUEST_PERMISSION_CALLBACK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_REQUEST_PERMISSION_CALLBACK) {
            String perm = permissions[0];
            OnPermRequestListener callback = mPermMap.remove(perm);
            callback.onPermRequestFinish(perm, grantResults[0]);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
