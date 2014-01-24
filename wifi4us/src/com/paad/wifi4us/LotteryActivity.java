/**
 * @(#)LotteryActivity.java, 2014-1-23. 
 * 
 * Copyright 2014 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.paad.wifi4us;

import com.paad.wifi4us.lottery.LotteryHistoryFragment;
import com.paad.wifi4us.lottery.LotteryPlateFragment;
import com.paad.wifi4us.lottery.UserInformationFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

/**
 *
 * @author yangshi
 *
 */
public class LotteryActivity extends ActionBarActivity implements TabListener{

    FragmentManager fragmentManager;
    
    public static final int TAG_NUM = 3;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);
        Log.d("LotteryActivity", "in onCreate");
        fragmentManager = this.getSupportFragmentManager();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        for (int i = 0; i < TAG_NUM; ++i) {
            Tab tab = actionBar.newTab().setText(tags[i]).setTabListener(this);
            actionBar.addTab(tab, i);
        }
    }
    
    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {}

    Fragment[] fragments = new Fragment[TAG_NUM];
    String[] tags = new String[]{"plate", "history", "userInfo"};
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        int pos = tab.getPosition();
        if(fragments[pos] == null){
            String name;
            switch(pos){
                case 0:
                    name = LotteryPlateFragment.class.getName();
                    break;
                case 1:
                    name = LotteryHistoryFragment.class.getName();
                    break;
                case 2:
                    name = UserInformationFragment.class.getName();
                    break;
                    default:
                        Log.e(this.getClass().getSimpleName(), "invalid pos");
                        return;
            }
            fragments[pos] =  Fragment.instantiate(this, name);
            Log.i("lottery", "add "+tab.getText());
            ft.add(R.id.activity_lottery, fragments[pos]);
        }else{
            ft.attach(fragments[pos]);
        }
        
    }
    
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        int pos = tab.getPosition();
        if(fragments[pos]!=null){
            ft.detach(fragments[pos]);
        }
    }

}
