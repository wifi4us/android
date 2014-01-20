package com.paad.wifi4us;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.MyWifiManager;

public class ReceiveFragment extends Fragment{
	private FragmentManager fragmentManager;
	private Fragment receivie_wifi_switcher_button;
	private Fragment receive_id_switcher_text_on;
	private Fragment receive_id_switcher_text_off;
	private Fragment receive_id_start_scan_button;
	private Fragment receive_id_start_connect_progressbar;
	private Fragment receive_id_start_wifi_connected_state;
	private MyWifiManager myWifiManager;

    //The call back functions start here
    
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	

	public void onStart(){
		super.onStart();
		//The init fragment
		if(Constant.FLAG.STATE_RECEIVE){
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
		myWifiManager = new MyWifiManager(getActivity().getApplicationContext());

		//start to render the fragment
		View view_res = inflater.inflate(R.layout.fragment_receive, container, false);	
		UICreate();
		return view_res;
	}
	
	
	private void UIStart(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		if(myWifiManager.getWifiManager().getWifiState() == WifiManager.WIFI_STATE_ENABLED){
			receive_id_switcher_text_on = new ReceiveSwitcherTextOn();
			transaction.replace(R.id.receive_container_switcher_text, receive_id_switcher_text_on, "receive_id_switcher_text_on");
		}else{	
			receive_id_switcher_text_off = new ReceiveSwitcherTextOff();
			transaction.replace(R.id.receive_container_switcher_text, receive_id_switcher_text_off, "receive_id_switcher_text_off");
		}
		transaction.commitAllowingStateLoss();

		
	}
	
	private void UICreate(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		receivie_wifi_switcher_button = new ReceiveSwitcherButton();

		transaction.add(R.id.receive_container_switcher_button, receivie_wifi_switcher_button, "receivie_wifi_switcher_button");
		
		receive_id_start_scan_button = fragmentManager.findFragmentByTag("receive_id_start_scan_button");
		if(receive_id_start_scan_button == null && !Constant.FLAG.STATE_RECEIVE){
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