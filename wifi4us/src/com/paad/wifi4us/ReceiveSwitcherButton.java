package com.paad.wifi4us;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.MyWifiManager;

public class ReceiveSwitcherButton extends Fragment{
	private Button switcherwifi;
	private Fragment receive_id_start_scan_progressbar;
	private Fragment receive_id_switcher_text_on;
	private Fragment receive_id_switcher_text_off;
	private Fragment receive_id_start_scan_text_openwifi;
	private Fragment receive_id_start_scan_button;
    private ClickSwitcherReceiver clickSwitcherReceiver;
	private FragmentManager fragmentManager;
	private MyWifiManager myWifiManager;

	
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
				myWifiManager = new MyWifiManager(getActivity().getApplicationContext());
				if(myWifiManager.getWifiApState() != myWifiManager.WIFI_AP_STATE_DISABLED){
					Toast toast = Toast.makeText(getActivity().getApplicationContext(), "分享的时候无法打开wifi", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				
				int state = myWifiManager.getWifiManager().getWifiState();
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
		
		if(receive_id_start_scan_button == null && !Constant.FLAG.STATE_RECEIVE){
			receive_id_start_scan_button = new ReceiveScanButton();
			transaction.replace(R.id.receive_container_scan, receive_id_start_scan_button, "receive_id_start_scan_button");
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
