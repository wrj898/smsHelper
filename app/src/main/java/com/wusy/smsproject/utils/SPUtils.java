package com.wusy.smsproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    public static final String KEY_TOKEN = "token";

    public static void saveParam(Context context, String key, String value){
        SharedPreferences.Editor editor = context.getSharedPreferences("localsaver", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static String getStringParam(Context context, String key){
        SharedPreferences preferences= context.getSharedPreferences("localsaver", Context.MODE_PRIVATE);
        return preferences.getString(key,"");
    }



}
