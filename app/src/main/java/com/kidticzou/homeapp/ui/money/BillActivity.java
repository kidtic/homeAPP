package com.kidticzou.homeapp.ui.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kidticzou.homeapp.MainActivity;
import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.model.Bill;
import com.kidticzou.homeapp.model.NetMsg;
import com.kidticzou.homeapp.model.SaveBill;
import com.kidticzou.homeapp.myapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

public class BillActivity extends AppCompatActivity implements NetMsg.ServerReturn {
    private ListView mLVbill;
    private myapp appdata;
    private NetMsg mNet;
    private EditText mSearchText;
    private Bill[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        mLVbill=findViewById(R.id.lv_bill);
        mSearchText=findViewById(R.id.bill_search_et);
        appdata= (myapp) getApplication();
        mNet=new NetMsg(BillActivity.this,appdata.url,2333,appdata.user,appdata.passwd);
        //设置搜索功能
        mSearchText.addTextChangedListener(textWatcher);

        //查余额线程
        new Thread(new Runnable() {
            @Override
            public void run() {

                synchronized (mNet) {
                    Bill[] mdata = mNet.PayReturn();
                    data=new Bill[mdata.length];
                    for(int i=0;i<data.length;i++){
                        data[i]=mdata[mdata.length-i-1];
                    }

                }
                //其他线程中要修改UI数据，则需要用runOnUiThread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLVbill.setAdapter(new BillAdapter(BillActivity.this,data));
                    }
                });
            }
        }).start();



    }


    @Override
    public void errorLog(final String errString) {
        //弹窗
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BillActivity.this,errString,Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void home() {

    }

    //搜索功能使用
    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //System.out.println("-1-beforeTextChanged-->" + mSearchText.getText().toString() + "<--");
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //System.out.println("-1-onTextChanged-->" + mSearchText.getText().toString() + "<--");
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //System.out.println("-1-afterTextChanged-->" + mSearchText.getText().toString() + "<--");
            //匹配
            String[] keyword=mSearchText.getText().toString().split(" ");

            //匹配
            ArrayList<Bill> billArrayData=new ArrayList<Bill>();
            for(int i=0;i<data.length;i++){
                int MatchNum=0;
                for (String s : keyword) {
                    if (data[i].ps.contains(s)) {
                        MatchNum++;
                    }
                }
                float rdi=(float) MatchNum/(float) keyword.length;
                if(rdi>0.8){
                    billArrayData.add(data[i]);
                }

            }
            //转换
            Bill[] mdata= new Bill[billArrayData.size()];
            for(int i=0;i<billArrayData.size();i++){
                mdata[i]=billArrayData.get(i);
            }
            mLVbill.setAdapter(new BillAdapter(BillActivity.this,mdata));
        }
    };
}