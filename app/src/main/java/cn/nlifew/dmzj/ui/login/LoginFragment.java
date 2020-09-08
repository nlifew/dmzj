package cn.nlifew.dmzj.ui.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.fragment.BaseFragment;
import cn.nlifew.dmzj.utils.ToastUtils;

import static cn.nlifew.dmzj.ui.login.LoginViewModel.Result.TYPE_DIALOG;
import static cn.nlifew.dmzj.ui.login.LoginViewModel.Result.TYPE_ID;
import static cn.nlifew.dmzj.ui.login.LoginViewModel.Result.TYPE_OK;
import static cn.nlifew.dmzj.ui.login.LoginViewModel.Result.TYPE_PASSWORD;
import static cn.nlifew.dmzj.ui.login.LoginViewModel.Result.TYPE_TOAST;

public class LoginFragment extends BaseFragment {
    private static final String TAG = "LoginFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mIdView = view.findViewById(R.id.fragment_login_id);

        mPasswordView = view.findViewById(R.id.fragment_login_password);
        mPasswordView.setOnEditorActionListener((tv, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                tryToLogin();
            }
            return false;
        });

        Button button = view.findViewById(R.id.fragment_login_go);
        button.setOnClickListener(v -> tryToLogin());

        return view;
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mViewModel.getLoginResult().observe(owner, this::onLoginResultChanged);
    }

    private TextInputEditText mIdView;
    private TextInputEditText mPasswordView;

    private LoginViewModel mViewModel;
    private ProgressDialog mProgressDialog;

    private void tryToLogin() {
        mViewModel.tryToLogin(mIdView.getText(), mPasswordView.getText());
    }

    private void onLoginResultChanged(LoginViewModel.Result result) {
        if (result == null) {
            return;
        }

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        switch (result.type) {
            case TYPE_ID:
                mIdView.setError(result.msg);
                break;
            case TYPE_PASSWORD:
                mPasswordView.setError(result.msg);
                break;
            case TYPE_DIALOG:
                if (mProgressDialog == null) {
                    mProgressDialog = createProgressDialog(getActivity());
                }
                mProgressDialog.setMessage(result.msg);
                mProgressDialog.show();
                break;
            case TYPE_TOAST:
                ToastUtils.getInstance(getContext()).show(result.msg);
                break;
            case TYPE_OK:
                Activity activity = getActivity();
                if (activity != null) {
                    activity.setResult(Activity.RESULT_OK);
                    activity.finish();
                }
                break;
        }
    }

    private static ProgressDialog createProgressDialog(Activity activity) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setCancelable(false);
        dialog.setTitle("请稍后");
        return dialog;
    }
}
