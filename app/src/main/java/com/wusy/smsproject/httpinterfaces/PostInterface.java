package com.wusy.smsproject.httpinterfaces;

import com.wusy.smsproject.entity.HttpResult;
import com.wusy.smsproject.entity.HttpResultOfBankList;

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

    @POST("list")
    Call<HttpResultOfBankList> getBankList(@Query("token") String token);


//    token string
//    name string 姓名
//    user_id string 用户id
//    code string 银行代码(CCB之类的)
//    note string 银行名字
//    app_id string 银行卡号
//      {code:1} 1.成功2.其他 注：成功后请求/list接口重新获取列表
    @POST("add")
    Call<HttpResult> addBankCard(@Query("token") String token, @Query("name") String name,
                                 @Query("user_id") String user_id, @Query("code") String code,
                                 @Query("note") String note, @Query("app_id") String app_id);


//    token string
//    id string 从服务器获取的银行卡id编号
//    name string
//    code string
//    note string
//    app_id string
//    {code:1} 1.成功 2.失败  注：成功后重新请求/list接口获取列表
    @POST("edit")
    Call<HttpResult> editBankCard(@Query("token") String token, @Query("id") String id,
                                 @Query("name") String name, @Query("code") String code,
                                 @Query("note") String note, @Query("app_id") String app_id);


    @POST("delete")
    Call<HttpResult> removeBankCard(@Query("token") String token, @Query("id") String id);


}