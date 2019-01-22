package com.wusy.smsproject.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wusy.smsproject.entity.BankCardEntity;
import com.wusy.smsproject.entity.LogEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseUtils {

    public static final String DATABASE_BANKCARD = "bacnkcard.db";

    public static void updateLog(Context context, LogEntity logEntity){
        SQLiteDatabase database = getSQLiteDatabase(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put("bankcode", logEntity.getBankCode());
        contentValues.put("bankname", logEntity.getBankName());
        contentValues.put("money", logEntity.getMoney());
        contentValues.put("time", logEntity.getTime());
        contentValues.put("cardnumber", logEntity.getCardNumber());
        contentValues.put("userkey", logEntity.getUserKey());
        contentValues.put("state", logEntity.getState());
        database.update(MySqliteHelper.TABLE_LOG, contentValues,"time=?", new String[]{logEntity.getTime()});
        database.close();
    }

    public static void insertLog(Context context, LogEntity logEntity){
        SQLiteDatabase database = getSQLiteDatabase(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put("bankcode", logEntity.getBankCode());
        contentValues.put("bankname", logEntity.getBankName());
        contentValues.put("money", logEntity.getMoney());
        contentValues.put("time", logEntity.getTime());
        contentValues.put("cardnumber", logEntity.getCardNumber());
        contentValues.put("userkey", logEntity.getUserKey());
        contentValues.put("state", logEntity.getState());

        database.insert(MySqliteHelper.TABLE_LOG, null,contentValues);
        database.close();
    }

    public static List<LogEntity> getLogList(Context context, String userKey){
        SQLiteDatabase database = getSQLiteDatabase(context);

        Cursor cursor = database.query(MySqliteHelper.TABLE_LOG, new String[]{"bankcode","bankname","money","time","cardnumber","userkey","state"},
                "userkey=?", new String[]{userKey}, null, null, "time DESC");

        int bankcodeIndex = cursor.getColumnIndex("bankcode");
        int banknameIndex = cursor.getColumnIndex("bankname");
        int moneyIndex = cursor.getColumnIndex("money");
        int timeIndex = cursor.getColumnIndex("time");
        int cardnumberIndex = cursor.getColumnIndex("cardnumber");
        int userkeyIndex = cursor.getColumnIndex("userkey");
        int stateIndex = cursor.getColumnIndex("state");

        List<LogEntity> resultList = new ArrayList<>();

        while(cursor.moveToNext()){
            LogEntity logEntity = new LogEntity();
            logEntity.setBankCode(cursor.getString(bankcodeIndex));
            logEntity.setBankName(cursor.getString(banknameIndex));
            logEntity.setMoney(cursor.getString(moneyIndex));
            logEntity.setTime(cursor.getString(timeIndex));
            logEntity.setCardNumber(cursor.getString(cardnumberIndex));
            logEntity.setUserKey(cursor.getString(userkeyIndex));
            logEntity.setState(cursor.getInt(stateIndex));
            resultList.add(logEntity);
        }

        database.close();

        return resultList;
    }


    public static List<LogEntity> getLogListWithState(Context context, String userKey, String state){
        SQLiteDatabase database = getSQLiteDatabase(context);

        Cursor cursor = database.query(MySqliteHelper.TABLE_LOG, new String[]{"bankcode","bankname","money","time","cardnumber","userkey","state"},
                "userkey=? AND state=?", new String[]{userKey, state}, null, null, null);

        int bankcodeIndex = cursor.getColumnIndex("bankcode");
        int banknameIndex = cursor.getColumnIndex("bankname");
        int moneyIndex = cursor.getColumnIndex("money");
        int timeIndex = cursor.getColumnIndex("time");
        int cardnumberIndex = cursor.getColumnIndex("cardnumber");
        int userkeyIndex = cursor.getColumnIndex("userkey");
        int stateIndex = cursor.getColumnIndex("state");

        List<LogEntity> resultList = new ArrayList<>();

        while(cursor.moveToNext()){
            LogEntity logEntity = new LogEntity();
            logEntity.setBankCode(cursor.getString(bankcodeIndex));
            logEntity.setBankName(cursor.getString(banknameIndex));
            logEntity.setMoney(cursor.getString(moneyIndex));
            logEntity.setTime(cursor.getString(timeIndex));
            logEntity.setCardNumber(cursor.getString(cardnumberIndex));
            logEntity.setUserKey(cursor.getString(userkeyIndex));
            logEntity.setState(cursor.getInt(stateIndex));
            resultList.add(logEntity);
        }

        database.close();

        return resultList;
    }




    public static void insertBankCard(Context context, BankCardEntity bankCardEntity){
        SQLiteDatabase database = getSQLiteDatabase(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put("bankcode", bankCardEntity.getBankCode());
        contentValues.put("bankname", bankCardEntity.getBankName());
        contentValues.put("cardnumber", bankCardEntity.getCardNumber());
        contentValues.put("userkey", bankCardEntity.getUserKey());

        database.insert(MySqliteHelper.TABLE_BANKCARD, null,contentValues);

        database.close();
    }

    public static boolean hasBankCard(Context context, String userKey, String bankName){
        SQLiteDatabase database = getSQLiteDatabase(context);

        Cursor cursor = database.query(MySqliteHelper.TABLE_BANKCARD, new String[]{"bankcode"},
                "userkey=? AND bankName=?", new String[]{userKey, bankName}, null, null, null);

        return cursor.getCount() > 0;
    }


    public static List<BankCardEntity> getBankCardList(Context context, String userKey){
        SQLiteDatabase database = getSQLiteDatabase(context);

        Cursor cursor = database.query(MySqliteHelper.TABLE_BANKCARD, new String[]{"bankcode","bankname","cardnumber","userkey"},
                "userkey=?", new String[]{userKey}, null, null, null);

        int bankcodeIndex = cursor.getColumnIndex("bankcode");
        int banknameIndex = cursor.getColumnIndex("bankname");
        int cardnumberIndex = cursor.getColumnIndex("cardnumber");
        int userkeyIndex = cursor.getColumnIndex("userkey");

        List<BankCardEntity> resultList = new ArrayList<>();

        while(cursor.moveToNext()){
            BankCardEntity bankCardEntity = new BankCardEntity();
            bankCardEntity.setBankCode(cursor.getString(bankcodeIndex));
            bankCardEntity.setBankName(cursor.getString(banknameIndex));
            bankCardEntity.setCardNumber(cursor.getString(cardnumberIndex));
            bankCardEntity.setUserKey(cursor.getString(userkeyIndex));
            resultList.add(bankCardEntity);
        }

        database.close();

        return resultList;
    }

    public static HashMap<String, BankCardEntity> getBankCardHashMap(Context context, String userKey){
        SQLiteDatabase database = getSQLiteDatabase(context);

        Cursor cursor = database.query(MySqliteHelper.TABLE_BANKCARD, new String[]{"bankcode","bankname","cardnumber","userkey"},
                "userkey=?", new String[]{userKey}, null, null, null);

        int bankcodeIndex = cursor.getColumnIndex("bankcode");
        int banknameIndex = cursor.getColumnIndex("bankname");
        int cardnumberIndex = cursor.getColumnIndex("cardnumber");
        int userkeyIndex = cursor.getColumnIndex("userkey");

        HashMap<String, BankCardEntity> resultMap = new HashMap<>();

        while(cursor.moveToNext()){
            BankCardEntity bankCardEntity = new BankCardEntity();
            bankCardEntity.setBankCode(cursor.getString(bankcodeIndex));
            bankCardEntity.setBankName(cursor.getString(banknameIndex));
            bankCardEntity.setCardNumber(cursor.getString(cardnumberIndex));
            bankCardEntity.setUserKey(cursor.getString(userkeyIndex));
            resultMap.put(bankCardEntity.getBankCode(), bankCardEntity);
        }

        database.close();

        return resultMap;
    }


    public static SQLiteDatabase getSQLiteDatabase(Context context){
        MySqliteHelper helper = new MySqliteHelper(context,DATABASE_BANKCARD, null, 1);
        return helper.getWritableDatabase();
    }
}
