package com.paad.wifi4us;

import java.util.ArrayList;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ReceiveScanButton extends Fragment{
	private Button scanwifi;
	private boolean doubleclickscan;
	private Fragment receive_id_start_scan_progressbar;
	private Fragment receive_id_start_scan_resultlist;
    private ClickScanReceiver clickScanReceiver;
	private FragmentManager fragmentManager;

	
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
		doubleclickscan = false;
		fragmentManager = getFragmentManager();
		View view_res = inflater.inflate(R.layout.fragment_receive_scan_button, container, false);
		scanwifi = (Button) view_res.findViewById(R.id.receive_button_start_scan_button);
		scanwifi.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				if(!haveBondService)
	    			return;
				if(doubleclickscan == true)
					return;
				doubleclickscan = true;
				clickScanReceiver = new ClickScanReceiver();
		        getActivity().getApplicationContext().registerReceiver(clickScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		   
				//The progress bar fragment
				UIScanFromOtherToProgress();

		        receiveService.WifiScan();
			}
		});
		
		return view_res;
	}
	
	public class ClickScanReceiver extends BroadcastReceiver{
    	public void onReceive(Context c, Intent intent) {

    		if(!haveBondService){
        		c.unregisterReceiver(this);    			
    			return;
    		}
    		//The result list fragment or fail result fragment
    		ArrayList<String> wifiAPList = receiveService.getWifiScanResult();
    		UIScanFromProgressToScanresult(wifiAPList);
    		
    		c.unregisterReceiver(this);    			

            doubleclickscan = false;
    	}
    }
	
	
	private void UIScanFromOtherToProgress(){
		receive_id_start_scan_progressbar = new WifiProgressBar();
		
		FragmentTransaction transaction = fragmentManager.beginTransaction(); 
		
		receive_id_start_scan_resultlist = fragmentManager.findFragmentByTag("receive_id_start_scan_resultlist");

		if(receive_id_start_scan_resultlist != null)
		{
			transaction.remove(receive_id_start_scan_resultlist);
		}

		
		transaction.add(R.id.receive_container_scan, receive_id_start_scan_progressbar, "receive_id_start_scan_progressbar_scan");
		transaction.commitAllowingStateLoss(); 

	}

	private void UIScanFromProgressToScanresult(ArrayList<String> wifiAPList){
        FragmentTransaction transaction = fragmentManager.beginTransaction(); 
        receive_id_start_scan_progressbar = fragmentManager.findFragmentByTag("receive_id_start_scan_progressbar_scan");
        if(receive_id_start_scan_progressbar != null)
		{

			transaction.remove(receive_id_start_scan_progressbar);
		}
       
        receive_id_start_scan_resultlist = fragmentManager.findFragmentByTag("receive_id_start_scan_resultlist");
        if(receive_id_start_scan_resultlist == null){
        	receive_id_start_scan_resultlist = new ReceiveScanResultList();
        }
        ((ReceiveScanResultList)receive_id_start_scan_resultlist).setView(wifiAPList);
        transaction.add(R.id.receive_container_scan, receive_id_start_scan_resultlist, "receive_id_start_scan_resultlist");
      
        
        transaction.commitAllowingStateLoss();
	}
}
