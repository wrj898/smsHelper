package com.wusy.smsproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wusy.smsproject.R;
import com.wusy.smsproject.entity.LogEntity;
import com.wusy.smsproject.utils.BankUtils;

import java.util.List;

public class LogInfoAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<LogEntity> list;


    public LogInfoAdapter(Context context , List<LogEntity> list){
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
            convertView = mInflater.inflate(R.layout.item_loginfo, null);
            holder.bankname = convertView.findViewById(R.id.loginfo_cardname);
            holder.cardno = convertView.findViewById(R.id.loginfo_cardno);
            holder.money = convertView.findViewById(R.id.loginfo_money);
            holder.time = convertView.findViewById(R.id.loginfo_time);
            holder.state = convertView.findViewById(R.id.loginfo_state);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.bankname.setText(list.get(position).getBankName());
        holder.cardno.setText(list.get(position).getCardNumber());
        holder.money.setText(list.get(position).getMoney());
        holder.time.setText(list.get(position).getTimeStr());
        holder.state.setText(list.get(position).getStateStr());
        return convertView;
    }


    public final class ViewHolder{
        public TextView bankname;
        public TextView cardno;
        public TextView money;
        public TextView time;
        public TextView state;
    }

}
