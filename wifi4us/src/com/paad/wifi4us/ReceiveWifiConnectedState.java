package com.paad.wifi4us;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReceiveWifiConnectedState extends Fragment{
	
	private ConnectedStateReceiver connectedStateReceiver;
	private WifiDisconnectReceiver wifiDisconnectReceiver;
	private TextView time;
	private TextView traffic;
	private FragmentManager fragmentManager;
	private Fragment receive_id_start_scan_button;
	
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
		
		connectedStateReceiver = new ConnectedStateReceiver();
		wifiDisconnectReceiver = new WifiDisconnectReceiver();
        getActivity().getApplicationContext().registerReceiver(connectedStateReceiver, new IntentFilter(ReceiveService.CONMUNICATION_SETUP_HEART_BEATEN));
		getActivity().getApplicationContext().registerReceiver(wifiDisconnectReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	public void onDestroy(){
		super.onDestroy();
		getActivity().unbindService(sc);
		getActivity().getApplicationContext().unregisterReceiver(connectedStateReceiver);
		getActivity().getApplicationContext().unregisterReceiver(wifiDisconnectReceiver);
	}
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View view_res = inflater.inflate(R.layout.fragment_receive_wifi_connected_state, container, false);	
		time = (TextView)view_res.findViewById(R.id.receive_text_time_left);
		traffic = (TextView)view_res.findViewById(R.id.receive_text_traffic_used);
		return view_res;
	}
	
	private class ConnectedStateReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent){
			String timeNow = intent.getExtras().getString(ReceiveService.CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TIME);
			String trafficNow = intent.getExtras().getString(ReceiveService.CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TRAFFIC);
			time.setText(timeNow);
			traffic.setText(trafficNow);
		}
	}
	
	
	public class WifiDisconnectReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent) {
			if(!haveBondService)
				return;
			ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
    		State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  

    		if(State.DISCONNECTED == state){   
     	   		receiveService.closeConnection();
     	   		receiveService.WifiDisconnect();
     			Editor sharedata = getActivity().getApplicationContext().getSharedPreferences(getActivity().getApplicationContext().getPackageName(), Context.MODE_PRIVATE).edit(); 
     			sharedata.putBoolean("STATE_RECEIVE", false);
     			sharedata.commit();
     			
     			fragmentManager = getFragmentManager();
     			FragmentTransaction transaction = fragmentManager.beginTransaction();
     			receive_id_start_scan_button = fragmentManager.findFragmentByTag("receive_id_start_scan_button");
     			if(receive_id_start_scan_button == null){
     				receive_id_start_scan_button = new ReceiveScanButton();		
     				transaction.replace(R.id.receive_container_scan, receive_id_start_scan_button, "receive_id_start_scan_button");
     				
     			}
     			transaction.commitAllowingStateLoss();    		
    		
    		}  

		}
	}

}