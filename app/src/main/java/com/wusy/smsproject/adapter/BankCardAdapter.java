package com.wusy.smsproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wusy.smsproject.R;
import com.wusy.smsproject.entity.BankCardEntity;
import com.wusy.smsproject.entity.NewBankCardEntity;

import java.util.List;

public class BankCardAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<NewBankCardEntity> list;
    private Context mContext;
    private BankCardClickListener clickListener;


    public BankCardAdapter(Context context , List<NewBankCardEntity> list){
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
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
            holder.btnEdit = convertView.findViewById(R.id.bankcard_edit);
            holder.btnChange = convertView.findViewById(R.id.bankcard_change);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.bankname.setText(list.get(position).getNote());
        holder.cardno.setText(list.get(position).getApp_id());
        holder.btnEdit.setTag(position);
        holder.btnChange.setTag(position);

        holder.btnEdit.setOnClickListener(mListener);
        holder.btnChange.setOnClickListener(mListener);

        changeViewShow(position, holder.btnChange);
        return convertView;
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bankcard_edit:
                    if(clickListener != null){
                        clickListener.onItemClick(true, (Integer) v.getTag());
                    }
                    break;
                case R.id.bankcard_change:
                    if(clickListener != null){
                        clickListener.onItemClick(false, (Integer) v.getTag());
                    }
                    break;
            }
        }
    };

    public final class ViewHolder{
        public TextView bankname;
        public TextView cardno;
        public TextView btnEdit;
        public TextView btnChange;
    }

    private void changeViewShow(int position, TextView tvChange){
        if(mContext != null && tvChange != null && list != null && position < list.size()){
            NewBankCardEntity bankCardEntity = list.get(position);
            if(bankCardEntity.isLocked()){
                tvChange.setText("解除");
                tvChange.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            }else{
                tvChange.setText("锁定");
                tvChange.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            }
        }
    }

    public void setOnBankCardClickListener(BankCardClickListener listener){
        this.clickListener = listener;
    }

    public interface BankCardClickListener{
        public void onItemClick(boolean isEdit, int position);
    }
}
