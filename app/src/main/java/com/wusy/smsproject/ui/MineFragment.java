package com.wusy.smsproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wusy.smsproject.BaseApplication;
import com.wusy.smsproject.R;

public class MineFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        TextView userName = view.findViewById(R.id.mine_username);
        TextView userRate = view.findViewById(R.id.mine_rate);
        TextView userMoney = view.findViewById(R.id.mine_money);
        TextView logOut = view.findViewById(R.id.mine_logout);
        userName.setText(BaseApplication.getCurRealUserName());
        userMoney.setText(BaseApplication.getCurUserMoney());
        userRate.setText(BaseApplication.getCurUserRate());
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getContext() != null && getActivity() != null){
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.putExtra("isLogout", true);
                    getContext().startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        return view;
    }
}
