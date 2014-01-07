package com.paad.wifi4us;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SendWifiConnectedState extends Fragment{
	
	private ConnectedStateReceiver connectedStateReceiver;
	private ConnectedFinishedReceiver connectedFinishedReceiver;
	private FragmentManager fragmentManager;
	
	private Fragment send_id_start_share_button;
	private Fragment send_id_start_share_text;
	
	private TextView time;
	private TextView traffic;
	private TextView clientinfo;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		fragmentManager = getFragmentManager();
		connectedStateReceiver = new ConnectedStateReceiver();
		connectedFinishedReceiver = new ConnectedFinishedReceiver();
        getActivity().getApplicationContext().registerReceiver(connectedStateReceiver, new IntentFilter(SendService.CONNECTION_HEARTBEAT));
        getActivity().getApplicationContext().registerReceiver(connectedFinishedReceiver, new IntentFilter(SendService.CONNECTION_FINISH));
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
			String trafficNow = intent.getExtras().getString(SendService.CONNECTION_HEARTBEAT_EXTRA_TRAFFIC);
			traffic.setText(trafficNow);
		}
	}
	
	private class ConnectedFinishedReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent){
			c.unregisterReceiver(this);

			FragmentTransaction transaction = fragmentManager.beginTransaction();
			send_id_start_share_button = new SendStartShareButton();
			send_id_start_share_text = new SendStartShareText();
			transaction.replace(R.id.send_container, send_id_start_share_button, "send_id_start_share_button");
			transaction.replace(R.id.send_stateinfo_container, send_id_start_share_text, "send_id_start_share_text");
			transaction.commitAllowingStateLoss();
			
		}
	}
	
}