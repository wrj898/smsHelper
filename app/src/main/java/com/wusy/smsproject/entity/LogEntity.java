package com.wusy.smsproject.entity;

import com.wusy.smsproject.base.BaseParamas;
import com.wusy.smsproject.utils.BankUtils;

public class LogEntity {

    private String bankCode;
    private String bankName;
    private String money;
    private String time;
    private String cardNumber;
    private int state;
    private String userKey;

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTime() {
        return time;
    }

    public String getTimeStr(){
        return BankUtils.formatTime(time);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateStr() {
        if(state == BaseParamas.STATE_UPLOAD){
            return "已上传";
        }else if(state == BaseParamas.STATE_UPLOAD_FAILED){
            return "上传失败";
        }else{
            return "未上传";
        }
    }
}
