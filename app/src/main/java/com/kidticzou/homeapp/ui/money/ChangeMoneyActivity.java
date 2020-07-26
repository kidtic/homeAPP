package com.kidticzou.homeapp.ui.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kidticzou.homeapp.R;
import com.kidticzou.homeapp.model.Bill;
import com.kidticzou.homeapp.model.NetMsg;
import com.kidticzou.homeapp.myapp;

public class ChangeMoneyActivity extends AppCompatActivity implements NetMsg.ServerReturn {
    private EditText mEditChangeMoney;
    private EditText mEidtPs;
    private Button mBtnDiv;
    private Button mBtnConmmit;
    private NetMsg mNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_money);
        mEditChangeMoney=findViewById(R.id.et_changmoney);
        mEidtPs=findViewById(R.id.et_ps);
        mBtnDiv=findViewById(R.id.btn_div2);
        mBtnConmmit=findViewById(R.id.btn_moneycommit);
        myapp appdata= (myapp) getApplication();
        mNet=new NetMsg(ChangeMoneyActivity.this,appdata.url,2333,appdata.user,appdata.passwd);


        mBtnConmmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float changemoney=Float.parseFloat(mEditChangeMoney.getText().toString());
                String ps=mEidtPs.getText().toString();
                mNet.changeMoney(changemoney,ps,false);
            }
        });
        mBtnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int changemoney=Integer.parseInt(mEditChangeMoney.getText().toString());
                changemoney=changemoney/2;
                mEditChangeMoney.setText(String.valueOf(changemoney));

            }
        });
    }

    @Override
    public void loginhome() {

    }

    @Override
    public void errorLog(String errString) {
        Looper.prepare();
        Toast.makeText(this,errString,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void returnBill(Bill[] data) {

    }

    @Override
    public void home() {
        setResult(RESULT_OK);
        finish();
    }


    //键盘隐藏


    @Override
    public boolean dispatchTouchEvent(MotionEvent  ev) {
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