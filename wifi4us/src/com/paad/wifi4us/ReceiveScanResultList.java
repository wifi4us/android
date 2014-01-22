package com.paad.wifi4us;


import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.MyWifiManager;

public class ReceiveScanResultList extends ListFragment{
	private Context context;
	private ArrayList<String> scanresultlist;
	private ArrayAdapter<String> scanresultlist_adapter;
	public static ClickConnectReceiver clickConnectReceiver;
	public static ConmunicationReceiver conmunicationReceiver;
	public static ConnectFailReceiver connectFailReceiver;
	public static WifiDisconnectWrongReceiver wifiDisconnectReceiver;
	
	private FragmentManager fragmentManager;
	private Fragment receive_id_start_connect_progressbar;
	private Fragment receive_id_start_wifi_connected_fail_text;
	private Activity currentActivity;
	
	private MyWifiManager myWifiManager;
	private String rawssid;

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
		currentActivity = getActivity();
		context = getActivity().getApplicationContext();
		unResgiterOldReceiver(context);

        //bind service to get ready for all the clickable element
		currentActivity.bindService(intent, sc, Context.BIND_AUTO_CREATE); 
        super.onCreate(savedInstanceState);
        
    }
	
	public void onDestroy(){
		super.onDestroy();
		currentActivity.unbindService(sc);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		fragmentManager = getFragmentManager();
		myWifiManager = new MyWifiManager(context);
		ArrayList<String> resultshown = getShownName(scanresultlist);

		scanresultlist_adapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1, resultshown);
		setListAdapter(scanresultlist_adapter);
		return inflater.inflate(R.layout.fragment_receive_scan_resultlist, container, false);
	}

	public void onListItemClick(ListView arg0, View view, int pos, long id){
		if(!haveBondService)
			return;
		

		if(myWifiManager.getWifiManager().getWifiState() != WifiManager.WIFI_STATE_ENABLED){
			Toast toast = Toast.makeText(context, "请先打开wifi", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		
		receiveService.WifiDisconnect();
    	UIToProgressbar();
		Constant.FLAG.FINISH_VIDEO = false;
		Constant.FLAG.FINISH_PRECONNNECT = false;
		rawssid = scanresultlist.get(pos);
 
		clickConnectReceiver = new ClickConnectReceiver();
		conmunicationReceiver = new ConmunicationReceiver();
		connectFailReceiver = new ConnectFailReceiver();
		wifiDisconnectReceiver = new WifiDisconnectWrongReceiver();

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
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
			if(State.DISCONNECTED == state){
				context.registerReceiver(connectFailReceiver, new IntentFilter(Constant.BroadcastReceive.CONMUNICATION_SETUP_INTERRUPT));
				context.registerReceiver(clickConnectReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
				context.registerReceiver(conmunicationReceiver, new IntentFilter(Constant.BroadcastReceive.CONMUNICATION_SETUP));
				context.registerReceiver(wifiDisconnectReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));

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
    			String actualSSID = myWifiManager.getWifiManager().getConnectionInfo().getSSID();
    			if(!actualSSID.equals(rawssid)){
    				Intent i = new Intent();
    				i.setAction(Constant.BroadcastReceive.CONMUNICATION_SETUP);
    				i.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_STATE, "wifi连接认证超时");
    				currentActivity.sendBroadcast(i);
    					
    				receiveService.WifiDisconnectCompletely();
    	     		c.unregisterReceiver(this);
    	     		return;
    			}

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

    			Intent startvideo = new Intent(currentActivity, VideoActivity.class);    
    			startvideo.putExtra("adword", adword);
    			startvideo.putExtra("adid", adid);
    			Constant.FLAG.FINISH_PRECONNNECT = true;
    			currentActivity.startActivity(startvideo);			
    		}else{
    			ProgressbarToFail(state);
          		c.unregisterReceiver(this);
    		}
		}   		
	}
	
	public class ConnectFailReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent) {
			ProgressbarToFail("连接过程被打断，网络连接失败");
			c.unregisterReceiver(this);
		}
	}
	

	public class WifiDisconnectWrongReceiver extends BroadcastReceiver{
		public void onReceive(Context c, Intent intent) {
			if(!haveBondService)
				return;
			//get reward for receiving
			SupplicantState state = (SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			if(state.equals(SupplicantState.INACTIVE)){ 
				ProgressbarToFail("分享者的共享意外中断");
     	   		receiveService.WifiDisconnectCompletely();
     	   		c.unregisterReceiver(this);
    		}  

		}
	}

	private void UIToProgressbar(){
		FragmentTransaction transaction = fragmentManager.beginTransaction(); 
		transaction.remove(this);
		receive_id_start_connect_progressbar = new WifiProgressBar();
		transaction.replace(R.id.receive_container_scan_result, receive_id_start_connect_progressbar, "receive_id_start_connect_progressbar");

        
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
		transaction.add(R.id.receive_container_scan_result, receive_id_start_wifi_connected_fail_text, "receive_id_start_wifi_connected_fail_text");
		transaction.commitAllowingStateLoss(); 
	}
	
	private ArrayList<String> getShownName(ArrayList<String> arr){
		ArrayList<String> temp_arr = new ArrayList<String>(arr);
		for(int i = 0; i < temp_arr.size(); i++){
			String shownname = "伟大无私的" + temp_arr.get(i).substring(1, 8);
			String modepart = temp_arr.get(i).substring(24, 28);
			if(modepart.equals("3005")){
				shownname = shownname + "分享了流量，最多使用30分钟或5M";
			}
			temp_arr.set(i, shownname);
		}
		
		return temp_arr;
	}
	
	private void unResgiterOldReceiver(Context context){
		try{
			context.unregisterReceiver(ReceiveScanButton.clickScanReceiver);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
