package com.paad.wifi4us;


import java.util.ArrayList;
import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.PasswdUtil;
import com.paad.wifi4us.utility.SharedPreferenceHelper;

public class ReceiveScanResultList extends ListFragment{
	//ignore first wifi connected broadcast	
	private ArrayList<String> scanresultlist;
	private ArrayAdapter<String> scanresultlist_adapter;
	private ClickConnectReceiver clickConnectReceiver;
	private ConmunicationReceiver conmunicationReceiver;
	private FragmentManager fragmentManager;
	private Fragment receive_id_start_connect_progressbar;
	private Fragment receive_id_start_wifi_connected_fail_text;
	private SharedPreferenceHelper sharedPreference;

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
    
    public void setView(ArrayList<String> arr){
    	scanresultlist = arr;
    }
    
	public void onStart(){
		super.onStart();
	}
	
	public void onStop(){
		super.onStop();
	}
	
	
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = new Intent(getActivity(), ReceiveService.class);  
        //bind service to get ready for all the clickable element
		getActivity().bindService(intent, sc, Context.BIND_AUTO_CREATE); 
        super.onCreate(savedInstanceState);
        
    }
	
	public void onDestroy(){
		super.onDestroy();
		getActivity().unbindService(sc);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		fragmentManager = getFragmentManager();
		ArrayList<String> resultshown = getShownName(scanresultlist);
    	sharedPreference = new SharedPreferenceHelper(getActivity().getApplicationContext());

		scanresultlist_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, resultshown);
		setListAdapter(scanresultlist_adapter);
		return inflater.inflate(R.layout.fragment_receive_scan_resultlist, container, false);
	}

	public void onListItemClick(ListView arg0, View view, int pos, long id){
    	UIToProgressbar();
    	sharedPreference.putBoolean("FINISH_VIDEO", false);
		
		String rawssid = scanresultlist.get(pos);
		if(!haveBondService)
			return;
 
		clickConnectReceiver = new ClickConnectReceiver();
		conmunicationReceiver = new ConmunicationReceiver();
		
		receiveService.WifiDisconnect();

		/*
		 * process the next step until the wifi has been disconnected completely, 
		 * in case the broadcast receiver gets the wrong state which the last wifi 
		 * connection left
		 */
		while(true){
			try {
				Thread.sleep(100);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			Context c = getActivity().getApplicationContext();
			ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
			State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
			if(State.DISCONNECTED == state){
				getActivity().getApplicationContext().registerReceiver(clickConnectReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	        	getActivity().getApplicationContext().registerReceiver(conmunicationReceiver, new IntentFilter(Constant.BroadcastReceive.CONMUNICATION_SETUP));
	        	receiveService.WifiConnect(rawssid);
	        	break;
			}
		}
        

	}

	
	public class ClickConnectReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent) {
			
    		if(!haveBondService){
    			c.unregisterReceiver(this);
    			return;
    		}
    		ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
    		State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
    		if(State.CONNECTED == state){  
     			c.unregisterReceiver(this);
     	        receiveService.EstablishConmunication();
    		}  

		}
	}
	
	public class ConmunicationReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent) {
    		if(!haveBondService){
    			c.unregisterReceiver(this);
    			return;
    		}
    		
    		String state = intent.getExtras().getString(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_STATE);
      		if(state.equals("ok")){
          		c.unregisterReceiver(this);
      			String adid = intent.getExtras().getString(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_ADID);
      			String adword = intent.getExtras().getString(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_ADWORD);

    			Intent startvideo = new Intent(receive_id_start_connect_progressbar.getActivity(), VideoActivity.class);    
    			startvideo.putExtra("adword", adword);
    			startvideo.putExtra("adid", adid);
    			receive_id_start_connect_progressbar.startActivity(startvideo);			
    		}else{
    			ProgressbarToFail(state);
          		c.unregisterReceiver(this);
          		
    		}
	
      		

		}   		
	}
	
	private void UIToProgressbar(){
		FragmentTransaction transaction = fragmentManager.beginTransaction(); 
		receive_id_start_connect_progressbar = new WifiProgressBar();
		transaction.remove(this);

		transaction.add(R.id.receive_container_scan, receive_id_start_connect_progressbar, "receive_id_start_connect_progressbar");
		transaction.commitAllowingStateLoss(); 
	}
	
	private void ProgressbarToFail(String state){
		FragmentTransaction transaction = fragmentManager.beginTransaction(); 
		receive_id_start_connect_progressbar = fragmentManager.findFragmentByTag("receive_id_start_connect_progressbar");
		if(receive_id_start_connect_progressbar != null){
			transaction.remove(receive_id_start_connect_progressbar);
		}
		receive_id_start_wifi_connected_fail_text = new ReceiveWifiConnectedFailText();
		((ReceiveWifiConnectedFailText)receive_id_start_wifi_connected_fail_text).setTextWord(state);
		transaction.add(R.id.receive_container_scan, receive_id_start_wifi_connected_fail_text, "receive_id_start_wifi_connected_fail_text");
		transaction.commitAllowingStateLoss(); 
	}
	
	private ArrayList<String> getShownName(ArrayList<String> arr){
		ArrayList<String> temp_arr = new ArrayList<String>(arr);
		for(int i = 0; i < temp_arr.size(); i++){
			temp_arr.set(i, temp_arr.get(i).substring(1, 8));
		}
		
		return temp_arr;
	}
	
}
