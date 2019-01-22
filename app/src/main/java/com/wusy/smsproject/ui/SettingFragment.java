package com.wusy.smsproject.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.wusy.smsproject.httpinterfaces.BankGetInterface;
import com.wusy.smsproject.utils.BankUtils;
import com.wusy.smsproject.utils.db.DatabaseUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingFragment extends Fragment {

    private ListView bankcardList;
    private BankCardAdapter bankCardAdapter;
    private List<BankCardEntity> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        TextView btnAdd = view.findViewById(R.id.setting_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        bankcardList = view.findViewById(R.id.setting_list);
        list = DatabaseUtils.getBankCardList(getContext(),BaseApplication.getCurUserName());
        bankCardAdapter = new BankCardAdapter(getContext(), list);
        bankcardList.setAdapter(bankCardAdapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    private void showInputDialog() {
        final EditText editText = new EditText(getContext());
        DigitsKeyListener numericOnlyListener = new DigitsKeyListener(false,true);
        editText.setKeyListener(numericOnlyListener);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(getContext());
        inputDialog.setTitle("输入银行卡号").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String bankNum = editText.getText().toString();
                        if(BankUtils.checkBankCard(bankNum)){
                            validateBankcard(bankNum);
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


    public void validateBankcard(final String cardNo){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseParamas.ALIPAY_CCDCAPI) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        BankGetInterface request = retrofit.create(BankGetInterface.class);
        Call<CardInfo> call = request.validateAndCacheCardInfo("utf-8",true, cardNo);
        call.enqueue(new Callback<CardInfo>() {

            @Override
            public void onResponse(Call<CardInfo> call, Response<CardInfo> response) {
                if("ok".equals(response.body().getStat())){
                    BankCardEntity bankCardEntity = new BankCardEntity();
                    bankCardEntity.setBankName(BankUtils.getNameOfBank(response.body().getBank()));
                    if(DatabaseUtils.hasBankCard(getContext(),BaseApplication.getCurUserName(), bankCardEntity.getBankName())){
                        Toast.makeText(getContext(), "已有 " + bankCardEntity.getBankName() + " 银行卡", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    bankCardEntity.setBankCode(BankUtils.getTelFromBankName(bankCardEntity.getBankName()));
                    bankCardEntity.setCardNumber(cardNo);
                    bankCardEntity.setUserKey(BaseApplication.getCurUserName());
                    list.add(bankCardEntity);
                    bankCardAdapter.notifyDataSetChanged();
                    // TODO 存储到数据库
                    DatabaseUtils.insertBankCard(getContext(), bankCardEntity);
                }else{
                    Toast.makeText(getContext(), "请输入正确的银行卡号", Toast.LENGTH_SHORT).show();
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<CardInfo> call, Throwable throwable) {
                System.out.println("请求失败");
                System.out.println(throwable.getMessage());
            }
        });
    }
}
