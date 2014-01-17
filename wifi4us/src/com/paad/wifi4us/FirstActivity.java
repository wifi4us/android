/*
 * all shared preference 
 * FINISH_VIDEO
 * USER_ID
 * FINISH_VIDEO
 * LAST_TAB
 */


package com.paad.wifi4us;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.paad.wifi4us.utility.PasswdUtil;
import com.paad.wifi4us.utility.SharedPreferenceHelper;

public class FirstActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferenceHelper sharedPreference = new SharedPreferenceHelper(getApplicationContext());
        sharedPreference.putBoolean("FINISH_VIDEO", true);
		sharedPreference.putBoolean("FINISH_PRECONNNECT", true);
        setContentView(R.layout.activity_first);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void StartMainActivity(View view)
    {

    	Intent intent = new Intent();
    	intent.setClass(this, MainActivity.class);
    	startActivity(intent);
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
    
 
}
