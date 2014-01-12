package com.paad.wifi4us;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paad.wifi4us.utility.MyWifiManager;

public class SendFragment extends Fragment{
	private FragmentManager fragmentManager;
	private Fragment send_id_start_share_button;
	private Fragment send_id_start_share_text;
	private Fragment send_id_stop_share_button;
	
	private MyWifiManager myWifiManager;

    
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		fragmentManager = getFragmentManager();
		myWifiManager = new MyWifiManager(getActivity().getApplicationContext());
		View view_res = inflater.inflate(R.layout.fragment_send, container, false);
		return view_res;
	}
	
	
	public void onStart(){
		super.onStart();
		int state = myWifiManager.getWifiApState();
		if(state == myWifiManager.WIFI_AP_STATE_ENABLING || state == myWifiManager.WIFI_AP_STATE_ENABLED){
			UIStopSend();
		}else{
			UIStartSend();
		}
	}
	
	private void UIStartSend(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		send_id_start_share_button = fragmentManager.findFragmentByTag("send_id_start_share_button");
		if(send_id_start_share_button == null){
			send_id_start_share_button = new SendStartShareButton();
			 transaction.replace(R.id.send_container, send_id_start_share_button, "send_id_start_share_button");		
		}
		send_id_start_share_text = fragmentManager.findFragmentByTag("send_id_start_share_text");
		if(send_id_start_share_text == null){
			send_id_start_share_text = new SendStartShareText();
			transaction.replace(R.id.send_stateinfo_container, send_id_start_share_text, "send_id_start_share_text");
		}
		
		transaction.commitAllowingStateLoss();
	}
	
	private void UIStopSend(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		send_id_stop_share_button = new SendStopShareButton();
		transaction.replace(R.id.send_container, send_id_stop_share_button, "send_id_stop_share_button");
		transaction.commitAllowingStateLoss();
	}
	
}
