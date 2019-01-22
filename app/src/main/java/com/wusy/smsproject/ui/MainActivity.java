package com.wusy.smsproject.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.wusy.smsproject.BaseApplication;
import com.wusy.smsproject.R;
import com.wusy.smsproject.base.BaseParamas;
import com.wusy.smsproject.entity.BankCardEntity;
import com.wusy.smsproject.entity.HttpResult;
import com.wusy.smsproject.entity.LogEntity;
import com.wusy.smsproject.entity.LogTaskEntity;
import com.wusy.smsproject.entity.UserInfo;
import com.wusy.smsproject.httpinterfaces.CallBackInterface;
import com.wusy.smsproject.httpinterfaces.PostInterface;
import com.wusy.smsproject.utils.BankUtils;
import com.wusy.smsproject.utils.SPUtils;
import com.wusy.smsproject.utils.db.DatabaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends FragmentActivity {

    private int lastShowFragment = 0;

    // 短信权限请求码
    private final static int REQUEST_SMS_CODE = 300;

    private LogFragment logFragment;
    private SettingFragment settingFragment;
    private MineFragment mineFragment;
    private Fragment[] fragments;

    private SmsReceiver mReceiver;
    private UploadReceiver mUploadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        checkSMSPerssion();

    }




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_log:
                    if (lastShowFragment != 0) {
                        switchFrament(lastShowFragment, 0);
                    }
                    return true;
                case R.id.navigation_setting:
                    if (lastShowFragment != 1) {
                        switchFrament(lastShowFragment, 1);
                    }
                    return true;
                case R.id.navigation_mine:
                    if (lastShowFragment != 2) {
                        switchFrament(lastShowFragment, 2);
                    }
                    return true;
            }
            return false;
        }
    };

    public void switchFrament(int lastIndex, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastIndex]);
        if (!fragments[index].isAdded()) {
            transaction.add(R.id.main_content, fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
        lastShowFragment = index;
    }



    private void initFragments() {
        logFragment = new LogFragment();
        settingFragment = new SettingFragment();
        mineFragment = new MineFragment();

        fragments = new Fragment[]{logFragment, settingFragment, mineFragment};
        lastShowFragment = 0;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_content, logFragment)
                .show(logFragment)
                .commit();

        IntentFilter iFilter = null; // 意图过滤对象
        mReceiver = new SmsReceiver(); // 广播接收类初始化
        iFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED"); // 意图过滤初始化
        iFilter.setPriority(Integer.MAX_VALUE); // 设置优先级
        registerReceiver(mReceiver, iFilter); // 注册广播接


        IntentFilter iFilter2 = null;
        mUploadReceiver = new UploadReceiver();
        iFilter2 = new IntentFilter(UploadReceiver.ALARM_WAKE_ACTION);
        iFilter2.setPriority(Integer.MAX_VALUE);
        registerReceiver(mUploadReceiver, iFilter2);


        startRepeatingTask();
        startRepeatingVerify();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
        }
        if(mUploadReceiver != null){
            unregisterReceiver(mUploadReceiver);
        }
        // 清除掉所有上传任务
        cancelAllTask();

        Intent alarmIntent = new Intent();
        alarmIntent.setAction(UploadReceiver.ALARM_WAKE_ACTION);
        PendingIntent operation = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent2 = new Intent();
        alarmIntent2.setAction(VerifyReceiver.ALARM_WAKE_ACTION_VERIFY);
        PendingIntent operation2 = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(alarmManager != null){
            // 取消掉定时任务
            alarmManager.cancel(operation);
            alarmManager.cancel(operation2);
        }
    }

    /**
     * 检查是否有短信权限
     */
    private void checkSMSPerssion() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, REQUEST_SMS_CODE);
            } else {
                initFragments();
            }
        } else {
            initFragments();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initFragments();
            } else {
                // 如果用户拒绝权限，直接关闭应用
                // TODO 或者有其他需求可再此处更改
                Toast.makeText(MainActivity.this, "请在设置中打开短信权限", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }




    public class SmsReceiver extends BroadcastReceiver {

        public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

        public SmsReceiver() {

        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            if (SMS_RECEIVED.equals(intent.getAction())) {
                // 获取当前用户所有银行卡列表
                HashMap<String, BankCardEntity> bankCardMap = DatabaseUtils.getBankCardHashMap(context, BaseApplication.getCurUserName());
                if(bankCardMap.size() == 0){
                    return;
                }

                Bundle bundle = intent.getExtras();
                if(bundle == null){
                    return;
                }

                Object[] pdus = (Object[]) bundle.get("pdus");
                if(pdus == null){
                    return;
                }
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length > 0) {
                    String content = messages[0].getMessageBody();
                    String sender = messages[0].getOriginatingAddress();
                    long msgDate = messages[0].getTimestampMillis();

//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                    Date d = new Date(msgDate);
//                    String strDate = dateFormat.format(d);

                    BankCardEntity curBankCard = null;
                    if(!bankCardMap.containsKey(sender)){
                        return;
                    }
                    curBankCard = bankCardMap.get(sender);

                    LogEntity logEntity = new LogEntity();
                    logEntity.setBankName(curBankCard.getBankName());
                    logEntity.setCardNumber(curBankCard.getCardNumber());
                    logEntity.setUserKey(BaseApplication.getCurUserName());
                    logEntity.setTime(String.valueOf(msgDate));
                    logEntity.setMoney(BankUtils.getMoneyFromSMS(content));
                    logEntity.setState(BaseParamas.STATE_WITHOUT_UPLOAD);
                    DatabaseUtils.insertLog(BaseApplication.getCurApplicationContext(), logEntity);
                    // TODO 加入页面刷新
                    if(logFragment != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logFragment.refreashPage();
                            }
                        });
                    }
                    LogTaskEntity logTaskEntity = new LogTaskEntity(logEntity);
                    logTaskEntity.setCallback(new CallBackInterface() {
                        @Override
                        public void uploadTaskCallback(LogEntity logEntity, int resultCode) {
                            Log.e("wusy","resultCode = " + resultCode);
                            if(resultCode == BaseParamas.REQUEST_SUCCESS){
                                logEntity.setState(BaseParamas.STATE_UPLOAD);
                                DatabaseUtils.updateLog(BaseApplication.getCurApplicationContext(), logEntity);
                            }else if(resultCode == BaseParamas.REQUEST_TOKEN_USELESS){
                                logEntity.setState(BaseParamas.STATE_UPLOAD_FAILED);
                                DatabaseUtils.updateLog(BaseApplication.getCurApplicationContext(), logEntity);
                                cancelAllTask();
                                toLoginActivity(true);
                            }else{
                                logEntity.setState(BaseParamas.STATE_UPLOAD_FAILED);
                                DatabaseUtils.updateLog(BaseApplication.getCurApplicationContext(), logEntity);
                            }
                            if(logFragment != null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        logFragment.refreashPage();
                                    }
                                });
                            }
                        }
                    });
                    logTaskEntity.startTask();
                }
            }
        }
    }


//    public class UploadService extends Service{
//
//        @Nullable
//        @Override
//        public IBinder onBind(Intent intent) {
//            return null;
//        }
//
//        @Override
//        public int onStartCommand(Intent intent, int flags, int startId) {
//            Intent alarmIntent = new Intent();
//            alarmIntent.setAction(UploadReceiver.ALARM_WAKE_ACTION);
//            PendingIntent operation = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            alarmManager.cancel(operation);
//            Calendar calendar = Calendar.getInstance();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), operation);
//            } else {
//                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), operation);
//            }
//            return super.onStartCommand(intent, flags, startId);
//        }
//    }

    private void startRepeatingTask(){
        Intent alarmIntent = new Intent();
        alarmIntent.setAction(UploadReceiver.ALARM_WAKE_ACTION);
        PendingIntent operation = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(alarmManager != null){
            alarmManager.cancel(operation);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, operation);
        }
    }

    private void startRepeatingVerify(){
        Intent alarmIntent = new Intent();
        alarmIntent.setAction(VerifyReceiver.ALARM_WAKE_ACTION_VERIFY);
        PendingIntent operation = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(alarmManager != null){
            alarmManager.cancel(operation);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 60 * 1000, operation);
        }
    }

    public class VerifyReceiver extends BroadcastReceiver {

        public static final String ALARM_WAKE_ACTION_VERIFY = "wusy.verifytask.ALARM_WAKE_ACTION";

        public VerifyReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            verifyAccout();
        }
    }

    // 接受任务提醒
    public class UploadReceiver extends BroadcastReceiver {

        public static final String ALARM_WAKE_ACTION = "wusy.uploadtask.ALARM_WAKE_ACTION";

        public UploadReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            startUploadTask();
        }
    }


    private List<LogTaskEntity> logTaskCache = new ArrayList<>();

    private void startUploadTask(){
        cancelAllTask();
        List<LogEntity> logEntityList = DatabaseUtils.getLogListWithState(this, BaseApplication.getCurUserName(),
                String.valueOf(BaseParamas.STATE_UPLOAD_FAILED));
        if(logEntityList == null || logEntityList.size() == 0){
            return;
        }
        for(int i = 0;i< logEntityList.size();i++){
            LogTaskEntity logTaskEntity = new LogTaskEntity(logEntityList.get(i));
            logTaskEntity.setCallback(new CallBackInterface() {
                @Override
                public void uploadTaskCallback(LogEntity logEntity, int resultCode) {
                    Log.e("wusy","resultCode = " + resultCode);
                    if(resultCode == BaseParamas.REQUEST_SUCCESS){
                        logEntity.setState(BaseParamas.STATE_UPLOAD);
                        DatabaseUtils.updateLog(BaseApplication.getCurApplicationContext(), logEntity);

                    }else if(resultCode == BaseParamas.REQUEST_TOKEN_USELESS){
                        logEntity.setState(BaseParamas.STATE_UPLOAD_FAILED);
                        DatabaseUtils.updateLog(BaseApplication.getCurApplicationContext(), logEntity);
                        cancelAllTask();
                        toLoginActivity(true);
                    }else{
                        logEntity.setState(BaseParamas.STATE_UPLOAD_FAILED);
                        DatabaseUtils.updateLog(BaseApplication.getCurApplicationContext(), logEntity);
                    }
                    if(logFragment != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logFragment.refreashPage();
                            }
                        });
                    }
                }
            });
            logTaskEntity.startTask();
        }
    }


    private void cancelAllTask(){
        if(logTaskCache == null){
            logTaskCache = new ArrayList<>();
        }
        if(logTaskCache.size() > 0){
            for(int i = 0;i < logTaskCache.size();i++){
                logTaskCache.get(0).cancelTask();
            }
            logTaskCache.clear();
        }
    }


    private void toLoginActivity(boolean  isToken){
        if(isToken){
            Toast.makeText(BaseApplication.getCurApplicationContext(), "token失效，请重新登录!", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("isLogout", true);
        startActivity(intent);
        finish();
    }

    public void verifyAccout(){
        String localToken = SPUtils.getStringParam(MainActivity.this, SPUtils.KEY_TOKEN);
        // TOKEN 如果为空，则是出现异常，返回重新登录
        if(TextUtils.isEmpty(localToken)){
            toLoginActivity(true);
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseParamas.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        PostInterface request = retrofit.create(PostInterface.class);
        Call<HttpResult> call = request.verify(localToken);
        call.enqueue(new Callback<HttpResult>() {

            @Override
            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
                if(response.body().getCode() == BaseParamas.REQUEST_SUCCESS){
                    SPUtils.saveParam(MainActivity.this, SPUtils.KEY_TOKEN, response.body().getToken());
                }else{
                    // 返回结果失败的话，直接返回登录页面重新登录
                    toLoginActivity(true);
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<HttpResult> call, Throwable throwable) {
                System.out.println("请求失败");
                System.out.println(throwable.getMessage());
            }
        });
    }
}
