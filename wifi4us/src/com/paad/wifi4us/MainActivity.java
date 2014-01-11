package com.paad.wifi4us;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.paad.wifi4us.utility.SharedPreferenceHelper;

public class MainActivity extends FragmentActivity {
	private FragmentManager fragmentManager;
    
	private Fragment receive_wifi_switcher_button;
	private Fragment receive_id_switcher_text_on;
	private Fragment receive_id_switcher_text_off;
	private Fragment receive_id_start_scan_button;
	private Fragment receive_id_start_scan_resultlist;
	private Fragment receive_id_start_scan_progressbar;
	private Fragment receive_id_start_connect_progressbar;
	private Fragment receive_id_start_scan_text_openwifi;
	private Fragment receive_id_start_wifi_connected_state;

	private Fragment send_id_start_share_button;
	private Fragment send_id_progressbar;
	private Fragment send_id_stop_share_button;
	private Fragment send_id_start_share_text;

	private Fragment send;
	private Fragment receive;
	private Fragment other;
	
	private SharedPreferenceHelper sharedPreference;
	
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
    
    private void addTab(String label, String titleText, int drawableId, int contentId, TabHost tabHost) {
        TabHost.TabSpec spec = tabHost.newTabSpec(label);
         
        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_card, tabHost.getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.tabtitle);
        title.setText(titleText);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.tabimage);
        icon.setImageResource(drawableId);
         
        spec.setIndicator(tabIndicator);
        spec.setContent(contentId);
        tabHost.addTab(spec);
    }

	
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
    	sharedPreference = new SharedPreferenceHelper(getApplicationContext());
    	sharedPreference.putBoolean("STATE_RECEIVE", false);

    	
		fragmentManager = this.getSupportFragmentManager();
		Intent intent = new Intent(this, ReceiveService.class);  
		bindService(intent, sc, Context.BIND_AUTO_CREATE); 

    	
		startService(new Intent(this, ReceiveService.class));
		startService(new Intent(this, SendService.class));

    	setContentView(R.layout.activity_main);
    	
    	TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost); 
    	
    	
    	tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {    
            public void onTabChanged(String tabId) {  
            	String lastTabId = sharedPreference.getString("LAST_TAB");

            	if(tabId.equals("receive")){
            		if(lastTabId.equals("send")){
                		StopSendFragment();
            		}else{
                		StopOtherFragment();
            		}
            		StartReceiveFragment();
            	}else if(tabId.equals("send")){
            		if(lastTabId.equals("other")){
                		StopOtherFragment();
            		}else{
                		StopReceiveFragment();
            		}
            		StartSendFragment();
            	}else{
            		if(lastTabId.equals("receive")){
                		StopReceiveFragment();
            		}else{
                		StopSendFragment();
            		}
            		StartOtherFragment();
            	}
            }  
        }); 
    	
    	tabHost.setup();
    	
    	addTab("receive", "使用",  R.drawable.tab_search_selector, R.id.receive, tabHost);
    	addTab("send", "分享",  R.drawable.tab_share_selector, R.id.send, tabHost);
    	addTab("other", "其他",  R.drawable.tab_settings_selector, R.id.other, tabHost);
    	tabHost.setCurrentTab(0);
    	
    }
	
	public void onDestroy(){
		super.onDestroy();
		unbindService(sc);
	}
    public void onBackPressed() {  
    	if(!haveBondService){
    		return;
    	}
		Boolean finishVideo = sharedPreference.getBoolean("FINISH_VIDEO");
		if(!finishVideo){
			receiveService.WifiDisconnectCompletely();
		}
        Intent backtoHome = new Intent(Intent.ACTION_MAIN);
        backtoHome.addCategory(Intent.CATEGORY_HOME);
        backtoHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backtoHome);  
    }  
    
    private void StartReceiveFragment(){
    	sharedPreference.putString("LAST_TAB", "receive");
    	
    	initFragments();
    	if(receive != null){
        	receive.onStart();
        	receive.onResume();
    	}
    	if(receive_wifi_switcher_button != null){
    		receive_wifi_switcher_button.onStart();
    		receive_wifi_switcher_button.onResume();
    	}
    	if(receive_id_switcher_text_on != null){
        	receive_id_switcher_text_on.onStart();
        	receive_id_switcher_text_on.onResume();
    	}
    	if(receive_id_switcher_text_off != null){
        	receive_id_switcher_text_off.onStart();
        	receive_id_switcher_text_off.onResume();
    	}
    	if(receive_id_start_scan_button != null){
        	receive_id_start_scan_button.onStart();
        	receive_id_start_scan_button.onResume();
    	}
    	if(receive_id_start_scan_resultlist != null){
        	receive_id_start_scan_resultlist.onStart();
        	receive_id_start_scan_resultlist.onResume();
    	}
    	if(receive_id_start_scan_progressbar != null){
        	receive_id_start_scan_progressbar.onStart();
        	receive_id_start_scan_progressbar.onResume();
    	}
    	if(receive_id_start_connect_progressbar != null){
        	receive_id_start_connect_progressbar.onStart();
        	receive_id_start_connect_progressbar.onResume();
    	}
    	if(receive_id_start_scan_text_openwifi != null){
        	receive_id_start_scan_text_openwifi.onStart();
        	receive_id_start_scan_text_openwifi.onResume();
    	}
    	if(receive_id_start_wifi_connected_state != null){
        	receive_id_start_wifi_connected_state.onStart();
        	receive_id_start_wifi_connected_state.onResume();
    	}
    }
    
    private void StartSendFragment(){
    	sharedPreference.putString("LAST_TAB", "send");
		
    	initFragments();
       	if(send != null){
       		send.onStart();
       		send.onResume();
    	}
    	if(send_id_start_share_button != null){
    		send_id_start_share_button.onStart();
    		send_id_start_share_button.onResume();
    	}
    	if(send_id_progressbar != null){
    		send_id_progressbar.onStart();
    		send_id_progressbar.onResume();
    	}
    	if(send_id_stop_share_button != null){
    		send_id_stop_share_button.onStart();
    		send_id_stop_share_button.onResume();
    	}
    	if(send_id_start_share_text != null){
    		send_id_start_share_text.onStart();
    		send_id_start_share_text.onResume();
    	}

    }
    
    private void StartOtherFragment(){
    	sharedPreference.putString("LAST_TAB", "other");
		
    	initFragments();
       	if(other != null){
       		other.onStart();
       		other.onResume();
       	}
    }
    
    private void StopReceiveFragment(){
    	initFragments();
    	if(receive != null){
    		receive.onPause();
    		receive.onStop();
    	}
    	if(receive_wifi_switcher_button != null){
    		receive_wifi_switcher_button.onPause();
    		receive_wifi_switcher_button.onStop();
    	}
    	if(receive_id_switcher_text_on != null){
        	receive_id_switcher_text_on.onPause();
        	receive_id_switcher_text_on.onStop();
    	}
    	if(receive_id_switcher_text_off != null){
        	receive_id_switcher_text_off.onPause();
        	receive_id_switcher_text_off.onStop();
    	}
    	if(receive_id_start_scan_button != null){
        	receive_id_start_scan_button.onPause();
        	receive_id_start_scan_button.onStop();
    	}
    	if(receive_id_start_scan_resultlist != null){
        	receive_id_start_scan_resultlist.onPause();
        	receive_id_start_scan_resultlist.onStop();
    	}
    	if(receive_id_start_scan_progressbar != null){
        	receive_id_start_scan_progressbar.onPause();
        	receive_id_start_scan_progressbar.onStop();
    	}
    	if(receive_id_start_connect_progressbar != null){
        	receive_id_start_connect_progressbar.onPause();
        	receive_id_start_connect_progressbar.onStop();
    	}
    	if(receive_id_start_scan_text_openwifi != null){
        	receive_id_start_scan_text_openwifi.onPause();
        	receive_id_start_scan_text_openwifi.onStop();
    	}
    	if(receive_id_start_wifi_connected_state != null){
        	receive_id_start_wifi_connected_state.onPause();
        	receive_id_start_wifi_connected_state.onStop();
    	}
    }
    
    private void StopSendFragment(){
    	initFragments();
    	if(send != null){
    		send.onPause();
    		send.onStop();
    	}
    	if(send_id_start_share_button != null){
    		send_id_start_share_button.onPause();
    		send_id_start_share_button.onStop();
    	}
    	if(send_id_progressbar != null){
    		send_id_progressbar.onPause();
    		send_id_progressbar.onStop();
    	}
    	if(send_id_stop_share_button != null){
    		send_id_stop_share_button.onPause();
    		send_id_stop_share_button.onStop();
    	}
    	if(send_id_start_share_text != null){
    		send_id_start_share_text.onPause();
    		send_id_start_share_text.onStop();
    	}
    }
    
    private void StopOtherFragment(){
    	initFragments();
       	if(other != null){
       		other.onPause();
       		other.onStop();
       	}
    }
    
    private void initFragments(){
    	receive_wifi_switcher_button = fragmentManager.findFragmentByTag("receivie_wifi_switcher_button");
    	receive_id_switcher_text_on = fragmentManager.findFragmentByTag("receive_id_switcher_text_on");
    	receive_id_switcher_text_off = fragmentManager.findFragmentByTag("receive_id_switcher_text_off");
    	receive_id_start_scan_button = fragmentManager.findFragmentByTag("receive_id_start_scan_button");
    	receive_id_start_scan_resultlist = fragmentManager.findFragmentByTag("receive_id_start_scan_resultlist");
    	receive_id_start_scan_progressbar = fragmentManager.findFragmentByTag("receive_id_start_scan_progressbar");
    	receive_id_start_connect_progressbar = fragmentManager.findFragmentByTag("receive_id_start_connect_progressbar");
    	receive_id_start_scan_text_openwifi = fragmentManager.findFragmentByTag("receive_id_start_scan_text_openwifi");
    	receive_id_start_wifi_connected_state = fragmentManager.findFragmentByTag("receive_id_start_wifi_connected_state");

    	send_id_start_share_button = fragmentManager.findFragmentByTag("send_id_start_share_button");
    	send_id_progressbar = fragmentManager.findFragmentByTag("send_id_progressbar");
    	send_id_stop_share_button = fragmentManager.findFragmentByTag("send_id_stop_share_button");
    	send_id_start_share_text = fragmentManager.findFragmentByTag("send_id_start_share_text");

    	send = fragmentManager.findFragmentById(R.id.send);
    	receive = fragmentManager.findFragmentById(R.id.receive);
    	other = fragmentManager.findFragmentById(R.id.other);


    }
}
