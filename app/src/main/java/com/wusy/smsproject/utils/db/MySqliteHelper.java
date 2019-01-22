package com.wusy.smsproject.utils.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySqliteHelper extends SQLiteOpenHelper {

    public static final String TAG = "MySqliteHelper";

    public static final String TABLE_LOG = "banklog";
    public static final String TABLE_BANKCARD = "bankcardinfo";


    public MySqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version) {
        super(context, name, factory, version);
    }

    @Override

    public void onOpen(SQLiteDatabase db) {
        Log.i(TAG,"open db");
        super.onOpen(db);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG,"create db");
        String createLogTable = "create table if not exists " + TABLE_LOG
                + "(id integer primary key AUTOINCREMENT,"
                + "bankcode varchar(20),"
                + "bankname varchar(40),"
                + "money varchar(20),"
                + "time varchar(30),"
                + "cardnumber varchar(30),"
                + "userkey varchar(40),"
                + "state integer)";
        db.execSQL(createLogTable);

        String createBankCardTable = "create table if not exists " + TABLE_BANKCARD
                + "(id integer primary key AUTOINCREMENT,"
                + "bankcode varchar(20),"
                + "bankname varchar(40),"
                + "userkey varchar(40),"
                + "cardnumber varchar(30))";

        db.execSQL(createBankCardTable);

        Log.i(TAG,"after excSql");
    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

}
