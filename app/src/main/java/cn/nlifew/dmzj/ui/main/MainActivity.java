package cn.nlifew.dmzj.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.settings.Settings;
import cn.nlifew.dmzj.ui.BaseActivity;
import cn.nlifew.dmzj.ui.login.LoginActivity;
import cn.nlifew.dmzj.utils.ToastUtils;
import cn.nlifew.xdmzj.entity.Account;
import cn.nlifew.xdmzj.utils.NetworkUtils;

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener {
    private static final String TAG = "MainActivity";

    private static final int CODE_ACTIVITY_LOGIN = 20;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;

    private boolean mShouldUpdateAccountInfo = true;
    private boolean mShouldUpdateNavMenu = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.activity_main_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mDrawer.addDrawerListener(toggle);
        mDrawer.addDrawerListener(this);
        toggle.syncState();

        mNavigationView = findViewById(R.id.activity_main_nav);
        mNavigationView.setNavigationItemSelectedListener(this);

        TabLayout tab = findViewById(R.id.activity_main_tab);
        ViewPager pager = findViewById(R.id.activity_main_pager);
        MainAdapter adapter = new MainAdapter(this);
        adapter.bind(tab, pager);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_main_night: {
                Settings settings = Settings.getInstance(this);
                boolean night = settings.isNightMode();
                settings.setNightMode(! night);
                AppCompatDelegate.setDefaultNightMode(night ?
                        AppCompatDelegate.MODE_NIGHT_NO :
                        AppCompatDelegate.MODE_NIGHT_YES);
                break;
            }
            case R.id.activity_main_settings: {
//                Intent intent = new Intent(this, SettingsActivity.class);
//                startActivity(intent);
                break;
            }
            case R.id.activity_main_bookshelf: {
//                Account account = Account.currentAccount();
//                if (account == null) {
//                    Intent intent = new Intent(this, SMSLoginActivity.class);
//                    startActivityForResult(intent, CODE_ACTIVITY_LOGIN);
//                }
//                else {
//                    Intent intent = new Intent(this, ShelfActivity.class);
//                    startActivity(intent);
//                }
                break;
            }
            case R.id.activity_main_about: {
//                Intent intent = new Intent(this, AboutActivity.class);
//                startActivity(intent);
                break;
            }
            case R.id.activity_main_feedback: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=2223574948"));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "onNavigationItemSelected: ", e);
                    ToastUtils.getInstance(this).show("启动QQ失败" + e.toString());
                }
                break;
            }
            case R.id.activity_main_history: {
//                Intent intent = new Intent(this, HistoryActivity.class);
//                startActivity(intent);
            }
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void updateAccountInfo() {
        if (! mShouldUpdateAccountInfo) {
            return;
        }
        mShouldUpdateAccountInfo = false;

        View headerView = mNavigationView.getHeaderView(0);
        TextView nameView = headerView.findViewById(R.id.activity_main_name);
        ImageView head = headerView.findViewById(R.id.activity_main_image);
        head.setOnClickListener((v) -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, CODE_ACTIVITY_LOGIN);
        });

        Account account = Account.getInstance();
        if (account == null) {
            return;
        }

        nameView.setText(account.name);
        RequestOptions options = RequestOptions
                .errorOf(R.drawable.ic_account_circle_white_48dp);

        Glide.get(this)
                .getRequestManagerRetriever()
                .get(this)
                .asBitmap()
                .apply(options)
                .load(NetworkUtils.imageUrl(account.photo))
                .into(head);
    }

    private void updateNavMenu() {
        if (! mShouldUpdateNavMenu) {
            return;
        }
        mShouldUpdateNavMenu = false;

        Menu menu = mNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.activity_main_night);
        SwitchCompat swch = (SwitchCompat) item.getActionView();

        swch.setChecked(Settings.getInstance(this).isNightMode());
        swch.setOnCheckedChangeListener((view, isChecked) -> {
            onNavigationItemSelected(item);
        });
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        updateAccountInfo();
        updateNavMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode " + requestCode + " resultCode " + resultCode);

        if (requestCode == CODE_ACTIVITY_LOGIN && resultCode == RESULT_OK) {
            mShouldUpdateAccountInfo = true;
            updateAccountInfo();
        }
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        // eat it.
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        // eat it.
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        // eat it.
    }
}
