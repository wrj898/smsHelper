package com.wusy.smsproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wusy.smsproject.R;
import com.wusy.smsproject.entity.BankCardEntity;

import java.util.List;

public class BankCardAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<BankCardEntity> list;


    public BankCardAdapter(Context context , List<BankCardEntity> list){
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_bankcard, null);
            holder.bankname = convertView.findViewById(R.id.bankcard_name);
            holder.cardno = convertView.findViewById(R.id.bankcard_cardno);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.bankname.setText(list.get(position).getBankName());
        holder.cardno.setText(list.get(position).getCardNumber());
        return convertView;
    }


    public final class ViewHolder{
        public TextView bankname;
        public TextView cardno;
    }

}
