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

public class SendWifiConnectedState extends Fragment{
	
	private ConnectedStateReceiver connectedStateReceiver;
	private TextView time;
	private TextView traffic;
	private TextView clientinfo;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		connectedStateReceiver = new ConnectedStateReceiver();
        getActivity().getApplicationContext().registerReceiver(connectedStateReceiver, new IntentFilter(SendService.LISTEN_SETUP_HEART_BEATEN));
	}
	
	public void onDestroy(){
		super.onDestroy();
		getActivity().getApplicationContext().unregisterReceiver(connectedStateReceiver);
	}
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View view_res = inflater.inflate(R.layout.fragment_send_wifi_connected_state, container, false);	
		time = (TextView)view_res.findViewById(R.id.send_text_time_left);
		traffic = (TextView)view_res.findViewById(R.id.send_text_traffic_used);
		clientinfo = (TextView)view_res.findViewById(R.id.send_text_client_info);

		return view_res;
	}
	
	private class ConnectedStateReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent){
			String trafficNow = intent.getExtras().getString(SendService.LISTEN_SETUP_HEART_BEATEN_EXTRA_TRAFFIC);
			traffic.setText(trafficNow);
		}
	}
}