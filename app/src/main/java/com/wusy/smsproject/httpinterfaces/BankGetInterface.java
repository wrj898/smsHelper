package com.wusy.smsproject.httpinterfaces;

import com.wusy.smsproject.entity.CardInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BankGetInterface {
//
//    // https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=
//    @GET("/validateAndCacheCardInfo.json?_input_charset=utf-8&cardBinCheck=true&cardNo={cardNo}")
//    Call<CardInfo> validateAndCacheCardInfo(@Path("cardNo") String cardNo);


    @GET("/validateAndCacheCardInfo.json")
    Call<CardInfo> validateAndCacheCardInfo(@Query("_input_charset") String _input_charset,
                                            @Query("cardBinCheck") boolean cardBinCheck,
                                            @Query("cardNo") String cardNo);
}
