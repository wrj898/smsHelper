package com.wusy.smsproject.entity;

import java.util.List;

public class HttpResultOfBankList {

    private int code;
    private List<NewBankCardEntity> cardlist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<NewBankCardEntity> getCardlist() {
        return cardlist;
    }

    public void setCardlist(List<NewBankCardEntity> cardlist) {
        this.cardlist = cardlist;
    }
}
