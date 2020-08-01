package com.kidticzou.homeapp.model;

import java.util.Date;

public class SaveBill {
    public Date time;
    public float target;
    public float money;
    public float targetchange;
    public float moneychange;
    public String ps;
    public boolean star;
    SaveBill(){};
    SaveBill( Date time,float target,float money,float targetchange,float moneychange,String ps,boolean star){
        this.time=time;
        this.target=target;
        this.money=money;
        this.moneychange=moneychange;
        this.targetchange=targetchange;
        this.ps=ps;
        this.star=star;
    }
}