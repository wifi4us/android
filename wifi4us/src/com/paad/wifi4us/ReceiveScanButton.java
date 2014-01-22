package com.paad.wifi4us;

import java.util.ArrayList;
import java.util.Iterator;

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
import com.paad.wifi4us.utility.PasswdUtil;

public class ReceiveScanButton extends Fragment{
	private Button scanwifi;
	private boolean doubleclickscan;
	private Fragment receive_id_start_scan_progressbar;
	private Fragment receive_id_start_scan_resultlist;
	private Fragment receive_id_start_scan_nothing;
	private Fragment receive_id_start_wifi_connected_fail_text;
    public static ClickScanReceiver clickScanReceiver;
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
		doubleclickscan = false;
		fragmentManager = getFragmentManager();
		myWifiManager = new MyWifiManager(getActivity().getApplicationContext());
		View view_res = inflater.inflate(R.layout.fragment_receive_scan_button, container, false);
		scanwifi = (Button) view_res.findViewById(R.id.receive_button_start_scan_button);
		scanwifi.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				if(!haveBondService)
	    			return;
				if(doubleclickscan == true)
					return;
				doubleclickscan = true;
				
				if(myWifiManager.getWifiManager().getWifiState() != WifiManager.WIFI_STATE_ENABLED){
					Toast toast = Toast.makeText(getActivity().getApplicationContext(), "ÇëÏÈ´ò¿ªwifi", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
		            doubleclickscan = false;
					return;
				}
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
    		removeOtherHotpot(wifiAPList);
    		removeOutdatedHotpot(wifiAPList);
    		if(wifiAPList.size() != 0){
        		UIScanFromProgressToScanresult(wifiAPList);
    		}else{
    			UIScanFromProgressToNothing();
    		}
    		
    		c.unregisterReceiver(this); 
    		c.removeStickyBroadcast(intent);
            doubleclickscan = false;
    	}
    }
	
	
	private void UIScanFromOtherToProgress(){
		receive_id_start_scan_progressbar = new WifiProgressBar();
		
		FragmentTransaction transaction = fragmentManager.beginTransaction(); 
		
		receive_id_start_scan_resultlist = fragmentManager.findFragmentByTag("receive_id_start_scan_resultlist");
        receive_id_start_scan_nothing = fragmentManager.findFragmentByTag("receive_id_start_scan_nothing");
        receive_id_start_wifi_connected_fail_text = fragmentManager.findFragmentByTag("receive_id_start_wifi_connected_fail_text");

		if(receive_id_start_scan_resultlist != null)
		{
			transaction.remove(receive_id_start_scan_resultlist);
		}
		if(receive_id_start_scan_nothing != null)
		{
			transaction.remove(receive_id_start_scan_nothing);
		}
		if(receive_id_start_wifi_connected_fail_text != null)
		{
			transaction.remove(receive_id_start_wifi_connected_fail_text);
		}
		
		
		transaction.replace(R.id.receive_container_scan_result, receive_id_start_scan_progressbar, "receive_id_start_scan_progressbar_scan");
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
        transaction.replace(R.id.receive_container_scan_result, receive_id_start_scan_resultlist, "receive_id_start_scan_resultlist");
      
        
        transaction.commitAllowingStateLoss();
	}
	
	private void UIScanFromProgressToNothing(){
        FragmentTransaction transaction = fragmentManager.beginTransaction(); 
        receive_id_start_scan_progressbar = fragmentManager.findFragmentByTag("receive_id_start_scan_progressbar_scan");
        if(receive_id_start_scan_progressbar != null)
		{

			transaction.remove(receive_id_start_scan_progressbar);
		}
       
        receive_id_start_scan_nothing = fragmentManager.findFragmentByTag("receive_id_start_scan_nothing");
        if(receive_id_start_scan_nothing == null){
        	receive_id_start_scan_nothing = new ReceiveStartScanNothing();
        }
        transaction.replace(R.id.receive_container_scan_result, receive_id_start_scan_nothing, "receive_id_start_scan_nothing");
        
        transaction.commitAllowingStateLoss();
	}
	
	private void removeOtherHotpot(ArrayList<String> arr){
    	Iterator<String> sListIterator = arr.iterator();  
    	while(sListIterator.hasNext()){  
    	    String ssidname = sListIterator.next();  
    	    if(ssidname.length() != 32){  
    	    	sListIterator.remove(); 
    	    	continue;
    	    }
    	    
    	    String namepart = ssidname.substring(0, 8);
    	    String passwdpart = ssidname.substring(8, 24);
    	    String modepart = ssidname.substring(24, 28);
    	    String signpart = ssidname.substring(28,32);
    	    String computesign = null;
    	    
    	    try{
    	    	computesign = PasswdUtil.getMD5Sign(namepart + passwdpart + modepart);
    	    }catch(Exception e){
    	    	e.printStackTrace();
    	    }

    	    if(!computesign.equals(signpart)){
    	    	sListIterator.remove();  
    	    }
    	}      	
	}
	
	private void removeOutdatedHotpot(ArrayList<String> arr){
		ArrayList<String> tempArr = arr;
		Iterator<String> sListIterator = arr.iterator();  
    	while(sListIterator.hasNext()){  
    	    String ssidname = sListIterator.next();
    	    for (int i = 0; i < tempArr.size(); i++){
    	    	String namepart = ssidname.substring(0, 8);
         	    String passwdpart = ssidname.substring(8, 24);
    	    	
         	    String ssidnametemp = tempArr.get(i);
        	    String nameparttemp = ssidnametemp.substring(0, 8);
        	    String passwdparttemp = ssidnametemp.substring(8, 24);

    	    	if(namepart.equals(nameparttemp)){
    	    	    try{
    	    	    	String passwd = PasswdUtil.decryptDES(passwdpart, Constant.Security.DES_KEY);
    	    	    	String passwdtemp = PasswdUtil.decryptDES(passwdparttemp, Constant.Security.DES_KEY);
    	    	    	long passwdlong = Long.parseLong(passwd);
    	    	    	long passwdtemplong = Long.parseLong(passwdtemp);
    	    	    	if(passwdlong < passwdtemplong){
    	        	    	sListIterator.remove();  
    	    	    	}
    	    	    }catch(Exception e){
    	    	    	e.printStackTrace();
    	    	    }
    	    	}
    	    }
    	}
	}
}
