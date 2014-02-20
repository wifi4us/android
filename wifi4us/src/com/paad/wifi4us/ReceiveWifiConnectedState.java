package com.paad.wifi4us;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.SupplicantState;
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

import com.paad.wifi4us.utility.Constant;

public class ReceiveWifiConnectedState extends Fragment{
	
	private ConnectedStateReceiver connectedStateReceiver;
	private WifiDisconnectReceiver wifiDisconnectReceiver;
	private TextView traffic;
	
	private Fragment currentFragment;
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
		fragmentManager = getFragmentManager();
		currentFragment = this;
		
		Context context = getActivity().getApplicationContext();
		unResgiterOldReceiver(context);
		connectedStateReceiver = new ConnectedStateReceiver();
		wifiDisconnectReceiver = new WifiDisconnectReceiver();
		if(!Constant.FLAG.RECEIVE_LIMIT_MODE.equals("UN")){
	        context.registerReceiver(connectedStateReceiver, new IntentFilter(Constant.BroadcastReceive.CONMUNICATION_SETUP_HEART_BEATEN));
		}
		context.registerReceiver(wifiDisconnectReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
	}
	
	public void onDestroy(){
		super.onDestroy();
		getActivity().unbindService(sc);
	}
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View view_res = inflater.inflate(R.layout.fragment_receive_wifi_connected_state, container, false);	
		traffic = (TextView)view_res.findViewById(R.id.receive_text_traffic_used);
		if(!Constant.FLAG.RECEIVE_LIMIT_MODE.equals("UN")){
			traffic.setText("0");
		}else{
			traffic.setText("²»ÏÞ");
		}
		return view_res;
	}
	
	private void unResgiterOldReceiver(Context context){
		try{
			context.unregisterReceiver(ReceiveScanResultList.clickConnectReceiver);
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			context.unregisterReceiver(ReceiveScanResultList.conmunicationReceiver);
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			context.unregisterReceiver(ReceiveScanResultList.connectFailReceiver);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private class ConnectedStateReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent){
			String trafficNow = intent.getExtras().getString(Constant.BroadcastReceive.CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TRAFFIC);
			int kbTraffic = Integer.parseInt(trafficNow);
			String kb = String.valueOf(kbTraffic / 1024);
			traffic.setText(kb);
		}
	}
	
	
	public class WifiDisconnectReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent) {
			try{
				if(!haveBondService)
					return;
				//get reward for receiving
				SupplicantState state = (SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
				if(state.equals(SupplicantState.DISCONNECTED) || state.equals(SupplicantState.INACTIVE)){  
					if(!Constant.FLAG.RECEIVE_LIMIT_MODE.equals("UN")){
						c.unregisterReceiver(connectedStateReceiver);
						receiveService.timer.cancel();
					}
					c.unregisterReceiver(this);
					receiveService.WifiDisconnectCompletely();
					receiveService.playSendSound(false);
					
	     	   		Constant.FLAG.STATE_RECEIVE = false;
	     			
	     			FragmentTransaction transaction = fragmentManager.beginTransaction();
	     			transaction.remove(currentFragment);
	     			receive_id_start_scan_button = fragmentManager.findFragmentByTag("receive_id_start_scan_button");
	     			if(receive_id_start_scan_button == null){
	     				receive_id_start_scan_button = new ReceiveScanButton();		
	     				transaction.replace(R.id.receive_container_scan, receive_id_start_scan_button, "receive_id_start_scan_button");
	     			}
	     			
	     			transaction.commitAllowingStateLoss();    		
	    		
	    		}
			}catch(Exception e){
				e.printStackTrace();
			}
  

		}
	}

}