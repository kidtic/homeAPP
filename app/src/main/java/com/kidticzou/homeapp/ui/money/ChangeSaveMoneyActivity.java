package com.kidticzou.homeapp.ui.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.model.NetMsg;
import com.kidticzou.homeapp.myapp;

public class ChangeSaveMoneyActivity extends AppCompatActivity implements NetMsg.ServerReturn{

    private EditText mEditChangeMoney;
    private EditText mEidtPs;
    private Button mBtnConmmit;
    private RadioGroup mChangRadio;
    private RadioButton mRbtn_f;//扣款
    private RadioButton mRbtn_p;//存款
    private boolean mChangRadioF_bool;//是选的哪一个，实时更新。
    private NetMsg mNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_save_money);

        mEditChangeMoney=findViewById(R.id.et_changmoney);
        mEidtPs=findViewById(R.id.et_ps);
        mBtnConmmit=findViewById(R.id.btn_moneycommit);
        mChangRadio=findViewById(R.id.rg_1);
        mRbtn_f=findViewById(R.id.rb_f);
        mRbtn_p=findViewById(R.id.rb_p);
        myapp appdata= (myapp) getApplication();
        mNet=new NetMsg(ChangeSaveMoneyActivity.this,appdata.url,2333,appdata.user,appdata.passwd);
        //查看默认扣款还是还款
        if(!appdata.mChangMoneyF){
            mRbtn_f.setChecked(true);
            mEditChangeMoney.setTextColor(Color.rgb(0,255,0));
            mChangRadioF_bool=true;
        }
        else {
            mRbtn_p.setChecked(true);
            mEditChangeMoney.setTextColor(Color.rgb(255,0,0));
            mChangRadioF_bool=false;
        }

        //扣还是加，监听事件
        mChangRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.rb_f){
                    mEditChangeMoney.setTextColor(Color.rgb(0,255,0));
                    mChangRadioF_bool=true;

                }
                else if (i==R.id.rb_p){
                    mEditChangeMoney.setTextColor(Color.rgb(255,0,0));
                    mChangRadioF_bool=false;
                }
            }
        });

        //提交按键，开辟线程提交
        mBtnConmmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        float changemoney;String ps;
                        synchronized (this){
                            changemoney=Float.parseFloat(mEditChangeMoney.getText().toString());
                            if(mChangRadioF_bool){
                                changemoney=(-1)*changemoney;
                            }
                            ps=mEidtPs.getText().toString();
                        }

                        mNet.SaveChangeMoney(changemoney,ps,false);
                    }
                }).start();
            }
        });


    }

    @Override
    public void errorLog(String errString) {
        Looper.prepare();
        Toast.makeText(this,errString,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void home() {
        setResult(RESULT_OK);
        finish();
    }

    //键盘隐藏
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isShouldHideInput(view, ev)) {
                InputMethodManager Object = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (Object != null) {
                    Object.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    //判断是否隐藏键盘
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}