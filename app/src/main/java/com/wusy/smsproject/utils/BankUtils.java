package com.wusy.smsproject.utils;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BankUtils {

    // 检测银行的接口
    //https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=银行卡卡号&cardBinCheck=true


    // 中国工商银行
    public static final String TEL_GONGSHANG = "95588";
    // 中国建设银行
    public static final String TEL_JIANSHE = "95533";
    // 招商银行
    public static final String TEL_ZHAOSHANG = "95555";
    // 中国农业银行
    public static final String TEL_NONGYE = "95599";
    // 中国民生银行
    public static final String TEL_MINSHENG= "95568";
    // 深圳发展银行
    public static final String TEL_SHENZHENFAZHAN = "95501";
    // 广东发展银行
    public static final String TEL_GUANGFA = "800-830-8003";
    // 兴业发展银行
    public static final String TEL_XINGYEFAZHAN = "95561";
    // 中国邮政储蓄银行
    public static final String TEL_YOUZHENGCHUXU = "95580";
    // 北京银行
    public static final String TEL_BEIJING = "95526";
    // 上海浦东发展银行
    public static final String TEL_PUFA = "95528";
    // 交通银行
    public static final String TEL_JIAOTONG = "95559";
    // 中国光大银行
    public static final String TEL_GUANGDA = "95595";
    // 平安银行
    public static final String TEL_PINGAN = "95511";
    // 中国银行
    public static final String TEL_ZHONGGUOYINHANG = "95566";


    public static String getTelFromBankName(String bankName){
        if(TextUtils.isEmpty(bankName)){
            return "000000";
        }
        if(bankName.contains("工商银行")){
            return TEL_GONGSHANG;
        }else if(bankName.contains("建设银行")){
            return TEL_JIANSHE;
        }else if(bankName.contains("招商银行")){
            return TEL_ZHAOSHANG;
        }else if(bankName.contains("农业银行")){
            return TEL_NONGYE;
        }else if(bankName.contains("民生银行")){
            return TEL_MINSHENG;
        }else if(bankName.contains("深圳发展银行")){
            return TEL_SHENZHENFAZHAN;
        }else if(bankName.contains("广东发展银行")){
            return TEL_GUANGFA;
        }else if(bankName.contains("兴业银行")){
            return TEL_XINGYEFAZHAN;
        }else if(bankName.contains("邮政")){
            return TEL_YOUZHENGCHUXU;
        }else if(bankName.contains("北京银行")){
            return TEL_BEIJING;
        }else if(bankName.contains("上海浦东发展银行")){
            return TEL_PUFA;
        }else if(bankName.contains("交通银行")){
            return TEL_JIAOTONG;
        }else if(bankName.contains("中国光大银行")){
            return TEL_GUANGDA;
        }else if(bankName.contains("平安银行")){
            return TEL_PINGAN;
        }else if(bankName.contains("中国银行")){
            return TEL_ZHONGGUOYINHANG;
        }else{
            return "000000";
        }
    }

    private static final String bankJson = "{\"SRCB\": \"深圳农村商业银行\", \"BGB\": \"广西北部湾银行\", \"SHRCB\": \"上海农村商业银行\", \"BJBANK\": \"北京银行\"," +
            "\"WHCCB\": \"威海市商业银行\", \"BOZK\": \"周口银行\", \"KORLABANK\": \"库尔勒市商业银行\", \"SPABANK\": \"平安银行\",\"SDEB\": \"顺德农商银行\", " +
            "\"HURCB\": \"湖北省农村信用社\", \"WRCB\": \"无锡农村商业银行\", \"BOCY\": \"朝阳银行\", \"CZBANK\": \"浙商银行\", \"HDBANK\": \"邯郸银行\", \"BOC\": \"中国银行\"," +
            "\"BOD\": \"东莞银行\", \"CCB\": \"中国建设银行\", \"ZYCBANK\": \"遵义市商业银行\",\"SXCB\": \"绍兴银行\", \"GZRCU\": \"贵州省农村信用社\",\"ZJKCCB\": \"张家口市商业银行\"," +
            "\"BOJZ\": \"锦州银行\",\"BOP\": \"平顶山银行\",\"HKB\": \"汉口银行\",\"SPDB\": \"上海浦东发展银行\",\"NXRCU\": \"宁夏黄河农村商业银行\",\"NYNB\": \"广东南粤银行\"," +
            "\"GRCB\": \"广州农商银行\",\"BOSZ\": \"苏州银行\",\"HZCB\": \"杭州银行\",\"HSBK\": \"衡水银行\", \"HBC\": \"湖北银行\", \"JXBANK\": \"嘉兴银行\", \"HRXJB\": \"华融湘江银行\"," +
            "\"BODD\": \"丹东银行\", \"AYCB\": \"安阳银行\", \"EGBANK\": \"恒丰银行\", \"CDB\": \"国家开发银行\", \"TCRCB\": \"江苏太仓农村商业银行\", \"NJCB\": \"南京银行\", " +
            "\"ZZBANK\": \"郑州银行\", \"DYCB\": \"德阳商业银行\",\"YBCCB\": \"宜宾市商业银行\",\"SCRCU\": \"四川省农村信用\",\"KLB\": \"昆仑银行\",\"LSBANK\": \"莱商银行\"," +
            "\"YDRCB\": \"尧都农商行\",\"CCQTGB\": \"重庆三峡银行\",\"FDB\": \"富滇银行\",\"JSRCU\": \"江苏省农村信用联合社\",\"JNBANK\": \"济宁银行\",\"CMB\": \"招商银行\"," +
            "\"JINCHB\": \"晋城银行JCBANK\",\"FXCB\": \"阜新银行\",\"WHRCB\": \"武汉农村商业银行\",\"HBYCBANK\": \"湖北银行宜昌分行\",\"TZCB\": \"台州银行\",\"TACCB\": \"泰安市商业银行\"," +
            "\"XCYH\": \"许昌银行\",\"CEB\": \"中国光大银行\",\"NXBANK\": \"宁夏银行\",\"HSBANK\": \"徽商银行\",\"JJBANK\": \"九江银行\",\"NHQS\": \"农信银清算中心\"," +
            "\"MTBANK\": \"浙江民泰商业银行\",\"LANGFB\": \"廊坊银行\",\"ASCB\": \"鞍山银行\",\"KSRB\": \"昆山农村商业银行\",\"YXCCB\": \"玉溪市商业银行\",\"DLB\": \"大连银行\"," +
            "\"DRCBCL\": \"东莞农村商业银行\",\"GCB\": \"广州银行\",\"NBBANK\": \"宁波银行\",\"BOYK\": \"营口银行\",\"SXRCCU\": \"陕西信合\",\"GLBANK\": \"桂林银行\"," +
            "\"BOQH\": \"青海银行\",\"CDRCB\": \"成都农商银行\",\"QDCCB\": \"青岛银行\",\"HKBEA\": \"东亚银行\",\"HBHSBANK\": \"湖北银行黄石分行\",\"WZCB\": \"温州银行\"," +
            "\"TRCB\": \"天津农商银行\",\"QLBANK\": \"齐鲁银行\",\"GDRCC\": \"广东省农村信用社联合社\",\"ZJTLCB\": \"浙江泰隆商业银行\",\"GZB\": \"赣州银行\",\"GYCB\": \"贵阳市商业银行\"," +
            "\"CQBANK\": \"重庆银行\",\"DAQINGB\": \"龙江银行\",\"CGNB\": \"南充市商业银行\",\"SCCB\": \"三门峡银行\",\"CSRCB\": \"常熟农村商业银行\",\"SHBANK\": \"上海银行\"," +
            "\"JLBANK\": \"吉林银行\",\"CZRCB\": \"常州农村信用联社\",\"BANKWF\": \"潍坊银行\",\"ZRCBANK\": \"张家港农村商业银行\",\"FJHXBC\": \"福建海峡银行\",\"ZJNX\": \"浙江省农村信用社联合社\"," +
            "\"LZYH\": \"兰州银行\",\"JSB\": \"晋商银行\",\"BOHAIB\": \"渤海银行\",\"CZCB\": \"浙江稠州商业银行\",\"YQCCB\": \"阳泉银行\",\"SJBANK\": \"盛京银行\",\"XABANK\": \"西安银行\"," +
            "\"BSB\": \"包商银行\",\"JSBANK\": \"江苏银行\",\"FSCB\": \"抚顺银行\",\"HNRCU\": \"河南省农村信用\",\"COMM\": \"交通银行\",\"XTB\": \"邢台银行\",\"CITIC\": \"中信银行\"," +
            "\"HXBANK\": \"华夏银行\",\"HNRCC\": \"湖南省农村信用社\",\"DYCCB\": \"东营市商业银行\",\"ORBANK\": \"鄂尔多斯银行\",\"BJRCB\": \"北京农村商业银行\",\"XYBANK\": \"信阳银行\"," +
            "\"ZGCCB\": \"自贡市商业银行\",\"CDCB\": \"成都银行\",\"HANABANK\": \"韩亚银行\",\"CMBC\": \"中国民生银行\",\"LYBANK\": \"洛阳银行\",\"GDB\": \"广东发展银行\"," +
            "\"ZBCB\": \"齐商银行\",\"CBKF\": \"开封市商业银行\",\"H3CB\": \"内蒙古银行\",\"CIB\": \"兴业银行\",\"CRCBANK\": \"重庆农村商业银行\",\"SZSBK\": \"石嘴山银行\"," +
            "\"DZBANK\": \"德州银行\", \"SRBANK\": \"上饶银行\", \"LSCCB\": \"乐山市商业银行\", \"JXRCU\": \"江西省农村信用\",\"ICBC\": \"中国工商银行\", \"JZBANK\": \"晋中市商业银行\"," +
            "\"HZCCB\": \"湖州市商业银行\", \"NHB\": \"南海农村信用联社\", \"XXBANK\": \"新乡银行\",\"JRCB\": \"江苏江阴农村商业银行\", \"YNRCC\": \"云南省农村信用社\", \"ABC\": \"中国农业银行\", " +
            "\"GXRCU\": \"广西省农村信用\", \"PSBC\": \"中国邮政储蓄银行\", \"BZMD\": \"驻马店银行\", \"ARCU\": \"安徽省农村信用社\", \"GSRCU\": \"甘肃省农村信用\", \"LYCB\": \"辽阳市商业银行\", " +
            "\"JLRCU\": \"吉林农信\", \"URMQCCB\": \"乌鲁木齐市商业银行\", \"XLBANK\": \"中山小榄村镇银行\", \"CSCB\": \"长沙银行\", \"JHBANK\": \"金华银行\",\"BHB\": \"河北银行\", " +
            "\"NBYZ\": \"鄞州银行\", \"LSBC\": \"临商银行\", \"BOCD\": \"承德银行\", \"SDRCU\": \"山东农信\", \"NCB\": \"南昌银行\", \"TCCB\": \"天津银行\", \"WJRCB\": \"吴江农商银行\", " +
            "\"CBBQS\": \"城市商业银行资金清算中心\", \"HBRCU\": \"河北省农村信用社\"}";

    public static JSONObject bankCodeJson;


    public static String getNameOfBank(String bankCode) {
        if(TextUtils.isEmpty(bankCode)){
            return "未知银行";
        }
        try {
            if(bankCodeJson == null){
                bankCodeJson = new JSONObject(bankJson);
            }
            if(bankCodeJson.has(bankCode)){
                return bankCodeJson.getString(bankCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "未知银行";

//        long longBin = Long.parseLong(bankCode);
//        int index = binarySearch(bankBin, longBin);
//
//        if(index==-1) {
//            return "未知银行";
//        }
//        return bankName[index];
    }

    //二分查找方法
    public static int binarySearch(long[] srcArray, long des){
        int low = 0;
        int high = srcArray.length-1;
        while(low <= high) {
            int middle = (low + high)/2;
            if(des == srcArray[middle]) {
                return middle;
            } else if(des <srcArray[middle]) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        return -1;
    }



    /*
      校验过程：
      1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
      2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，将个位十位数字相加，即将其减去9），再求和。
      3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
      */
    /**
     * 校验银行卡卡号
     */
    public static boolean checkBankCard(String bankCard) {
        if(bankCard.length() < 15 || bankCard.length() > 19) {
            return false;
        }
        char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
        if(bit == 'N'){
            return false;
        }
        return bankCard.charAt(bankCard.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     * @param nonCheckCodeBankCard
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeBankCard){
        if(nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
                || !nonCheckCodeBankCard.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeBankCard.trim().toCharArray();
        int luhmSum = 0;
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if(j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
    }


    public static String getMoneyFromSMS(String message){
        if(TextUtils.isEmpty(message)){
            return "0";
        }
        // 建行 + 招商 匹配规则
        String regStr1 = "人民币[0-9]*[.][0-9]{0,4}元";
        // 农业银行匹配规则
        String regStr2 = "人民币[0-9]*[.][0-9]{0,4}.*,";

        // 提取出里面的金额
        String regMoney = "[0-9]*[.][0-9]{0,4}";
        String result = getMoneyFromSMSWithReg(message,regStr1);
        if("0".equals(result)){
            result = getMoneyFromSMSWithReg(message,regStr2);
        }
        if(!"0".equals(result)){
            result = getMoneyFromSMSWithReg(result, regMoney);
        }
        return result;
    }

    public static String getMoneyFromSMSWithReg(String message, String reg){
        if(TextUtils.isEmpty(message) || TextUtils.isEmpty(reg)){
            return "0";
        }
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(message);
        // 查找字符串中是否有匹配正则表达式的字符/字符串
        boolean isMatch = matcher.find();
        if(isMatch) {
            return matcher.group();
        }
        return "0";
    }



    public static String formatTime(String timeStr){
        if(TextUtils.isEmpty(timeStr)){
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date(Long.parseLong(timeStr));
        return dateFormat.format(d);
    }
}
