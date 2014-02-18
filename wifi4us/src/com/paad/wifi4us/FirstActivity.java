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
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;

import com.paad.wifi4us.utility.Constant;

public class FirstActivity extends Activity {
    private Activity currentActivity;
	private boolean startFlag;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
    	startFlag = true;

        Constant.FLAG.FINISH_PRECONNNECT = true;
		Constant.FLAG.FINISH_VIDEO = true;
		setContentView(R.layout.activity_first);
		
		 Thread thread = new Thread(runnable);
		 thread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void StartMainActivity(View view){
    	if(startFlag){
    		startFlag = false;
    		Intent intent = new Intent();
    		intent.setClass(currentActivity, MainActivity.class);
    		startActivity(intent); 
    		finish();
    	}
    }
    
    private Handler mHandler = new Handler(){
        
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            if(((String)msg.obj).equals("MainActivity")) {
                //Ìø×ª
            	StartMainActivity(null);
            }
        }
    };
    
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message msg = mHandler.obtainMessage();
            //ÑÓÊ±3Ãë
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            msg.obj = "MainActivity";
            mHandler.sendMessage(msg);
        }
        
    };
}
