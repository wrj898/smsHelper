package com.wusy.smsproject.entity;

public class HttpResult {

//    code : 1, //1登陆成功 2.其他
////    token: “abcdefghskjfdlaj”
////    username:”abc”用户名
////    balance:100 余额
////    fees: 0.05 费率
//{
//    "code": 1,
//        "token": "0edfa8643a2c780644c6bee248e271e7",
//        "user_id": 3,
//        "user_name": "kk688",
//        "balance": 0,
//        "fees": 0.02
//}
    private int code;
    private String token;
    private int user_id;
    private String user_name;
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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
