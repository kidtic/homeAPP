package com.kidticzou.homeapp.model;

/**
 * 版本号解析
 */
public class versionType{
    public int gen;   //代号，最大版本号，（第x代）
    public int bigversion;   //大版本
    public int smversion;    //小版本
    public versionType(String versionstr){
        String[] spstr = versionstr.split("\\.");
        if(spstr.length!=3){
            System.out.println("error:版本号格式错误");
            return;
        }

        gen= Integer.parseInt(spstr[0]);
        bigversion=Integer.parseInt(spstr[1]);
        smversion=Integer.parseInt(spstr[2]);

    }
}