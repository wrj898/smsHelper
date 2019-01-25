package com.wusy.smsproject.httpinterfaces;

import com.wusy.smsproject.entity.HttpResult;
import com.wusy.smsproject.entity.HttpResultOfBankList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostInterface {

    @FormUrlEncoded
    @POST("verify")
    Call<HttpResult> verify(@Field("token") String token);

    @FormUrlEncoded
    @POST("login")
    Call<HttpResult> login(@Field("user_name") String username, @Field("pwd") String password);

    @FormUrlEncoded
    @POST("submit")
    Call<HttpResult> submit(@Field("token") String token, @Field("bankCard") String bankcard,
                            @Field("amount") String amount, @Field("date") String date);

    @FormUrlEncoded
    @POST("list")
    Call<HttpResultOfBankList> getBankList(@Field("token") String token);


//    token string
//    name string 姓名
//    user_id string 用户id
//    code string 银行代码(CCB之类的)
//    note string 银行名字
//    app_id string 银行卡号
//      {code:1} 1.成功2.其他 注：成功后请求/list接口重新获取列表

    @FormUrlEncoded
    @POST("add")
    Call<HttpResult> addBankCard(@Field("token") String token, @Field("name") String name,
                                 @Field("user_id") String user_id, @Field("code") String code,
                                 @Field("note") String note, @Field("app_id") String app_id);


//    token string
//    id string 从服务器获取的银行卡id编号
//    name string
//    code string
//    note string
//    app_id string
//    {code:1} 1.成功 2.失败  注：成功后重新请求/list接口获取列表
    @FormUrlEncoded
    @POST("edit")
    Call<HttpResult> editBankCard(@Field("token") String token, @Field("id") String id,
                                 @Field("name") String name, @Field("code") String code,
                                 @Field("note") String note, @Field("app_id") String app_id);

    @FormUrlEncoded
    @POST("delete")
    Call<HttpResult> removeBankCard(@Field("token") String token, @Field("id") String id);


}