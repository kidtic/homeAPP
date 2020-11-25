package com.kidticzou.homeapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Trace;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kidticzou.homeapp.model.NetMsg;
import com.kidticzou.homeapp.ui.login.LoginActivity;
import com.kidticzou.homeapp.ui.money.BillActivity;
import com.kidticzou.homeapp.ui.money.BillAdapter;
import com.kidticzou.homeapp.ui.money.SaveBillActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MainActivity extends AppCompatActivity implements NetMsg.ServerReturn {
    private NetMsg mNet;
    private myapp appdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_money, R.id.navigation_config)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //初始化
        appdata=(myapp) getApplication();
        //--------------开辟新线程任务
        Thread tTimeTaskRun=new Thread(new TimeTaskRun());
        tTimeTaskRun.start();


    }

    @Override
    public void errorLog(final String errString) {
        //弹窗
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,errString,Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void home() {
        //跳转到登录页面，并清除历史跳转
        Intent intent =new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //定时任务--发送请求，得到服务器任务列表
    public class TimeTaskRun implements Runnable{
        @Override
        public void run() {
            //初始化
            mNet=new NetMsg(MainActivity.this,appdata.url,2333,appdata.user,appdata.passwd);
            while(true){
                //睡眠
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //得到任务列表
                JSONObject taskList;
                synchronized (this){
                    taskList=mNet.BasicReturn();
                }

                //查看连接是否正常,处理不正常的返回
                if(taskList.getString("result")=="error can't connect server"){
                    //无法连接服务器直接退出
                    errorLog("无法连接服务器");
                    home();
                    break;
                }
                else if(taskList.getString("result").charAt(0)=='e'){
                    errorLog(taskList.getString("result"));
                }

                //查看是否有任务
                System.out.print("time run");
            }
        }
    }

}