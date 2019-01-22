package com.wusy.smsproject.entity;

public class CardInfo {

    // {"cardType":"DC","bank":"CCB","key":"6217001960028895283","messages":[],"validated":true,"stat":"ok"}

    private String cardType;
    private String bank;
    private String key;
    private String validated;
    private String stat;

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValidated() {
        return validated;
    }

    public void setValidated(String validated) {
        this.validated = validated;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }
}
