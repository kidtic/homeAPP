package com.kidticzou.homeapp.ui.money;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.model.Bill;
import com.kidticzou.homeapp.model.NetMsg;
import com.kidticzou.homeapp.model.SaveBill;
import com.kidticzou.homeapp.myapp;

public class MoneyFragment extends Fragment implements NetMsg.ServerReturn {
    private TextView mMoneyShow;
    private ListView mMenu;
    private NetMsg mNet;
    private SharedPreferences mspf;
    private TextView mTv_savemoneyShow;
    private TextView mTv_savetargetShow;
    private CardView mMoneyCard;
    private String[] menu={"改变余额","账单"};


    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_money, container, false);
        mMoneyShow=root.findViewById(R.id.tv_money);
        mMenu=root.findViewById(R.id.lv_money);
        mMenu.setAdapter(new moneyMenuAdapter(root.getContext(),menu));
        mTv_savemoneyShow=root.findViewById(R.id.tv_savemoney);
        mTv_savetargetShow=root.findViewById(R.id.tv_savetarget);
        mMoneyCard=root.findViewById(R.id.moneycard);
        final myapp appdata= (myapp) getActivity().getApplication();

        mMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(menu[i].equals("改变余额")){
                    System.out.println("loguser: "+appdata.user);
                    if(appdata.user.equals("root")){
                        Intent intent =new Intent(root.getContext(), ChangeMoneyActivity.class);
                        //startActivity(intent);
                        startActivityForResult(intent,1);
                    }
                    else {
                        Toast.makeText(getContext(),"没有权限",Toast.LENGTH_SHORT).show();
                    }

                }
                else if(menu[i].equals("账单")){
                    Intent intent =new Intent(root.getContext(), BillActivity.class);
                    startActivity(intent);

                }
            }
        });


        //-----初始化通讯
        mNet=new NetMsg(MoneyFragment.this,appdata.url,2333,appdata.user,appdata.passwd);
        //-----查询余额与储蓄值线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bill data;
                final SaveBill savedata;
                synchronized (this){
                    data=mNet.PayReturnLast();
                    savedata=mNet.SaveReturnLast();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMoneyShow.setText(String.valueOf(data.money));
                        mTv_savemoneyShow.setText(String.valueOf(savedata.money));
                        mTv_savetargetShow.setText(String.valueOf(savedata.target));
                        if(savedata.money<savedata.target){
                            mMoneyCard.setCardBackgroundColor(Color.rgb(0xE6,0x1A,0x1A));
                        }
                        else{
                            mMoneyCard.setCardBackgroundColor(Color.rgb(0x11,0xEE,0xEE));
                        }
                    }
                });
            }
        }).start();

        //设置储蓄卡的点击事件
        mMoneyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(root.getContext(), SaveBillActivity.class);
                startActivity(intent);
            }
        });
        //root权限 改变存储
        if(appdata.user.equals("root")) {
            mMoneyCard.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent =new Intent(root.getContext(), ChangeSaveMoneyActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent,1);
                    return true;
                }
            });
        }


        return root;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //-----查询余额与储蓄值线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bill data;
                final SaveBill savedata;
                synchronized (this){
                    data=mNet.PayReturnLast();
                    savedata=mNet.SaveReturnLast();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMoneyShow.setText(String.valueOf(data.money));
                        mTv_savemoneyShow.setText(String.valueOf(savedata.money));
                        mTv_savetargetShow.setText(String.valueOf(savedata.target));
                        if(savedata.money<savedata.target){
                            mMoneyCard.setCardBackgroundColor(Color.rgb(0xE6,0x1A,0x1A));
                        }
                        else{
                            mMoneyCard.setCardBackgroundColor(Color.rgb(0x11,0xEE,0xEE));
                        }
                    }
                });
            }
        }).start();

        super.onActivityResult(requestCode, resultCode, data);

    }



    @Override
    public void errorLog(final String errString) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),errString,Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void home() {

    }


}