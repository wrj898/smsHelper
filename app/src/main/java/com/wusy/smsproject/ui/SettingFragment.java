package com.wusy.smsproject.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
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
import com.wusy.smsproject.entity.NewBankCardEntity;
import com.wusy.smsproject.httpinterfaces.BankGetInterface;
import com.wusy.smsproject.httpinterfaces.PostInterface;
import com.wusy.smsproject.utils.BankUtils;
import com.wusy.smsproject.utils.SPUtils;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingFragment extends Fragment {

    private ListView bankcardList;
    private BankCardAdapter bankCardAdapter;

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
        etName.setText("测试号");
        etCardNo.setText("6217001930028895283");
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
                Toast.makeText(getContext(), "请求失败 : " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 删除银行卡
     * @param bankcardId 银行卡id编号
     */
    public void removeBankCard(String bankcardId){
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
                    Toast.makeText(getContext(), "删除银行卡 网络请求错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 成功
                if(response.body().getCode() == BaseParamas.REQUEST_SUCCESS){
                    getBankCardList();
                }else{
                    // 失败处理
                    Toast.makeText(getContext(), "删除银行卡失败 : getCode = " + response.body().getCode(), Toast.LENGTH_SHORT).show();
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<HttpResult> call, Throwable throwable) {
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
                    Toast.makeText(getContext(), "编辑银行卡 网络请求错误", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "请求失败 : " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 获取银行卡列表
     */
    public void getBankCardList(){
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
}
