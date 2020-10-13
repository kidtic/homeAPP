package com.kidticzou.homeapp.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kidticzou.homeapp.MainActivity;
import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.model.Bill;
import com.kidticzou.homeapp.model.NetMsg;
import com.kidticzou.homeapp.model.SaveBill;
import com.kidticzou.homeapp.model.versionType;
import com.kidticzou.homeapp.myapp;
import com.kidticzou.homeapp.ui.money.ChangeSaveMoneyActivity;
import com.kidticzou.homeapp.ui.money.MoneyFragment;

public class LoginActivity extends AppCompatActivity implements NetMsg.ServerReturn {
    private EditText mEditurl;
    private EditText mEdituser;
    private EditText mEditpasswd;
    private Button mBtnLogin;
    private NetMsg mNet;
    private SharedPreferences mspf;
    private versionType version;
    private myapp appdata;

    //准备对话框
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEditurl=findViewById(R.id.et_url);
        mEdituser=findViewById(R.id.et_user);
        mEditpasswd=findViewById(R.id.et_passwd);
        mBtnLogin=findViewById(R.id.btn_login);
        mspf=super.getSharedPreferences("config",MODE_PRIVATE);
        //查看软件版本
        String vstr=getAppVersionName(LoginActivity.this);
        version=new versionType(vstr);
        //app
        appdata= (myapp) getApplication();
        appdata.localVersion=new versionType(vstr);

        //设置对话框内容
        builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("软件更新");// 设置标题
        builder.setMessage("软件必须要更新才能继续使用，是否更新？");// 为对话框设置内容
        // 为对话框设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(appdata.mConfigUpdate));
                it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                startActivity(it);
            }
        });

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


        //设置点击
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mspf.edit();
                editor.putString("url",mEditurl.getText().toString());
                editor.putString("user",mEdituser.getText().toString());
                editor.putString("passwd",mEditpasswd.getText().toString());
                editor.commit();
                appdata.url=mEditurl.getText().toString();
                appdata.user=mEdituser.getText().toString();
                appdata.passwd=mEditpasswd.getText().toString();
                mNet=new NetMsg(LoginActivity.this,mEditurl.getText().toString(),2333,
                        mEdituser.getText().toString(),
                        mEditpasswd.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String lgres=mNet.loginServer();
                        String[] lgressin=lgres.split(":");
                        if(lgressin[0].equals("ok")){
                            //查看版本号是否满足要求
                            versionType remoteVersion=new versionType(lgressin[1]);
                            appdata.remoteVersion=new versionType(lgressin[1]);
                            boolean TostDig=false;
                            if(remoteVersion.gen>version.gen){
                                //对话框
                                TostDig=true;
                            }
                            else if(remoteVersion.gen==version.gen){
                                if(remoteVersion.bigversion>version.bigversion){
                                    TostDig=true;
                                }
                            }
                            //弹出对话框
                            if(TostDig){
                                Looper.prepare();
                                builder.create().show();
                                Looper.loop();
                            }
                            else{
                                //跳转
                                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                            }



                        }
                        else{
                            Looper.prepare();
                            Toast.makeText(LoginActivity.this,lgres,Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();

            }
        });

    }


    //ServerReturn接口的回响方法

    @Override
    public void errorLog(final String errString) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,errString,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void home() {

    }


    public String getAppVersionName(Context context) {
        String versionName=null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
}