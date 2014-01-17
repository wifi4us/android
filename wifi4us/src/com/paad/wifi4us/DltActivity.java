/**
 * @(#)DltActivity.java, 2014-1-15. 
 * 
 * Copyright 2014 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.paad.wifi4us;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 * @author yangshi
 *
 */
public class DltActivity extends Activity implements OnClickListener{

    int checkedRedBalls = 0;
    
    int checkedBlueBalls =0;
    
    TextView tv;
    
    long caipiaoCnt =-1;
    
    long creditPerCaipiao = 2;
    
    OnCheckedChangeListener redballListener = new OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            if(isChecked){
                ++checkedRedBalls;
            }else{
                --checkedRedBalls;
            }
            updatePrice();
            refreshTextView();
        }
        
    };
    OnCheckedChangeListener blueballListener = new OnCheckedChangeListener(){
        
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            if(isChecked){
                ++checkedBlueBalls;
            }else{
                --checkedBlueBalls;
            }
            updatePrice();
            refreshTextView();
        }
        
    };
    
    protected void updatePrice(){
        if(checkedRedBalls>=6&&checkedBlueBalls>=1){
            caipiaoCnt = cxy(checkedBlueBalls,1)*cxy(checkedRedBalls,6);
        }else{
            caipiaoCnt = -1;
        }
    }

    long cxy(int x, int y) {
        long num = 1;
        int n = 0;
        while (n < y) {
            num *= x--;
            ++n;
        }
        while(y>1){
            num/=y--;
        }
        return num;
    }

    protected void refreshTextView() {
        if (tv != null) {
            if (caipiaoCnt > 0) {
                tv.setText("兑换" + caipiaoCnt + "注 需" + caipiaoCnt*creditPerCaipiao + "积分");
                confirmButton.setClickable(true);
            } else {
                tv.setText("请至少选择6个红球一个篮球");
                confirmButton.setClickable(false);
            }
        }
    }
    int numRedBall = 33;
    int numBlueBall = 16;
    int minRedBall = 6;
    int minBlueBall = 1;
    int col = 7;
    List<CheckBox> redTbs = new ArrayList<CheckBox>();
    List<CheckBox> blueTbs = new ArrayList<CheckBox>();
    Button confirmButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlt);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        
        tv = (TextView)findViewById(R.id.dlt_text);
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout redlayout = (LinearLayout)findViewById(R.id.dlt_red);
        while(true) {
            if (redTbs.size() >= numRedBall) {
                break;
            }
            LinearLayout ll = new LinearLayout(this);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);
            redlayout.addView(ll, lp);
            for (int j = 0; j < col; ++j) {
                if (redTbs.size() >= numRedBall) {
                    CheckBox tb = (CheckBox)inflater.inflate(R.layout.checkbox_redball, null);
                    tb.setVisibility(View.INVISIBLE);
                    ll.addView(tb);
                    continue;
                }
                CheckBox tb = (CheckBox)inflater.inflate(R.layout.checkbox_redball, null);
                tb.setOnCheckedChangeListener(redballListener);
                tb.setText((redTbs.size()+1)+ "");
                tb.setChecked(false);
                ll.addView(tb);
                redTbs.add(tb);
            }
        }
        
        LinearLayout bluelayout = (LinearLayout)findViewById(R.id.dlt_blue);
        
        while(true) {
            if (blueTbs.size() >= numBlueBall) {
                break;
            }
            LinearLayout ll = new LinearLayout(this);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);
            bluelayout.addView(ll, lp);
            for (int j = 0; j < col; ++j) {
                if (blueTbs.size() >= numBlueBall) {
                    CheckBox tb = (CheckBox)inflater.inflate(R.layout.checkbox_blueball, null);
                    tb.setVisibility(View.INVISIBLE);
                    ll.addView(tb);
                    continue;
                }
                CheckBox tb = (CheckBox)inflater.inflate(R.layout.checkbox_blueball, null);
                tb.setOnCheckedChangeListener(blueballListener);
                tb.setText((blueTbs.size()+1)+ "");
                tb.setChecked(false);
                ll.addView(tb);
                blueTbs.add(tb);
            }
        }
        confirmButton = (Button)findViewById(R.id.dlt_confirm_btn);
        confirmButton.setOnClickListener(this);
        refreshTextView();
    }

    String buildConfirmInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("您选择的号码是：\n红球：");
        for(int i = 0;i<redTbs.size();++i){
            if(redTbs.get(i).isChecked()){
                sb.append(i+1);
                sb.append(" ");
            }
        }
        sb.append("\n蓝球：");
        for(int i = 0;i<blueTbs.size();++i){
            if(blueTbs.get(i).isChecked()){
                sb.append(i+1);
                sb.append(" ");
            }
        }
        sb.append("\n共"+caipiaoCnt+"注，需要"+caipiaoCnt*creditPerCaipiao+"积分。");
        return sb.toString();
    }
    @Override
    public void onClick(View arg0) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(buildConfirmInfo());
        builder.setTitle("购买确认");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
