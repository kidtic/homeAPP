package com.kidticzou.homeapp.ui.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ListView;
import android.widget.Toast;

import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.model.Bill;
import com.kidticzou.homeapp.model.NetMsg;
import com.kidticzou.homeapp.model.SaveBill;
import com.kidticzou.homeapp.myapp;

public class BillActivity extends AppCompatActivity implements NetMsg.ServerReturn {
    private ListView mLVbill;
    private Bill[] data;
    private myapp appdata;
    private NetMsg mNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        //查余额
        appdata=(myapp) getApplication();
        data=appdata.appBillData;
        mLVbill=findViewById(R.id.lv_bill);
        mLVbill.setAdapter(new BillAdapter(this,data));


    }

    @Override
    public void loginhome() {

    }

    @Override
    public void errorLog(String errString) {
        Looper.prepare();
        Toast.makeText(this,errString,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void returnBill(Bill[] data) {

    }

    @Override
    public void returnSaveBill(SaveBill[] data) {

    }

    @Override
    public void home() {

    }
}