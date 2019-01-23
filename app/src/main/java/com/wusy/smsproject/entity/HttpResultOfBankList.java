package com.wusy.smsproject.entity;

import java.util.List;

public class HttpResultOfBankList {

    private int code;
    private List<NewBankCardEntity> cardList;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<NewBankCardEntity> getCardList() {
        return cardList;
    }

    public void setCardList(List<NewBankCardEntity> cardList) {
        this.cardList = cardList;
    }
}
