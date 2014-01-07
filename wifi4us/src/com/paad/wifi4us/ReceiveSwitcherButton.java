package com.paad.wifi4us;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ReceiveSwitcherButton extends Fragment{
	private Button switcherwifi;
	private Fragment receive_id_start_scan_progressbar;
	private Fragment receive_id_switcher_text_on;
	private Fragment receive_id_switcher_text_off;
	private Fragment receive_id_start_scan_text_openwifi;
	private Fragment receive_id_start_scan_button;
	private Fragment receive_id_start_scan_resultlist;
	private Fragment receive_id_start_wifi_connected_state;
    private ClickSwitcherReceiver clickSwitcherReceiver;
	private FragmentManager fragmentManager;
	private WifiManager wifiManager;
	private Boolean startReceive;

	
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


	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(getActivity(), ReceiveService.class);  
        //bind service to get ready for all the clickable element
		getActivity().bindService(intent, sc, Context.BIND_AUTO_CREATE); 
	}
	
	public void onDestroy(){
		super.onDestroy();
		getActivity().unbindService(sc);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		fragmentManager = getFragmentManager();
		View view_res = inflater.inflate(R.layout.fragment_receive_switcher_button, container, false);
		switcherwifi = (Button) view_res.findViewById(R.id.receivie_button_wifi_switcher);
		switcherwifi.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				if(!haveBondService)
	    			return;
				wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
				int state = wifiManager.getWifiState();
				if(state == WifiManager.WIFI_STATE_DISABLING || state == WifiManager.WIFI_STATE_ENABLING){
					return;
				}
				
				clickSwitcherReceiver = new ClickSwitcherReceiver();
		        UISwitcherFromTextToProgressbar();
		        getActivity().getApplicationContext().registerReceiver(clickSwitcherReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

		        
				receiveService.WifiSwitcher();
			}
		});
		return view_res;
	}
	
	public class ClickSwitcherReceiver extends BroadcastReceiver{
    	public void onReceive(Context c, Intent intent) {
    		if(!haveBondService){
	    		c.unregisterReceiver(this);
    			return;
    		}
    		
    		int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);

    		if(state == WifiManager.WIFI_STATE_DISABLING){
    			UISwitcherFromProgressToTextOff();
		        UISwitcherCleanScanZone();
	    		c.unregisterReceiver(this);

    		} 
    		
    		if(state == WifiManager.WIFI_STATE_ENABLING){
    			UISwitcherFromProgressToTextOn();
    			UISwitcherInitScanZone();
        		c.unregisterReceiver(this);

    		}


    	}
    }
	
	private void UISwitcherInitScanZone(){
		FragmentTransaction transaction = fragmentManager.beginTransaction(); 
        receive_id_start_scan_text_openwifi= fragmentManager.findFragmentByTag("receive_id_start_scan_text_openwifi");
        if(receive_id_start_scan_text_openwifi != null)
		{
			transaction.remove(receive_id_start_scan_text_openwifi);
		}
        
        receive_id_start_scan_button = fragmentManager.findFragmentByTag("receive_id_start_scan_button");
		
        Context context = getActivity().getApplicationContext();
		SharedPreferences sharedata = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE); 
		startReceive = sharedata.getBoolean("STATE_RECEIVE", false);

		if(receive_id_start_scan_button == null && !startReceive){
			receive_id_start_scan_button = new ReceiveScanButton();
			transaction.replace(R.id.receive_container_scan, receive_id_start_scan_button, "receive_id_start_scan_button");
		}
		
		transaction.commitAllowingStateLoss(); 

	}
	
	private void UISwitcherCleanScanZone(){
		Editor sharedata = getActivity().getApplicationContext().getSharedPreferences(getActivity().getApplicationContext().getPackageName(), Context.MODE_PRIVATE).edit(); 
		sharedata.putBoolean("STATE_RECEIVE", false);
		sharedata.commit();
			
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		receive_id_start_scan_button = fragmentManager.findFragmentByTag("receive_id_start_scan_button");
		receive_id_start_scan_resultlist = fragmentManager.findFragmentByTag("receive_id_start_scan_resultlist");
		receive_id_start_scan_progressbar = fragmentManager.findFragmentByTag("receive_id_start_scan_progressbar_switcher");
		receive_id_start_wifi_connected_state = fragmentManager.findFragmentByTag("receive_id_start_wifi_connected_state");
      
        if(receive_id_start_scan_button != null){
			transaction.remove(receive_id_start_scan_button);
		}
        if(receive_id_start_scan_resultlist != null){
			transaction.remove(receive_id_start_scan_resultlist);
		}
        if(receive_id_start_scan_progressbar != null){
			transaction.remove(receive_id_start_scan_progressbar);
		}
        if(receive_id_start_wifi_connected_state != null){
			transaction.remove(receive_id_start_wifi_connected_state);
		}
        
        receive_id_start_scan_text_openwifi = fragmentManager.findFragmentByTag("receive_id_start_scan_text_openwifi");
        if(receive_id_start_scan_text_openwifi == null){
        	receive_id_start_scan_text_openwifi = new ReceiveStartScanTextOpenwifi();
			transaction.replace(R.id.receive_container_scan, receive_id_start_scan_text_openwifi, "receive_id_start_scan_text_openwifi");
        }
        
		transaction.commitAllowingStateLoss(); 
	}
	
	private void UISwitcherFromTextToProgressbar(){
		FragmentTransaction transaction = fragmentManager.beginTransaction(); 
		receive_id_start_scan_progressbar = new WifiProgressBar();
		transaction.replace(R.id.receive_container_switcher_text, receive_id_start_scan_progressbar, "receive_id_start_scan_progressbar_switcher");
		transaction.commitAllowingStateLoss(); 

	}
	
	private void UISwitcherFromProgressToTextOn(){
		FragmentTransaction transaction = fragmentManager.beginTransaction(); 
        receive_id_switcher_text_on = new ReceiveSwitcherTextOn();
    	transaction.replace(R.id.receive_container_switcher_text, receive_id_switcher_text_on, "receive_id_switcher_text_on");
		transaction.commitAllowingStateLoss(); 

	}

	private void UISwitcherFromProgressToTextOff(){
		FragmentTransaction transaction = fragmentManager.beginTransaction(); 
        receive_id_switcher_text_off = new ReceiveSwitcherTextOff();
    	transaction.replace(R.id.receive_container_switcher_text, receive_id_switcher_text_off, "receive_id_switcher_text_off");
		transaction.commitAllowingStateLoss(); 
	}
	
}
