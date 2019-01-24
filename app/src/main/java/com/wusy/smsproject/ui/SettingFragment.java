package com.wusy.smsproject.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wusy.smsproject.BaseApplication;
import com.wusy.smsproject.R;
import com.wusy.smsproject.adapter.BankCardAdapter;
import com.wusy.smsproject.base.BaseParamas;
import com.wusy.smsproject.entity.BankCardEntity;
import com.wusy.smsproject.entity.CardInfo;
import com.wusy.smsproject.entity.HttpResult;
import com.wusy.smsproject.entity.HttpResultOfBankList;
import com.wusy.smsproject.entity.LogEntity;
import com.wusy.smsproject.entity.NewBankCardEntity;
import com.wusy.smsproject.httpinterfaces.BankGetInterface;
import com.wusy.smsproject.httpinterfaces.PostInterface;
import com.wusy.smsproject.utils.BankUtils;
import com.wusy.smsproject.utils.SPUtils;
import com.wusy.smsproject.utils.db.DatabaseUtils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingFragment extends Fragment {

    private ListView bankcardList;
    private BankCardAdapter bankCardAdapter;
    private ProgressDialog loadingDialog;
    private boolean isFirst = true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        TextView btnAdd = view.findViewById(R.id.setting_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(false, null);
            }
        });

        bankcardList = view.findViewById(R.id.setting_list);
        bankCardAdapter = new BankCardAdapter(getContext(), MainActivity.bankcardList);

        bankCardAdapter.setOnBankCardClickListener(new BankCardAdapter.BankCardClickListener() {
            @Override
            public void onItemClick(boolean isEdit, int position) {
                NewBankCardEntity cardEntity = null;
                if(MainActivity.bankcardList != null && position < MainActivity.bankcardList.size()){
                    cardEntity = MainActivity.bankcardList.get(position);
                }

                if(isEdit){
                    showInputDialog(true, cardEntity);
                }else{
                    // 转换锁定状态
                    if(cardEntity == null){
                        return;
                    }
                    // 如果是从未锁定状态转到锁定状态，需要判断是否有绑定重复卡
                    if(!cardEntity.isLocked()){
                        if(hasSameBankSelect(cardEntity)){
                            Toast.makeText(BaseApplication.getCurApplicationContext(), "已经监控 " + cardEntity.getNote() +
                                    " 银行卡，不允许重复银行", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // 切换状态并刷新页面
                    cardEntity.setLocked(!cardEntity.isLocked());
                    StringBuilder lockedBank = new StringBuilder();
                    for(int i =0;i < MainActivity.bankcardList.size(); i++){
                        if(MainActivity.bankcardList.get(i).isLocked()){
                            lockedBank.append(MainActivity.bankcardList.get(i).getApp_id());
                            lockedBank.append(",");
                        }
                    }
                    // 存储key为用户名称+用户id
                    String key = BaseApplication.getCurRealUserName() + BaseApplication.getCurUserName();
                    SPUtils.saveParam(BaseApplication.getCurApplicationContext(),key, lockedBank.toString());
                    bankCardAdapter.notifyDataSetChanged();

                }
            }
        });
        bankcardList.setAdapter(bankCardAdapter);

        bankcardList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(MainActivity.bankcardList != null && position < MainActivity.bankcardList.size()){
                    showDeletDialog(MainActivity.bankcardList.get(position));
                }else{
                    Toast.makeText(BaseApplication.getCurApplicationContext(),"长按删除位置出错", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        getBankCardList();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * 显示删除确认对话框
     */
    private  void showDeletDialog(final NewBankCardEntity bankCardEntity){
        if(bankCardEntity == null){
            Toast.makeText(BaseApplication.getCurApplicationContext(),"showDeletDialog 传入删除银行卡信息出错", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(getContext());
        normalDialog.setTitle("确认删除？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeBankCard(bankCardEntity.getId());
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }


    private void showInputDialog(final boolean isEdit, final NewBankCardEntity bankCardEntity) {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.dialog_bankcard, null);
        final EditText etName = view.findViewById(R.id.et_bankcard_name);
        final EditText etCardNo = view.findViewById(R.id.et_bankcard_cardno);
//        etName.setText("测试号");
//        etCardNo.setText("6217001930028895283");
        if(isEdit && bankCardEntity != null){
            etName.setText(bankCardEntity.getName());
            etCardNo.setText(bankCardEntity.getApp_id());
        }
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(getContext());
        if(isEdit){
            inputDialog.setTitle("修改银行卡信息");
        }else{
            inputDialog.setTitle("新增银行卡");
        }
        inputDialog.setView(view);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etName.getText().toString();
                        String cardNo = etCardNo.getText().toString();
                        if(TextUtils.isEmpty(name)){
                            Toast.makeText(getContext(), "姓名不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(TextUtils.isEmpty(cardNo)){
                            Toast.makeText(getContext(), "银行卡号不能为空", Toast.LENGTH_SHORT).show();
                        }
                        if(BankUtils.checkBankCard(cardNo)){
                            if(isEdit){
                                validateBankcard(cardNo, name, bankCardEntity);
                            }else{
                                validateBankcard(cardNo, name, null);
                            }
                        }else{
                            Toast.makeText(getContext(), "请输入正确的银行卡号", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        inputDialog.show();
    }


    /**
     * 验证银行卡号，通过的话上传到服务端
     */
    public void validateBankcard(final String cardNo, final String name, final NewBankCardEntity bankCardEntity){
        showLoadingDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseParamas.ALIPAY_CCDCAPI) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        BankGetInterface request = retrofit.create(BankGetInterface.class);
        Call<CardInfo> call = request.validateAndCacheCardInfo("utf-8",true, cardNo);
        call.enqueue(new Callback<CardInfo>() {

            @Override
            public void onResponse(Call<CardInfo> call, Response<CardInfo> response) {
                if(response.body() == null){
                    hideLoadingDailog();
                    Toast.makeText(getContext(), "验证银行卡号 网络请求错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                if("ok".equals(response.body().getStat()) && response.body().isValidated()){
                    // 为空则为添加
                    if(bankCardEntity == null){
                        addBankCard(name, response.body().getBank(), BankUtils.getNameOfBank(response.body().getBank()), cardNo);
                    }else{
                        editBankCard(bankCardEntity.getId(), name, response.body().getBank(), BankUtils.getNameOfBank(response.body().getBank()), cardNo);
                    }
                }else{
                    hideLoadingDailog();
                    if(!response.body().isValidated()){
                        Toast.makeText(getContext(), "请输入正确的银行卡号", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getContext(), "银行卡信息查询接口返回状态:" + response.body().getStat(), Toast.LENGTH_SHORT).show();
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<CardInfo> call, Throwable throwable) {
                hideLoadingDailog();
                Toast.makeText(getContext(), "请求失败 : " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 上传银行卡信息
     * @param name 姓名
     * @param bankCode 银行卡代码
     * @param bankName 银行名称
     * @param cardNo 卡号
     */
    public void addBankCard(String name, String bankCode, String bankName, String cardNo){
        showLoadingDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseParamas.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        PostInterface request = retrofit.create(PostInterface.class);

        String token = SPUtils.getStringParam(getContext(), SPUtils.KEY_TOKEN);
        String userId = BaseApplication.getCurUserName();

        Call<HttpResult> call = request.addBankCard(token, name, userId, bankCode,bankName, cardNo);
        call.enqueue(new Callback<HttpResult>() {

            @Override
            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
                if(response.body() == null){
                    hideLoadingDailog();
                    Toast.makeText(getContext(), "上传银行卡信息 网络请求错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 成功
                if(response.body().getCode() == BaseParamas.REQUEST_SUCCESS){
                    getBankCardList();
                }else{
                    // 失败处理
                    Toast.makeText(getContext(), "添加银行卡失败 : getCode = " + response.body().getCode(), Toast.LENGTH_SHORT).show();
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<HttpResult> call, Throwable throwable) {
                hideLoadingDailog();
                Toast.makeText(getContext(), "请求失败 : " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 删除银行卡
     * @param bankcardId 银行卡id编号
     */
    public void removeBankCard(String bankcardId){
        showLoadingDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseParamas.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        PostInterface request = retrofit.create(PostInterface.class);

        String token = SPUtils.getStringParam(getContext(), SPUtils.KEY_TOKEN);

        Call<HttpResult> call = request.removeBankCard(token,bankcardId);
        call.enqueue(new Callback<HttpResult>() {

            @Override
            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
                if(response.body() == null){
                    hideLoadingDailog();
                    Toast.makeText(getContext(), "删除银行卡 网络请求错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 成功
                if(response.body().getCode() == BaseParamas.REQUEST_SUCCESS){
                    getBankCardList();
                }else{
                    // 失败处理
                    hideLoadingDailog();
                    Toast.makeText(getContext(), "删除银行卡失败 : getCode = " + response.body().getCode(), Toast.LENGTH_SHORT).show();
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<HttpResult> call, Throwable throwable) {
                hideLoadingDailog();
                Toast.makeText(getContext(), "请求失败 : " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 编辑银行卡
     * @param id 银行卡编号
     * @param name 姓名
     * @param bankCode 银行卡代码
     * @param bankName 银行名称
     * @param cardNo 卡号
     */
    public void editBankCard(String id, String name, String bankCode, String bankName, String cardNo){
        showLoadingDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseParamas.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        PostInterface request = retrofit.create(PostInterface.class);

        String token = SPUtils.getStringParam(getContext(), SPUtils.KEY_TOKEN);

        Call<HttpResult> call = request.editBankCard(token, id, name, bankCode,bankName, cardNo);
        call.enqueue(new Callback<HttpResult>() {

            @Override
            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
                if(response.body() == null){
                    hideLoadingDailog();
                    Toast.makeText(getContext(), "编辑银行卡 网络请求错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 成功
                if(response.body().getCode() == BaseParamas.REQUEST_SUCCESS){
                    getBankCardList();
                }else{
                    // 失败处理
                    hideLoadingDailog();
                    Toast.makeText(getContext(), "添加银行卡失败 : getCode = " + response.body().getCode(), Toast.LENGTH_SHORT).show();
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<HttpResult> call, Throwable throwable) {
                hideLoadingDailog();
                Toast.makeText(getContext(), "请求失败 : " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 获取银行卡列表
     */
    public void getBankCardList(){
        showLoadingDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseParamas.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        PostInterface request = retrofit.create(PostInterface.class);

        String token = SPUtils.getStringParam(getContext(), SPUtils.KEY_TOKEN);

        Call<HttpResultOfBankList> call = request.getBankList(token);
        call.enqueue(new Callback<HttpResultOfBankList>() {

            @Override
            public void onResponse(Call<HttpResultOfBankList> call, Response<HttpResultOfBankList> response) {
                hideLoadingDailog();
                if(response.body() == null){
                    Toast.makeText(getContext(), "获取银行卡列表 : 网络请求错误 ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 成功
                if(response.body() != null && response.body().getCode() == BaseParamas.REQUEST_SUCCESS){
                    if(MainActivity.bankcardList == null){
                        MainActivity.bankcardList = new ArrayList<>();
                    }else{
                        MainActivity.bankcardList.clear();
                    }
                    Toast.makeText(getContext(), "刷新银行卡列表成功" + response.body().getCode(), Toast.LENGTH_SHORT).show();
                    MainActivity.bankcardList.addAll(response.body().getCardList());

                    String key = BaseApplication.getCurRealUserName() + BaseApplication.getCurUserName();
                    String lockBank = SPUtils.getStringParam(BaseApplication.getCurApplicationContext(), key);
                    for(int i = 0; i < MainActivity.bankcardList.size();i++){
                        // 如果保存信息里面 包含该银行，则状态换为锁定
                        if(lockBank.contains(MainActivity.bankcardList.get(i).getApp_id())){
                            MainActivity.bankcardList.get(i).setLocked(true);
                        }
                    }
                    if(isFirst){
                        isFirst = false;
//                        getRecentSms();
                    }
                    bankCardAdapter.notifyDataSetChanged();
                }else{
                    // 失败处理
                    Toast.makeText(getContext(), "获取银行卡列表 : getCode = " + response.body().getCode(), Toast.LENGTH_SHORT).show();
                    if(response.body().getCode() == BaseParamas.REQUEST_TOKEN_INVALID){
                        // 如果是token失效 返回到登录页面
                        if(getContext() != null && getActivity() != null){
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.putExtra("isLogout", true);
                            getContext().startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<HttpResultOfBankList> call, Throwable throwable) {
                hideLoadingDailog();
                Toast.makeText(getContext(), "请求失败 : " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean hasSameBankSelect(NewBankCardEntity bankCardEntity){
        if(MainActivity.bankcardList != null){
            for(int i =0;i < MainActivity.bankcardList.size(); i++){
                // 同一个银行的情况
                if(MainActivity.bankcardList.get(i).getCode().equals(bankCardEntity.getCode())){
                    if(MainActivity.bankcardList.get(i).isLocked()){
                        // 同一个银行 如果有已经被锁定状态的银行卡，则代表重复绑定同一个银行
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void showLoadingDialog() {
        if(getActivity() == null || loadingDialog == null){
            loadingDialog = new ProgressDialog(getActivity());
//        mDefaultDialog.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER); //默认就是小圆圈的那种形式
            loadingDialog.setMessage("正在请求...");
//        mDefaultDialog.setCancelable(true);//默认true
            loadingDialog.setCanceledOnTouchOutside(false);//默认true
        }
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
        }
    }

    public void hideLoadingDailog(){
        if(loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    /**
     * 获取近期短信
     * 思路: 每次app关闭或异常退出，会保留一个关闭退出时间(异常退出不一定会保存)
     *       当下次进入app，第一次到达银行页面 获取到银行卡列表的时候，会根据银行卡列表锁定信息，
     *       以及上次退出时间(如果上次退出时间太长，会只统计一天内的短信信息)来读取短信列表，
     *       短信列表信息筛选过后，发送广播，提示
     */
    public void getRecentSms() {
        // 所有短信
        String SMS_URI_ALL = "content://sms/";
        try {
            if(getContext() == null){
                return;
            }
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[] { "_id", "address", "person","body", "date", "type"};
            // 上次退出的时间
            long lastDestoryTime = SPUtils.getLongParam(BaseApplication.getCurApplicationContext(), SPUtils.KEY_DESTORY_TIME);
            // 一天的毫秒数
            long dayMills = 24 * 60 * 60 * 1000;
            if(System.currentTimeMillis() - lastDestoryTime > dayMills){
                // 如果上次退出 超过一天的时间，就只读取一天内的短信
                lastDestoryTime = System.currentTimeMillis() - dayMills;
            }
            // 筛选条件
            String condition = " date >  " + String.valueOf(lastDestoryTime);
            Cursor cur = getContext().getContentResolver().query(uri, projection, condition,null, "date desc");

            // 获取当前用户所有银行卡列表
            HashMap<String, BankCardEntity> bankCardMap = MainActivity.getBankCardHashMap();
            if(bankCardMap.size() == 0){
                return;
            }

            if (cur != null && cur.moveToFirst()) {
                // 发信人
                int index_Address = cur.getColumnIndex("address");
                // 短信内容
                int index_Body = cur.getColumnIndex("body");
                // 短信时间
                int index_Date = cur.getColumnIndex("date");
                // 短信类型 0:所以短信  1:"接收" 2:"发送"  3:"草稿"  4:"发件箱"  5:"发送失败" 6:"待发送列表"
                int index_Type = cur.getColumnIndex("type");

                do {
                    String strAddress = cur.getString(index_Address);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int intType = cur.getInt(index_Type);

                    BankCardEntity curBankCard = null;
                    if(!bankCardMap.containsKey(strAddress)){
                        continue;
                    }
                    // 根据时间来作为查询条件，看是否数据库已经包含该短信信息
                    if(DatabaseUtils.isContainLog(getContext(), String.valueOf(longDate))){
                        continue;
                    }

                    curBankCard = bankCardMap.get(strAddress);

                    LogEntity logEntity = new LogEntity();
                    logEntity.setBankName(curBankCard.getBankName());
                    logEntity.setCardNumber(curBankCard.getCardNumber());
                    logEntity.setUserKey(BaseApplication.getCurUserName());
                    logEntity.setTime(String.valueOf(longDate));
                    logEntity.setMoney(BankUtils.getMoneyFromSMS(strAddress,strbody));
                    logEntity.setState(BaseParamas.STATE_WITHOUT_UPLOAD);
                    DatabaseUtils.insertLog(getContext(), logEntity);
                } while (cur.moveToNext());

                Intent intent = new Intent();
                intent.setAction("wusy.uploadtask.ALARM_WAKE_ACTION");
                getContext().sendBroadcast(intent);

                if (!cur.isClosed()) {
                    cur.close();
                }
            } else {
                Log.e("LogFragment", "没有短信");
            }
            Log.d("LogFragment", "读取短信结束");
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
    }

}
