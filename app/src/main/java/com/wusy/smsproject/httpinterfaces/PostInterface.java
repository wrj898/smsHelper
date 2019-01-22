package com.wusy.smsproject.httpinterfaces;

import com.wusy.smsproject.entity.HttpResult;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostInterface {

    @POST("verify")
    Call<HttpResult> verify(@Query("token") String token);

    @POST("login")
    Call<HttpResult> login(@Query("username") String username, @Query("password") String password);

    @POST("submit")
    Call<HttpResult> submit(@Query("token") String token, @Query("bankcard") String bankcard,
                            @Query("amount") String amount, @Query("date") String date);


}