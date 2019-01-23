package com.wusy.smsproject;

import android.app.Application;
import android.content.Context;

import com.wusy.smsproject.entity.UserInfo;

public class BaseApplication extends Application {

    public static UserInfo curUser;
    private static Application curApplcation;

    @Override
    public void onCreate() {
        super.onCreate();
        curApplcation = this;
    }

    public static String getCurRealUserName(){
        if(curUser != null){
            return curUser.getUserName();
        }
        return null;
    }

    public static String getCurUserName(){
        if(curUser != null){
            // 原先用的是username做主键，现在修改成用id做主键
            return String.valueOf(curUser.getId());
        }
        return null;
    }

    public static String getCurUserMoney(){
        if(curUser != null){
            return curUser.getMoney();
        }
        return null;
    }

    public static String getCurUserRate(){
        if(curUser != null){
            return curUser.getRate();
        }
        return null;
    }

    public static String getCurUserToken(){
        if(curUser != null){
            return curUser.getToken();
        }
        return null;
    }


    public static Context getCurApplicationContext(){
        if(curApplcation != null){
            return curApplcation.getApplicationContext();
        }
        return null;
    }
}
