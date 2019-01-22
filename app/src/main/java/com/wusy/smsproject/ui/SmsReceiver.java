package com.wusy.smsproject.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import com.wusy.smsproject.BaseApplication;
import com.wusy.smsproject.base.BaseParamas;
import com.wusy.smsproject.entity.BankCardEntity;
import com.wusy.smsproject.entity.LogEntity;
import com.wusy.smsproject.httpinterfaces.CallBackInterface;
import com.wusy.smsproject.utils.BankUtils;
import com.wusy.smsproject.utils.db.DatabaseUtils;

import java.util.HashMap;

public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private CallBackInterface callBack;

    public SmsReceiver() {

    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (SMS_RECEIVED.equals(intent.getAction())) {
            // 获取当前用户所有银行卡列表
            HashMap<String, BankCardEntity> bankCardMap = DatabaseUtils.getBankCardHashMap(context, BaseApplication.getCurUserName());
            if(bankCardMap.size() == 0){
                return;
            }

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if(pdus != null){
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    if (messages.length > 0) {
                        String content = messages[0].getMessageBody();
                        String sender = messages[0].getOriginatingAddress();
                        long msgDate = messages[0].getTimestampMillis();

//                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                        Date d = new Date(msgDate);
//                        String strDate = dateFormat.format(d);

                        BankCardEntity curBankCard = null;
                        if(!bankCardMap.containsKey(sender)){
                            return;
                        }
                        curBankCard = bankCardMap.get(sender);

                        LogEntity logEntity = new LogEntity();
                        logEntity.setBankName(curBankCard.getBankName());
                        logEntity.setCardNumber(curBankCard.getCardNumber());
                        logEntity.setUserKey(BaseApplication.getCurUserName());
                        logEntity.setTime(String.valueOf(msgDate));
                        logEntity.setMoney(BankUtils.getMoneyFromSMS(content));
                        logEntity.setState(BaseParamas.STATE_WITHOUT_UPLOAD);
                        DatabaseUtils.insertLog(BaseApplication.getCurApplicationContext(), logEntity);
                        // TODO 加入页面刷新
                    }
                }
            }
        }
    }



}
