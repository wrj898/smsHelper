package com.wusy.smsproject.entity;

public class HttpResult {

//    code : 1, //1登陆成功 2.其他
////    token: “abcdefghskjfdlaj”
////    username:”abc”用户名
////    balance:100 余额
////    fees: 0.05 费率
    private int code;
    private String token;
    private String username;
    private String balance;
    private String fees;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }
}
