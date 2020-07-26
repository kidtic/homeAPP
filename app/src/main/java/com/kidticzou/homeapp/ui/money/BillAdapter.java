package com.kidticzou.homeapp.ui.money;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.model.Bill;

import java.text.SimpleDateFormat;

public class BillAdapter extends BaseAdapter {
    private Bill[] data;
    private Context mContext;
    private LayoutInflater mLayInf;


    BillAdapter(Context context, Bill[] data){
        mContext=context;
        this.data=data;
        mLayInf=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
       return data.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    static class ViewHolder{
        public TextView tvTime;
        public TextView tvps;
        public TextView tvChmoney;
        public TextView tvMoney;

    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null){
            view=mLayInf.inflate(R.layout.bill_item,null);
            holder=new ViewHolder();
            holder.tvTime=view.findViewById(R.id.tv_bill_time);
            holder.tvps=view.findViewById(R.id.tv_bill_ps);
            holder.tvChmoney=view.findViewById(R.id.tv_bill_changemoney);
            holder.tvMoney=view.findViewById(R.id.tv_bill_money);
            view.setTag(holder);
        }
        else {
            holder= (ViewHolder) view.getTag();
        }
        //给第i个项目的控件内容赋值
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.tvTime.setText( formatter.format(data[i].time));
        holder.tvps.setText(data[i].ps);
        String cmstr= String.valueOf(data[i].moneychange);
        if(data[i].moneychange>0){
            cmstr="+"+cmstr;
            holder.tvChmoney.setTextColor(Color.rgb(255,0,0));
        }
        else {
            holder.tvChmoney.setTextColor(Color.rgb(0,255,0));
        }
        holder.tvChmoney.setText(cmstr);
        holder.tvMoney.setText(String.valueOf(data[i].money));

        return view;
    }
}
