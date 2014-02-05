/*
 * all shared preference 
 * FINISH_VIDEO
 * USER_ID
 * FINISH_VIDEO
 * LAST_TAB
 */


package com.paad.wifi4us;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.paad.wifi4us.utility.Constant;

public class VideoFreeActivity extends Activity {
	private boolean startFlag;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constant.FLAG.FINISH_VIDEO = true;
		Constant.FLAG.STATE_RECEIVE = true;
    	startFlag = true;

		setContentView(R.layout.activity_videofree);
		
		 Thread thread = new Thread(runnable);
		 thread.start();
    }
    
    public void StartMainActivity(View view){
    	if(startFlag){
    		startFlag = false;
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
            //ÑÓÊ±0.5Ãë
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            msg.obj = "MainActivity";
            mHandler.sendMessage(msg);
        }
        
    };
}
