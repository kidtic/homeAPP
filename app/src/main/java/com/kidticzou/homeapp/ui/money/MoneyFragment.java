package com.kidticzou.homeapp.ui.money;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private Bill[] data;
    private SaveBill[] savedata;
    private TextView mTv_savemoneyShow;
    private TextView mTv_savetargetShow;
    private String[] menu={"改变余额","账单"};


    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_money, container, false);
        mMoneyShow=root.findViewById(R.id.tv_money);
        mMenu=root.findViewById(R.id.lv_money);
        mMenu.setAdapter(new moneyMenuAdapter(root.getContext(),menu));
        mTv_savemoneyShow=root.findViewById(R.id.tv_savemoney);
        mTv_savetargetShow=root.findViewById(R.id.tv_savetarget);
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


        //查余额
        //mspf=getSharedPreferences("config",MODE_PRIVATE);

        mNet=new NetMsg(MoneyFragment.this,appdata.url,2333,appdata.user,appdata.passwd);
        //查询账单与储蓄单
        try {
            mNet.returnBill();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            mNet.returnSaveBill();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updataShow();


        return root;
    }


    private void updataShow(){
        myapp appdata=(myapp)getActivity().getApplication();
        float mony = appdata.appBillData[appdata.appBillData.length-1].money;
        mMoneyShow.setText(String.valueOf(mony));

        SaveBill[] data=appdata.appSaveBillData;
        //显示储蓄额
        String moneystr;
        moneystr=String.valueOf(data[data.length-1].money);
        mTv_savemoneyShow.setText(moneystr);
        //显示应存量
        String targetstr;
        targetstr=String.valueOf(data[data.length-1].target);
        targetstr="应存:"+targetstr;
        mTv_savetargetShow.setText(targetstr);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            mNet.returnBill();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updataShow();
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void loginhome() {

    }

    @Override
    public void errorLog(String errString) {
        Looper.prepare();
        Toast.makeText(getContext(),errString,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void returnBill(Bill[] data) {
        myapp appdata= (myapp) getActivity().getApplication();
        appdata.appBillData=data;

    }

    @Override
    public void returnSaveBill(SaveBill[] data) {
        myapp appdata=(myapp)getActivity().getApplication();
        appdata.appSaveBillData=data;

    }

    @Override
    public void home() {

    }


}