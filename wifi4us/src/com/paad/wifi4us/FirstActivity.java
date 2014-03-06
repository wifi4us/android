/*
 * all shared preference 
 * FINISH_VIDEO
 * USER_ID
 * FINISH_VIDEO
 * LAST_TAB
 */


package com.paad.wifi4us;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.paad.wifi4us.utility.Constant;
import com.umeng.analytics.MobclickAgent;

public class FirstActivity extends Activity {
    private Activity currentActivity;
	private boolean startFlag;
	
	protected void onResume(){
		super.onResume();
		MobclickAgent.onPageStart("FirstActivity");
		MobclickAgent.onResume(this, Constant.UMENG_KEY, Constant.UMENG_CHANNEL);
	}
	
	public void onPause(){
		super.onPause();
		MobclickAgent.onPageEnd("FirstActivity");
		MobclickAgent.onPause(this);
	}

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
		 MobclickAgent.openActivityDurationTrack(false);
		 //MobclickAgent.setDebugMode(true);
		 PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY, "gxLMGxsKv6q3WRAKxBZwuidD");
		 /*
	             Resources resource = this.getResources();
	                String pkgName = this.getPackageName();
		 CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(                      getApplicationContext(),
	                        resource.getIdentifier("notification_custom_builder", "layout", pkgName), 
	                        resource.getIdentifier("notification_icon", "id", pkgName), 
	                        resource.getIdentifier("notification_title", "id", pkgName), 
	                        resource.getIdentifier("notification_text", "id", pkgName));
		        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
		        cBuilder.setLayoutDrawable(resource.getIdentifier("simple_notification_icon", "drawable", pkgName));
		        PushManager.setNotificationBuilder(this, 1, cBuilder);
		        */
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
                //��ת
            	StartMainActivity(null);
            }
        }
    };
    
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message msg = mHandler.obtainMessage();
            //��ʱ3��
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
