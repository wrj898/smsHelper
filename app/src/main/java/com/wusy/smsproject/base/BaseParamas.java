package com.wusy.smsproject.base;

public class BaseParamas {

//    public static final String BASE_URL = "https://easy-mock.com/mock/5c4480dafae54e04a50490d1/example/";
    public static final String BASE_URL = "http://api.jfpay.org/auto/";
    public static final String ALIPAY_CCDCAPI = "https://ccdcapi.alipay.com";


    // 请求结果码
    public static final int REQUEST_SUCCESS = 1;
    public static final int REQUEST_OTHER = 2;
    public static final int REQUEST_TOKEN_USELESS = 3;

    // 日志已上传
    public static final int STATE_UPLOAD = 1;
    // 日志未上传
    public static final int STATE_WITHOUT_UPLOAD = -1;
    // 日志上传失败
    public static final int STATE_UPLOAD_FAILED = -2;

}
