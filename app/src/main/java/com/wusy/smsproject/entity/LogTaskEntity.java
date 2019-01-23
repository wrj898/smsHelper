package com.wusy.smsproject.entity;

import android.util.Log;

import com.wusy.smsproject.BaseApplication;
import com.wusy.smsproject.base.BaseParamas;
import com.wusy.smsproject.httpinterfaces.CallBackInterface;
import com.wusy.smsproject.httpinterfaces.PostInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogTaskEntity {

    private LogEntity logEntity;
    private Call<HttpResult> call;
    private CallBackInterface callBack;

    public LogTaskEntity(LogEntity logEntity){
        this.logEntity = logEntity;
    }

    public void startTask(){
        Log.e("wusy","startTask");
        if(logEntity == null){
            Log.e("wusy","logEntity == null");
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseParamas.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        PostInterface request = retrofit.create(PostInterface.class);
        Log.e("wusy","retrofit.creat");
        call = request.submit(BaseApplication.getCurUserToken(), logEntity.getCardNumber(),logEntity.getMoney(), logEntity.getTimeStr());
        call.enqueue(new Callback<HttpResult>() {

            @Override
            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
                Log.e("wusy","onResponse ");
                if(response.body() != null){
                    callback(logEntity, response.body().getCode());
                }else{
                    callback(logEntity, BaseParamas.REQUEST_OTHER);
                }

            }

            //请求失败时回调
            @Override
            public void onFailure(Call<HttpResult> call, Throwable throwable) {
                System.out.println("请求失败");
                Log.e("wusy","请求失败.creat");
                System.out.println(throwable.getMessage());
                callback(logEntity, BaseParamas.REQUEST_OTHER);
            }
        });
    }

    public void cancelTask(){
        if(call != null){
            call.cancel();
            call = null;
            callBack = null;
        }
    }

    public void setCallback(CallBackInterface callBack){
        this.callBack = callBack;
    }

    private void callback(LogEntity logEntity, int resultCode){
        call = null;
        Log.e("wusy","callback ");
        if(callBack != null){
            Log.e("wusy","callBack != null ");
            callBack.uploadTaskCallback(logEntity, resultCode);
        }
    }
}
