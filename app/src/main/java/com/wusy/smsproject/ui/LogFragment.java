package com.wusy.smsproject.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wusy.smsproject.BaseApplication;
import com.wusy.smsproject.R;
import com.wusy.smsproject.adapter.LogInfoAdapter;
import com.wusy.smsproject.base.BaseParamas;
import com.wusy.smsproject.entity.BankCardEntity;
import com.wusy.smsproject.entity.LogEntity;
import com.wusy.smsproject.utils.BankUtils;
import com.wusy.smsproject.utils.db.DatabaseUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class LogFragment extends Fragment{

    private ListView logList;
    private List<LogEntity> list;
    private LogInfoAdapter logInfoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loglist, container, false);
        logList = view.findViewById(R.id.loglist);
        list = DatabaseUtils.getLogList(getContext(), BaseApplication.getCurUserName());
        logInfoAdapter = new LogInfoAdapter(getContext(), list);
        logList.setAdapter(logInfoAdapter);
//        getSmsInPhone();
        return view;
    }



    public void refreashPage(){
        if(list != null){
            list.clear();
            list.addAll(DatabaseUtils.getLogList(getContext(), BaseApplication.getCurUserName()));
            logInfoAdapter.notifyDataSetChanged();
        }
    }



    public void getSmsInPhone() {

        final String SMS_URI_ALL = "content://sms/"; // 所有短信
        final String SMS_URI_INBOX = "content://sms/inbox"; // 收件箱
        final String SMS_URI_SEND = "content://sms/sent"; // 已发送
        final String SMS_URI_DRAFT = "content://sms/draft"; // 草稿
        final String SMS_URI_OUTBOX = "content://sms/outbox"; // 发件箱
        final String SMS_URI_FAILED = "content://sms/failed"; // 发送失败
        final String SMS_URI_QUEUED = "content://sms/queued"; // 待发送列表


        try {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[] { "_id", "address", "person","body", "date", "type"};
            if(getContext()== null){
                return;
            }

            Cursor cur = getContext().getContentResolver().query(uri, projection, null,
                    null, "date desc"); // 获取手机内部短信
            // 获取短信中最新的未读短信
            // Cursor cur = getContentResolver().query(uri, projection,
            // "read = ?", new String[]{"0"}, "date desc");

            // 获取当前用户所有银行卡列表
            HashMap<String, BankCardEntity> bankCardMap = DatabaseUtils.getBankCardHashMap(getContext(),BaseApplication.getCurUserName());
            if(bankCardMap.size() == 0){
                return;
            }

            if (cur != null && cur.moveToFirst()) {
                // 发信人
                int index_Address = cur.getColumnIndex("address");
                // 发信人姓名
                int index_Person = cur.getColumnIndex("person");
                // 短信内容
                int index_Body = cur.getColumnIndex("body");
                // 短信时间
                int index_Date = cur.getColumnIndex("date");
                // 短信类型 0:所以短信  1:"接收" 2:"发送"  3:"草稿"  4:"发件箱"  5:"发送失败" 6:"待发送列表"
                int index_Type = cur.getColumnIndex("type");

                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int intType = cur.getInt(index_Type);

//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                    Date d = new Date(longDate);
//                    String strDate = dateFormat.format(d);

                    BankCardEntity curBankCard = null;
                    if(!bankCardMap.containsKey(strAddress)){
                        continue;
                    }
                    curBankCard = bankCardMap.get(strAddress);

                    LogEntity logEntity = new LogEntity();
                    logEntity.setBankName(curBankCard.getBankName());
                    logEntity.setCardNumber(curBankCard.getCardNumber());
                    logEntity.setUserKey(BaseApplication.getCurUserName());
                    logEntity.setTime(String.valueOf(longDate));
                    logEntity.setMoney(BankUtils.getMoneyFromSMS(strbody));
                    logEntity.setState(BaseParamas.STATE_WITHOUT_UPLOAD);
                    DatabaseUtils.insertLog(getContext(), logEntity);
                } while (cur.moveToNext());

                refreashPage();

                if (!cur.isClosed()) {
                    cur.close();
                }
            } else {
                Log.e("LogFragment", "没有短信");
            }
            Log.d("LogFragment", "读取短信结束");
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
    }
}
