package com.wusy.smsproject.httpinterfaces;

import com.wusy.smsproject.entity.LogEntity;

public interface CallBackInterface {

     void uploadTaskCallback(LogEntity logEntity, int resultCode);

}
