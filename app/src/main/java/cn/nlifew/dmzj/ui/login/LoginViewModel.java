package cn.nlifew.dmzj.ui.login;

import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.nlifew.xdmzj.bean.login.LoginBean;
import cn.nlifew.xdmzj.entity.Account;
import cn.nlifew.xdmzj.request.ILogin;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";

    static final class Result {
        static final int TYPE_OK        = 0;
        static final int TYPE_ID        = 1;
        static final int TYPE_PASSWORD  = 2;
        static final int TYPE_DIALOG    = 3;
        static final int TYPE_TOAST     = 4;

        @IntDef({TYPE_OK, TYPE_ID, TYPE_PASSWORD, TYPE_TOAST, TYPE_DIALOG})
        @Retention(RetentionPolicy.SOURCE)
        @interface Type {}

        @Type int type;
        String msg;

        Result(@Type int type, String msg) {
            this.type = type;
            this.msg = msg;
        }
    }

    public LoginViewModel() {

    }

    private MutableLiveData<Result> mLoginResult = new MutableLiveData<>(null);

    LiveData<Result> getLoginResult() {
        return mLoginResult;
    }

    void tryToLogin(CharSequence _id, CharSequence _password) {
        String id, password;

        // 1. 判断参数是否合法

        if (_id == null || (id = _id.toString().trim()).length() == 0) {
            mLoginResult.postValue(new Result(Result.TYPE_ID, "账号为空"));
            return;
        }
        if (_password == null || (password = _password.toString().trim()).length() == 0) {
            mLoginResult.postValue(new Result(Result.TYPE_PASSWORD, "密码为空"));
            return;
        }

        mLoginResult.postValue(new Result(Result.TYPE_DIALOG, "正在登录，请稍等..."));

        // 2. 网络请求

        ILogin request = NetworkUtils.create(ILogin.class);
        Call<LoginBean> call = request.loginByPassword(id, password);
        call.enqueue(new Callback<LoginBean>() {
            @Override
            public void onResponse(Call<LoginBean> call, Response<LoginBean> response) {
                LoginBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                // 成功时：{result: 1, msg:"OK", data:{...}}
                // 失败时：{result: 0, msg:"账号或密码错误"}
                if (bean.result != 1 || bean.data == null) {
                    mLoginResult.postValue(new Result(Result.TYPE_TOAST, bean.msg));
                    return;
                }
                Account.setInstance(makeAccount(bean.data));
                mLoginResult.postValue(new Result(Result.TYPE_TOAST, "登录成功，" + bean.data.nickname));
                mLoginResult.postValue(new Result(Result.TYPE_OK, ""));
            }

            private Account makeAccount(LoginBean.DataType data) {
                Account account = new Account();
                account.email = data.email;
                account.id = Integer.parseInt(data.uid);
                account.name = data.nickname;
                account.password = data.passwd;
                account.phone = data.bind_phone;
                account.photo = data.photo;
                account.token = data.dmzj_token;
                return account;
            }

            @Override
            public void onFailure(Call<LoginBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mLoginResult.postValue(new Result(Result.TYPE_TOAST, t.toString()));
            }
        });
    }


}
