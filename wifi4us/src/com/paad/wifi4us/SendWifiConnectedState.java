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

import com.paad.wifi4us.utility.Constant;

public class SendWifiConnectedState extends Fragment{
	
	private ConnectedStateReceiver connectedStateReceiver;
	private ConnectedFinishedReceiver connectedFinishedReceiver;
	private FragmentManager fragmentManager;
	
	private Fragment send_id_start_share_button;
	private Fragment send_id_start_share_text;
	
	private TextView traffic;
    

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		fragmentManager = getFragmentManager();
		connectedStateReceiver = new ConnectedStateReceiver();
		connectedFinishedReceiver = new ConnectedFinishedReceiver();
		
        getActivity().getApplicationContext().registerReceiver(connectedStateReceiver, new IntentFilter(Constant.BroadcastSend.CONNECTION_HEARTBEAT));
        getActivity().getApplicationContext().registerReceiver(connectedFinishedReceiver, new IntentFilter(Constant.BroadcastSend.CONNECTION_FINISH));
	}
	
	public void onDestroy(){
		try{
			getActivity().getApplicationContext().unregisterReceiver(connectedStateReceiver);
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onDestroy();
	}
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View view_res = inflater.inflate(R.layout.fragment_send_wifi_connected_state, container, false);	
		traffic = (TextView)view_res.findViewById(R.id.send_text_traffic_used);
		traffic.setText("0");
		return view_res;
	}
	
	private class ConnectedStateReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent){
			String trafficNow = intent.getExtras().getString(Constant.BroadcastSend.CONNECTION_HEARTBEAT_EXTRA_TRAFFIC);
			int kbTraffic = Integer.parseInt(trafficNow);
			String kb = String.valueOf(kbTraffic / 1024);
			traffic.setText(kb);
		}
	}
	
	private class ConnectedFinishedReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent){
			try{
				getActivity().getApplicationContext().unregisterReceiver(connectedStateReceiver);
				c.unregisterReceiver(this);
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				send_id_start_share_button = new SendStartShareButton();
				send_id_start_share_text = new SendStartShareText();
				transaction.replace(R.id.send_container, send_id_start_share_button, "send_id_start_share_button");
				transaction.replace(R.id.send_stateinfo_container, send_id_start_share_text, "send_id_start_share_text");
				transaction.commitAllowingStateLoss();
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
	
	
	
}