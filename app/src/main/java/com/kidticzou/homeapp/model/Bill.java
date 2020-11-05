package com.kidticzou.homeapp.model;

import java.util.Date;

public class Bill extends Object{
    public Date time;
    public float moneychange;
    public float money;
    public String ps;
    public boolean star;
    Bill(){};
    Bill( Date time,float moneychange,float money,String ps,boolean star){
        this.time=time;
        this.money=money;
        this.moneychange=moneychange;
        this.ps=ps;
        this.star=star;
    }
}
