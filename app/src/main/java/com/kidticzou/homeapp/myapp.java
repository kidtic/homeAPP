package com.kidticzou.homeapp;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.kidticzou.homeapp.model.Bill;
import com.kidticzou.homeapp.model.NetMsg;
import com.kidticzou.homeapp.model.SaveBill;
import com.kidticzou.homeapp.model.versionType;

public class myapp extends Application {
    public String url;
    public String user;
    public String passwd;

    //--------config
    public versionType remoteVersion;//服务器版本
    public versionType localVersion;//本地版本

    //是否默认是扣除余额的钱
    public boolean mChangMoneyF=true;
    //设置的项目
    public String[] mConfigItem ={"最新版本下载","关于"};
    //更新url
    public String mConfigUpdate="http://kidticzou.com:5000/sharing/fnfPcP4rp";



}
