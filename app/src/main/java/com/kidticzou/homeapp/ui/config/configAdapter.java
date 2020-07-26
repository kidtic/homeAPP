package com.kidticzou.homeapp.ui.config;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.ui.money.moneyMenuAdapter;

public class configAdapter extends BaseAdapter {
    private Context mContext;
    private String[] data;
    private LayoutInflater mLayInf;

    configAdapter(Context context, String[] testname){
        this.mContext=context;
        this.data=testname;
        mLayInf=LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return data.length; }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    static class ViewHolder{
        public TextView tvTitle;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null){
            view=mLayInf.inflate(R.layout.config_1_item,null);
            holder=new ViewHolder();
            holder.tvTitle=view.findViewById(R.id.tv_config_name);

            view.setTag(holder);
        }
        else {
            holder= (ViewHolder) view.getTag();
        }
        //给第i个项目的控件内容赋值
        holder.tvTitle.setText(data[i]);

        return view;
    }
}
