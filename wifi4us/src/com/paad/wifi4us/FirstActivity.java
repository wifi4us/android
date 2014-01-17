/*
 * all shared preference 
 * FINISH_VIDEO
 * USER_ID
 * FINISH_VIDEO
 * LAST_TAB
 */


package com.paad.wifi4us;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.paad.wifi4us.utility.SharedPreferenceHelper;

public class FirstActivity extends Activity {
	private boolean startFlag;
	private Activity currentActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	currentActivity = this;
    	startFlag = true;
        super.onCreate(savedInstanceState);
        SharedPreferenceHelper sharedPreference = new SharedPreferenceHelper(getApplicationContext());
        sharedPreference.putBoolean("FINISH_VIDEO", true);
		sharedPreference.putBoolean("FINISH_PRECONNNECT", true);
        setContentView(R.layout.activity_first);
        
        Timer timer = new Timer();  
        TimerTask task = new TimerTask(){  
	        public void run() {  
	        	((FirstActivity)currentActivity).StartMainActivity(null);
	        }      
	    };
	    timer.schedule(task, 3000);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void StartMainActivity(View view)
    {
    	if(startFlag){
    		startFlag = false;
    		Intent intent = new Intent();
        	intent.setClass(this, MainActivity.class);
        	startActivity(intent);
    	}
    }
    
 
}
