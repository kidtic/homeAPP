package com.kidticzou.homeapp.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kidticzou.homeapp.MainActivity;
import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.model.Bill;
import com.kidticzou.homeapp.model.NetMsg;
import com.kidticzou.homeapp.model.SaveBill;
import com.kidticzou.homeapp.myapp;
import com.kidticzou.homeapp.ui.money.MoneyFragment;

public class LoginActivity extends AppCompatActivity implements NetMsg.ServerReturn {
    private EditText mEditurl;
    private EditText mEdituser;
    private EditText mEditpasswd;
    private Button mBtnLogin;
    private NetMsg mNet;
    private SharedPreferences mspf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEditurl=findViewById(R.id.et_url);
        mEdituser=findViewById(R.id.et_user);
        mEditpasswd=findViewById(R.id.et_passwd);
        mBtnLogin=findViewById(R.id.btn_login);
        mspf=super.getSharedPreferences("config",MODE_PRIVATE);
        //判断默认
        final String url = mspf.getString("url",null);
        if(url!=null){
            mEditurl.setText(url);
        }
        else{
            Toast.makeText(this,"无法读取url",Toast.LENGTH_SHORT).show();
        }
        final String usstr=mspf.getString("user",null);
        if(usstr!=null)mEdituser.setText(usstr);
        final String pswd = mspf.getString("passwd",null);
        if(pswd!=null) mEditpasswd.setText(pswd);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mspf.edit();
                editor.putString("url",mEditurl.getText().toString());
                editor.putString("user",mEdituser.getText().toString());
                editor.putString("passwd",mEditpasswd.getText().toString());
                editor.commit();
                myapp appdata=(myapp) getApplication();
                appdata.url=mEditurl.getText().toString();
                appdata.user=mEdituser.getText().toString();
                appdata.passwd=mEditpasswd.getText().toString();
                mNet=new NetMsg(LoginActivity.this,mEditurl.getText().toString(),2333,
                        mEdituser.getText().toString(),
                        mEditpasswd.getText().toString());
                mNet.loginServer();
                //Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                //startActivity(intent);
            }
        });

    }


    //ServerReturn接口的回响方法
    @Override
    public void loginhome() {
        Intent intent =new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void errorLog(String errString) {
        Looper.prepare();
        Toast.makeText(LoginActivity.this,errString,Toast.LENGTH_SHORT).show();
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