package com.kidticzou.homeapp.ui.money;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kidticzou.homeapp.R;

import java.util.Date;

public class moneyMenuAdapter extends BaseAdapter {
    private Context mContext;
    private String[] data;
    private LayoutInflater mLayInf;

    moneyMenuAdapter(Context context, String[] testname){
        this.mContext=context;
        this.data=testname;
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
        public ImageView mImageVeiw;
        public TextView tvTitle;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null){
            view=mLayInf.inflate(R.layout.adapter_money,null);
            holder=new ViewHolder();
            holder.mImageVeiw=view.findViewById(R.id.iv_money_item);
            holder.tvTitle=view.findViewById(R.id.tv_money_item);

            view.setTag(holder);
        }
        else {
            holder= (ViewHolder) view.getTag();
        }
        //给第i个项目的控件内容赋值
        holder.tvTitle.setText(data[i]);
        if(data[i].equals("改变余额"))
            holder.mImageVeiw.setImageResource(R.drawable.plus);
        else if(data[i].equals("账单"))
            holder.mImageVeiw.setImageResource(R.drawable.bill);
        return view;
    }
}
