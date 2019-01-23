package com.wusy.smsproject.entity;

public class NewBankCardEntity {

//    "id": 61,
//            "user_id": 3,
//            "app_id": "6214830311652589",
//            "token": "",
//            "status": 2,
//            "current_money": 0,
//            "max_money": 50000,
//            "yesterday_money": 42782,
//            "success_rate": 0,
//            "weight": 4,
//            "types": 4,
//            "name": "罗刚",
//            "code": "CMB",
//            "note": "招商银行"
    private String id;
    private String user_id;
    private String app_id;
    private String token;
    private String status;
    private String current_money;
    private String max_money;
    private String yesterday_money;
    private String success_rate;
    private String weight;
    private String types;
    private String name;
    private String code;
    private String note;

    // 本地变量，是否被锁定监听
    private boolean isLocked = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrent_money() {
        return current_money;
    }

    public void setCurrent_money(String current_money) {
        this.current_money = current_money;
    }

    public String getMax_money() {
        return max_money;
    }

    public void setMax_money(String max_money) {
        this.max_money = max_money;
    }

    public String getYesterday_money() {
        return yesterday_money;
    }

    public void setYesterday_money(String yesterday_money) {
        this.yesterday_money = yesterday_money;
    }

    public String getSuccess_rate() {
        return success_rate;
    }

    public void setSuccess_rate(String success_rate) {
        this.success_rate = success_rate;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
