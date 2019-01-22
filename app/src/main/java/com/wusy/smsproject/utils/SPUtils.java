package com.wusy.smsproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    public static final String KEY_TOKEN = "token";

    public static final String KEY_USER_NAME = "username";
    public static final String KEY_USER_PWD = "userpwd";

    public static final String KEY_ISREMBER = "isrember";

    public static void saveParam(Context context, String key, String value){
        SharedPreferences.Editor editor = context.getSharedPreferences("localsaver", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static String getStringParam(Context context, String key){
        SharedPreferences preferences= context.getSharedPreferences("localsaver", Context.MODE_PRIVATE);
        return preferences.getString(key,"");
    }



    public static void saveBooleanParam(Context context, String key, boolean value){
        SharedPreferences.Editor editor = context.getSharedPreferences("localsaver", Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    public static boolean getBooleanParam(Context context, String key){
        SharedPreferences preferences= context.getSharedPreferences("localsaver", Context.MODE_PRIVATE);
        return preferences.getBoolean(key,false);
    }


}
