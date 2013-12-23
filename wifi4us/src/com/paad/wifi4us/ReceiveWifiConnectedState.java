package com.paad.wifi4us;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReceiveWifiConnectedState extends Fragment{
	
	private ConnectedStateReceiver connectedStateReceiver;
	private TextView time;
	private TextView traffic;
	
	public void onStart(){
		
		connectedStateReceiver = new ConnectedStateReceiver();
        getActivity().getApplicationContext().registerReceiver(connectedStateReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
	}
	
	public void onStop(){
		getActivity().getApplicationContext().unregisterReceiver(connectedStateReceiver);
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View view_res = inflater.inflate(R.layout.fragment_receive_wifi_connected_state, container, false);	
		time = (TextView)view_res.findViewById(R.id.receive_text_time_left);
		traffic = (TextView)view_res.findViewById(R.id.receive_text_traffic_used);
		return view_res;
	}
	
	private class ConnectedStateReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent){
			String timeNow = intent.getExtras().getString("time");
			String trafficNow = intent.getExtras().getString("traffic");
			time.setText(timeNow);
			traffic.setText(trafficNow);
		}
	}
}