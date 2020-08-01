package com.kidticzou.homeapp.model;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.kidticzou.homeapp.ui.money.MoneyFragment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class NetMsg {
    String servername="localhost";
    int port=2333;
    String passwd;
    String user;
    //SOCKET
    Socket client;
    DataOutputStream out;
    BufferedReader in;

    //外部接口对接
    private ServerReturn mServRet;



    int postcount=0;

    public NetMsg(Object context, String servername, int port, String user, String passwd){
        mServRet= (ServerReturn) context;
        this.servername=servername;
        this.port=port;
        this.user=user;
        this.passwd=passwd;

        if(this.servername==null||this.user==null||this.passwd==null){
            System.out.println("参数错误");
        }
    }

    private void Connect() throws IOException {
        client=new Socket(servername,port);
        out=new DataOutputStream(client.getOutputStream());
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }
    private void closeConnect() throws IOException{
        out.close();
        client.close();
    }



    //-------加密方法
    /**
     * 字符串转换成十六进制字符串
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     *
     * 十六进制转换字符串
     */

    public static byte[] hexStr2Bytes(String hexStr) {
        System.out.println("in len :" + hexStr.length());
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        System.out.println("out len :" + bytes.length);
        System.out.println("ddd" + Arrays.toString(bytes));
        return bytes;
    }

    /**
     * bytes转换成十六进制字符串
     */
    public static String byte2HexStr(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            // if (n<b.length-1) hs=hs+":";
        }
        return hs.toUpperCase();
    }

    public static String encrypt(String key, String initVector, String value) {

        if(key.length()>16){
            System.out.println("pqssword length too big");
            System.out.println(key);
            return "";
        }
        int len=key.length();
        if(key.length()<16) {
            for (int i = 0; i < 16 - len; i++) {
                key += "0";
            }
        }

        try {
            //System.out.println("key:\t" + Arrays.toString(key.getBytes("UTF-8")));
            //System.out.println("iv:\t" + Arrays.toString(initVector.getBytes("UTF-8")));
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            //System.out.println(Arrays.toString(encrypted));

            return new String( Base64.getEncoder().encode(encrypted));//通过Base64转码返回
            //return byte2HexStr(encrypted);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        if(key.length()>16){
            System.out.println("pqssword length too big");
            System.out.println(key);
            return "";
        }
        int len=key.length();
        if(key.length()<16) {
            for (int i = 0; i < 16 - len; i++) {
                key += "0";
            }
        }

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted.getBytes()));
            //byte[] original = cipher.doFinal(hexStr2Bytes(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    //-------------------------------------------------------------
    //                      发送请求
    //-------------------------------------------------------------
    /**
     * 回响接口
     */
    public interface ServerReturn{
        //成功登录到主界面
        void loginhome();
        //报错
        void errorLog(String errString);
        void returnBill(Bill[] data);
        void returnSaveBill(SaveBill[] data);
        //
        void home();
    }

    /**
     * 用于登录确认，对服务器
     * 发送确认消息等待服务器回应
     */
    public void loginServer(){
        //-----转换json命令
        JSONObject pt=new JSONObject();
        pt.put("head","request");
        pt.put("part","basic");
        pt.put("func","login");
        pt.put("user",user);
        JSONObject datajson=new JSONObject();
        datajson.put("time",new Date().toString());
        pt.put("data",datajson);
        String outjson=pt.toJSONString();
        //--加密
        final String encryptdata=encrypt(passwd,"0000000000000000",outjson);

        //--发送（考虑需要多线程）
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connect();
                    out.writeUTF(encryptdata);

                    String str = in.readLine();
                    String resstr=decrypt(passwd,"0000000000000000",str);
                    System.out.println(resstr);
                    if(resstr==null){
                        mServRet.errorLog("密码错误");
                    }
                    //解析返回
                    JSONObject res=JSON.parseObject(resstr);
                    if(res.getString("func").equals("login")){
                        if(res.getString("data").equals("ok")){
                            System.out.println("loginhome");
                            mServRet.loginhome();
                        }
                        else if(res.getString("data").equals("no user")){
                            System.out.println("用户名不存在");
                            mServRet.errorLog("用户名不存在");
                        }
                    }

                    closeConnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    mServRet.errorLog("无法连接服务器，或密码错误");

                }
            }
        }).start();

    }

    /**
     * 返回账单明细
     */
    public void returnBill() throws InterruptedException {
        //-----转换json命令
        JSONObject pt=new JSONObject();
        pt.put("head","request");
        pt.put("part","pay");
        pt.put("func","return");
        pt.put("user",user);
        pt.put("time",new Date().toString());
        String outjson=pt.toJSONString();

        //--加密
        final String encryptdata=encrypt(passwd,"0000000000000000",outjson);

        //--发送（考虑需要多线程）
        Thread t= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connect();
                    out.writeUTF(encryptdata);

                    String str = in.readLine();
                    String resstr=decrypt(passwd,"0000000000000000",str);
                    System.out.println(resstr);
                    if(resstr==null){
                        mServRet.errorLog("密码错误");
                    }
                    //解析返回
                    JSONObject res=JSON.parseObject(resstr);

                    if(res.getString("func").equals("return")&&res.getString("part").equals("pay")){
                        JSONArray billdatajson=res.getJSONArray("data");
                        int len=billdatajson.size();
                        Bill[] billdata=new Bill[len];
                        for(int i=0;i<len;i++){
                            JSONObject cashe= (JSONObject) billdatajson.get(i);
                            //解析日期
                            SimpleDateFormat ft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date time=ft.parse(cashe.getString("time"));
                            //解析余额money
                            float money=Float.parseFloat(cashe.getString("money"));
                            //解析改变余额
                            float moneychange=Float.parseFloat(cashe.getString("moneychange"));
                            //解析ps
                            String ps=cashe.getString("ps");
                            //解析star
                            boolean star=cashe.getString("star").equals("true");
                            //准备账单
                            billdata[i] = new Bill(time,moneychange,money,ps,star);

                        }
                        mServRet.returnBill(billdata);
                    }

                    closeConnect();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    mServRet.errorLog("无法连接服务器，或密码错误");

                }
            }
        });
        t.start();
        t.join();

    }

    /**
     * 改变余额
     */
    public void changeMoney(float changmoney,String ps,boolean star){
        //-----转换json命令
        JSONObject pt=new JSONObject();
        pt.put("head","request");
        pt.put("part","pay");
        pt.put("func","change");
        pt.put("user",user);
        pt.put("time",new Date().toString());
        JSONObject datajson=new JSONObject();
        datajson.put("changeNumber",changmoney);
        datajson.put("ps",ps);
        datajson.put("star",star);
        pt.put("data",datajson);
        String outjson=pt.toJSONString();

        //--加密
        final String encryptdata=encrypt(passwd,"0000000000000000",outjson);

        //--发送（考虑需要多线程）
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connect();
                    out.writeUTF(encryptdata);

                    String str = in.readLine();
                    String resstr=decrypt(passwd,"0000000000000000",str);
                    System.out.println(resstr);
                    if(resstr==null){
                        mServRet.errorLog("密码错误");
                    }
                    //解析返回
                    JSONObject res=JSON.parseObject(resstr);
                    if(res.getString("func").equals("change")&&res.getString("part").equals("pay")){
                        JSONObject datajson=res.getJSONObject("data");
                        String result=datajson.getString("result");
                        if(result=="error"){
                            mServRet.errorLog("改动失败");
                        }
                        else {
                            mServRet.home();
                        }
                    }

                    closeConnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    mServRet.errorLog("无法连接服务器，或密码错误");

                }
            }
        }).start();

    }

    /**
     * 返回万元计划存储单
     */
    public void returnSaveBill() throws InterruptedException {
        //-----转换json命令
        JSONObject pt=new JSONObject();
        pt.put("head","request");
        pt.put("part","save");
        pt.put("func","return");
        pt.put("user",user);
        pt.put("time",new Date().toString());
        String outjson=pt.toJSONString();

        //--加密
        final String encryptdata=encrypt(passwd,"0000000000000000",outjson);

        //--发送（考虑需要多线程）
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connect();
                    out.writeUTF(encryptdata);

                    String str = in.readLine();
                    String resstr=decrypt(passwd,"0000000000000000",str);
                    System.out.println(resstr);
                    if(resstr==null){
                        mServRet.errorLog("密码错误");
                    }
                    //解析返回
                    JSONObject res=JSON.parseObject(resstr);

                    if(res.getString("func").equals("return")&&res.getString("part").equals("save")){
                        JSONArray billdatajson=res.getJSONArray("data");
                        int len=billdatajson.size();
                        SaveBill[] billdata=new SaveBill[len];
                        for(int i=0;i<len;i++){
                            JSONObject cashe= (JSONObject) billdatajson.get(i);
                            //解析日期
                            SimpleDateFormat ft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date time=ft.parse(cashe.getString("time"));
                            //解析savemoney
                            float money=Float.parseFloat(cashe.getString("money"));
                            //解析改变savemoney
                            float moneychange=Float.parseFloat(cashe.getString("moneychange"));
                            //解析target
                            float target=Float.parseFloat(cashe.getString("target"));
                            //解析改变target
                            float targetchange=Float.parseFloat(cashe.getString("targetchange"));
                            //解析ps
                            String ps=cashe.getString("ps");
                            //解析star
                            boolean star=cashe.getString("star").equals("true");
                            //准备账单
                            billdata[i] = new SaveBill(time,target,money,targetchange,moneychange,ps,star);

                        }
                        mServRet.returnSaveBill(billdata);
                    }

                    closeConnect();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    mServRet.errorLog("无法连接服务器，或密码错误");

                }
            }
        });
        t.start();
        t.join();


    }





}
