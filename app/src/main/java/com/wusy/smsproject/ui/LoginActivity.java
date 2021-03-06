package com.wusy.smsproject.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wusy.smsproject.BaseApplication;
import com.wusy.smsproject.R;
import com.wusy.smsproject.base.BaseParamas;
import com.wusy.smsproject.entity.HttpResult;
import com.wusy.smsproject.entity.UserInfo;
import com.wusy.smsproject.httpinterfaces.PostInterface;
import com.wusy.smsproject.utils.SPUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private TextView btnLogin;
    private EditText etUserName;
    private EditText etPassword;

    // 短信权限请求码
    private final static int REQUEST_SMS_CODE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        checkSMSPerssion();
    }

    private void initViews() {

        btnLogin = findViewById(R.id.btn_login);
        etUserName = findViewById(R.id.et_login_name);
        etPassword = findViewById(R.id.et_login_psw);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                toMainActivity();
                String localToken = SPUtils.getStringParam(LoginActivity.this, SPUtils.KEY_TOKEN);
                if(TextUtils.isEmpty(localToken)){
                    // 没有token的话，就直接登录
                    loginAccout();
                }else{
                    verifyAccout(localToken);
                }
            }
        });
    }

    public void autoLogin(){
        String localToken = SPUtils.getStringParam(LoginActivity.this, SPUtils.KEY_TOKEN);
        if(!TextUtils.isEmpty(localToken)){
            verifyAccout(localToken);
        }
    }

    public void loginAccout() {
        String userName = etUserName.getText().toString();
        String userPwd = etPassword.getText().toString();

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userPwd)){
            Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseParamas.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        PostInterface request = retrofit.create(PostInterface.class);

        Call<HttpResult> call = request.login(userName,userPwd);

        call.enqueue(new Callback<HttpResult>() {

            //请求成功时回调
            @Override
            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
                if(response.body().getCode() == BaseParamas.REQUEST_SUCCESS){
                    SPUtils.saveParam(LoginActivity.this, SPUtils.KEY_TOKEN, response.body().getToken());
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserName(response.body().getUsername());
                    userInfo.setToken(response.body().getToken());
                    userInfo.setMoney(response.body().getBalance());
                    userInfo.setRate(response.body().getFees());
                    toMainActivity(userInfo);
                }else{
                    Toast.makeText(LoginActivity.this, "请求失败1", Toast.LENGTH_SHORT).show();
                }

            }

            //请求失败时回调
            @Override
            public void onFailure(Call<HttpResult> call, Throwable throwable) {
                Toast.makeText(LoginActivity.this, "请求失败2=" + throwable.getMessage() , Toast.LENGTH_SHORT).show();
                System.out.println("请求失败");
                System.out.println(throwable.getMessage());
            }
        });
    }


    public void verifyAccout(String token){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseParamas.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        PostInterface request = retrofit.create(PostInterface.class);
        Call<HttpResult> call = request.verify(token);
        call.enqueue(new Callback<HttpResult>() {

            @Override
            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
                if(response.body().getCode() == BaseParamas.REQUEST_SUCCESS){
                    SPUtils.saveParam(LoginActivity.this, SPUtils.KEY_TOKEN, response.body().getToken());
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserName(response.body().getUsername());
                    userInfo.setToken(response.body().getToken());
                    userInfo.setMoney(response.body().getBalance());
                    userInfo.setRate(response.body().getFees());
                    toMainActivity(userInfo);
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<HttpResult> call, Throwable throwable) {
                System.out.println("请求失败");
                System.out.println(throwable.getMessage());
            }
        });
    }


    private void toMainActivity(UserInfo userInfo){
        BaseApplication.curUser = userInfo;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 检查是否有短信权限
     */
    private void checkSMSPerssion() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_SMS}, REQUEST_SMS_CODE);
            } else {
                autoLogin();
            }
        } else {
            autoLogin();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                autoLogin();
            } else {
                // 如果用户拒绝权限，直接关闭应用
                // TODO 或者有其他需求可再此处更改
                Toast.makeText(LoginActivity.this, "请在设置中打开短信权限", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
