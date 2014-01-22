package com.paad.wifi4us;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.paad.wifi4us.utility.Constant;

public class VideoActivity extends Activity {
	
	private Button button_sound;
	private Button button_interest;
	private TextView counttime;
	private MyVideoView video;
	private MediaController controller;
	private String adid;
	private String adword;

	public static WifiDisconnectWrongReceiver wifiDisconnectReceiver;
	private Activity currentActivity;
	
    //Receive Service 	
    private ReceiveService receiveService;
	private boolean haveBondService;
	private ServiceConnection sc = new ServiceConnection() {
        @Override  
        public void onServiceDisconnected(ComponentName arg0) {  
        	haveBondService = false;
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder binder) {
        	receiveService = ((ReceiveService.MyBinder)binder).getService();
        	haveBondService = true;
        }  
    };  
	
	public void onDestroy(){
		super.onDestroy();
		unbindService(sc);
	}
	
	
    public void onPause() {  
    	super.onPause();
    	if(!haveBondService){
    		return;
    	}
		if(!Constant.FLAG.FINISH_VIDEO){
			Intent intent = new Intent();
			intent.setAction(Constant.BroadcastReceive.CONMUNICATION_SETUP_INTERRUPT); 
			sendBroadcast(intent);
			finish();
			receiveService.WifiDisconnectCompletely();
		}
    }  
    
    public void onStop(){
    	super.onStop();
    	try{
    		getApplicationContext().unregisterReceiver(wifiDisconnectReceiver);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
		Intent intent = new Intent(this, ReceiveService.class);  
        //bind service to get ready for all the clickable element
		bindService(intent, sc, Context.BIND_AUTO_CREATE); 
        setContentView(R.layout.activity_video);
        adid = getIntent().getStringExtra("adid");
        adword = getIntent().getStringExtra("adword");
        currentActivity = this;
        wifiDisconnectReceiver = new WifiDisconnectWrongReceiver();
		getApplicationContext().registerReceiver(wifiDisconnectReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));

        
        button_sound = (Button)findViewById(R.id.receive_button_ad_sound_button);
        button_sound.getBackground().setAlpha(100);
        button_sound.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
	            Toast.makeText(VideoActivity.this, "sound click", Toast.LENGTH_SHORT).show();
			}
        });
        
        button_interest = (Button)findViewById(R.id.receive_button_ad_interest_button);
        button_interest.setText(adword);
        button_interest.getBackground().setAlpha(100);
        button_interest.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
	            Toast.makeText(VideoActivity.this, "interest click", Toast.LENGTH_SHORT).show();
			}
        });
        
        
        counttime = (TextView)findViewById(R.id.receive_text_ad_timecount);  
        MyCount mc = new MyCount(30000, 1000);  
        mc.start();  
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        video = (MyVideoView)findViewById(R.id.videoview);
        video.SetCurrentState(this,new DisplayMetrics());
        controller = new MediaController(this);
        controller.setVisibility(View.INVISIBLE); 
        video.setMediaController(controller);
        video.setVideoPath(getApplicationContext().getCacheDir().toString() + "/ad_" + adid + ".3gp");
        video.start();
        
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
        	public void onCompletion(MediaPlayer mp){
        		Constant.FLAG.FINISH_VIDEO = true;
        		Constant.FLAG.STATE_RECEIVE = true;
        		finish();
        	}
        });
    }
    
	public class WifiDisconnectWrongReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent) {
			if(!haveBondService)
				return;
			//get reward for receiving
			SupplicantState state = (SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			if(state.equals(SupplicantState.INACTIVE)){ 
				Intent i = new Intent();
				i.setAction(Constant.BroadcastReceive.CONMUNICATION_SETUP_INTERRUPT); 
				sendBroadcast(i);
				receiveService.WifiDisconnectCompletely();
				currentActivity.finish();
				c.unregisterReceiver(this);
			}
		}
	}
	
    public class MyCount extends CountDownTimer {     
        public MyCount(long millisInFuture, long countDownInterval) {     
            super(millisInFuture, countDownInterval);     
        }     
        @Override     
        public void onFinish() {     
        	counttime.setText("Íê³É");        
        }     
        @Override     
        public void onTick(long millisUntilFinished) {     
        	counttime.setText(Long.toString(millisUntilFinished / 1000));     
        }    
    }     
}
