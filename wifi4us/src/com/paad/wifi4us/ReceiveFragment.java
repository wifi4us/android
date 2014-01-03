package com.paad.wifi4us;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReceiveFragment extends Fragment{
	private FragmentManager fragmentManager;
	private Fragment receivie_wifi_switcher_button;
	private Fragment receive_id_switcher_text_on;
	private Fragment receive_id_switcher_text_off;
	private Fragment receive_id_start_scan_button;
	private Fragment receive_id_start_scan_resultlist;
	private Fragment receive_id_start_scan_progressbar;
	private Fragment receive_id_start_connect_progressbar;
	private Fragment receive_id_start_scan_text_openwifi;
	private Fragment receive_id_start_wifi_connected_state;
	private WifiManager wifiManager;
	private Boolean startReceive;

    //The call back functions start here
    
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	

	public void onStart(){
		super.onStart();
		//The init fragment
		Context context = getActivity().getApplicationContext();
		SharedPreferences sharedata = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE); 
		startReceive = sharedata.getBoolean("STATE_RECEIVE", false);
		if(startReceive){
			UIStartFinishVideo();
		}else{
			UIStart();
		}
	}
	
	public void onStop(){
		super.onStop();
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		//initial the activity variable
		fragmentManager = getFragmentManager();
		wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

		//start to render the fragment
		View view_res = inflater.inflate(R.layout.fragment_receive, container, false);	
		UICreate();
		return view_res;
	}
	
	
	private void UIStart(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
			receive_id_switcher_text_on = new ReceiveSwitcherTextOn();
			transaction.replace(R.id.receive_container_switcher_text, receive_id_switcher_text_on, "receive_id_switcher_text_on");
			
			receive_id_start_scan_text_openwifi = fragmentManager.findFragmentByTag("receive_id_start_scan_text_openwifi");
			if(receive_id_start_scan_text_openwifi != null){
				transaction.remove(receive_id_start_scan_text_openwifi);
			}
			
			receive_id_start_scan_button = fragmentManager.findFragmentByTag("receive_id_start_scan_button");
			if(receive_id_start_scan_button == null && !startReceive){
				receive_id_start_scan_button = new ReceiveScanButton();		
				transaction.replace(R.id.receive_container_scan, receive_id_start_scan_button, "receive_id_start_scan_button");
				
			}
		}else{	
			receive_id_switcher_text_off = new ReceiveSwitcherTextOff();
			transaction.replace(R.id.receive_container_switcher_text, receive_id_switcher_text_off, "receive_id_switcher_text_off");
			
			//clean scan zone
			receive_id_start_scan_button = fragmentManager.findFragmentByTag("receive_id_start_scan_button");
			receive_id_start_scan_resultlist = fragmentManager.findFragmentByTag("receive_id_start_scan_resultlist");
			receive_id_start_scan_progressbar = fragmentManager.findFragmentByTag("receive_id_start_scan_progressbar_switcher");
			receive_id_start_connect_progressbar = fragmentManager.findFragmentByTag("receive_id_start_connect_progressbar");
			
		
	        if(receive_id_start_scan_button != null){
				transaction.remove(receive_id_start_scan_button);
			}
	        if(receive_id_start_scan_resultlist != null){
				transaction.remove(receive_id_start_scan_resultlist);
			}

	        if(receive_id_start_scan_progressbar != null){
				transaction.remove(receive_id_start_scan_progressbar);
			}
	        
	        if(receive_id_start_connect_progressbar != null){
				transaction.remove(receive_id_start_connect_progressbar);
			}
	        
			receive_id_start_scan_text_openwifi = new ReceiveStartScanTextOpenwifi();
			transaction.replace(R.id.receive_container_scan, receive_id_start_scan_text_openwifi, "receive_id_start_scan_text_openwifi");
		
		}
		transaction.commitAllowingStateLoss();

		
	}
	
	private void UICreate(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		receivie_wifi_switcher_button = new ReceiveSwitcherButton();

		transaction.add(R.id.receive_container_switcher_button, receivie_wifi_switcher_button, "receivie_wifi_switcher_button");
		
		Context context = getActivity().getApplicationContext();
		SharedPreferences sharedata = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE); 
		startReceive = sharedata.getBoolean("STATE_RECEIVE", false);

		receive_id_start_scan_button = fragmentManager.findFragmentByTag("receive_id_start_scan_button");
		if(receive_id_start_scan_button == null && !startReceive){
			receive_id_start_scan_button = new ReceiveScanButton();		
			transaction.replace(R.id.receive_container_scan, receive_id_start_scan_button, "receive_id_start_scan_button");
			
		}
		transaction.commitAllowingStateLoss();
	}
	
	private void UIStartFinishVideo(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		
		receive_id_start_connect_progressbar = fragmentManager.findFragmentByTag("receive_id_start_connect_progressbar");
        if(receive_id_start_connect_progressbar != null){
			transaction.remove(receive_id_start_connect_progressbar);
		}
        receive_id_start_wifi_connected_state = fragmentManager.findFragmentByTag("receive_id_start_wifi_connected_state");
        if(receive_id_start_wifi_connected_state == null){
        	receive_id_start_wifi_connected_state = new ReceiveWifiConnectedState();
    		transaction.replace(R.id.receive_container_scan, receive_id_start_wifi_connected_state, "receive_id_start_wifi_connected_state");
        }
		transaction.commitAllowingStateLoss();
	}
	
}