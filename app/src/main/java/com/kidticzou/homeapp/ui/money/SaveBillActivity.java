
package com.kidticzou.homeapp.ui.money;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.widget.ListView;
import android.widget.Toast;

import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.model.Bill;
import com.kidticzou.homeapp.model.NetMsg;
import com.kidticzou.homeapp.model.SaveBill;
import com.kidticzou.homeapp.myapp;

public class SaveBillActivity extends AppCompatActivity implements NetMsg.ServerReturn{
    private ListView mLVbill;
    private myapp appdata;
    private NetMsg mNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_bill);

        mLVbill=findViewById(R.id.lv_bill);
        appdata= (myapp) getApplication();
        mNet=new NetMsg(SaveBillActivity.this,appdata.url,2333,appdata.user,appdata.passwd);

        //查线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bill[] data;
                synchronized (mNet) {
                    SaveBill[] mdata = mNet.SaveReturn();
                    data=new Bill[mdata.length];
                    for(int i=0;i<data.length;i++){
                        data[i]=mdata[mdata.length-i-1].to_Bill();
                    }

                }
                //其他线程中要修改UI数据，则需要用runOnUiThread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLVbill.setAdapter(new BillAdapter(SaveBillActivity.this,data));
                    }
                });



            }
        }).start();


    }

    @Override
    public void errorLog(String errString) {
        Looper.prepare();
        Toast.makeText(this,errString,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void home() {

    }
}