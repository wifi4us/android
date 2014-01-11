package com.paad.wifi4us;

import com.paad.wifi4us.utility.Constant;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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

public class SendStartShareButton extends Fragment{
	private Button startshare;
	private Context context;
	private Fragment send_id_progressbar_button;
	private Fragment send_id_progressbar_text;
	private Fragment send_id_stop_share_button;
	private Fragment send_id_stop_stateinfo;
	private Fragment send_id_stop_share_text;
	private Fragment send_id_start_share_fail;

	
	private ClickStartShareReceiver clickStartShareReceiver;
	private ListenStartReceiver listenStartReceiver;
	private ConnectionStartReceiver connectionStartReceiver;

	private FragmentManager fragmentManager;

    //Send Service 	
    private SendService sendService;
	private boolean haveBondService;
	private ServiceConnection sc = new ServiceConnection() {
        @Override  
        public void onServiceDisconnected(ComponentName arg0) {  
        	haveBondService = false;
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder binder) {
        	sendService = ((SendService.MyBinder)binder).getService();
        	haveBondService = true;
        }  
    };  

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(getActivity(), SendService.class);  
        //bind service to get ready for all the clickable element
		getActivity().bindService(intent, sc, Context.BIND_AUTO_CREATE); 
	}
	
	public void onDestroy(){
		super.onDestroy();
		getActivity().unbindService(sc);

	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		fragmentManager = getFragmentManager();
		
		View view_res = inflater.inflate(R.layout.fragment_send_start_share_button, container, false);
		startshare = (Button) view_res.findViewById(R.id.send_button_start_share);
		startshare.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				if(!haveBondService)
	    			return;
				
				context = getActivity().getApplicationContext();
				clickStartShareReceiver = new ClickStartShareReceiver();
				listenStartReceiver = new ListenStartReceiver();
				connectionStartReceiver = new ConnectionStartReceiver();
				context.registerReceiver(clickStartShareReceiver, new IntentFilter(Constant.BroadcastSend.AP_STATE_OPEN_ACTION));


				//The progress bar fragment
				UIScanFromShareToProgress();

		        sendService.WifiApOn();
			}
		});

		return view_res;
	}
	
	public class ClickStartShareReceiver extends BroadcastReceiver{
    	public void onReceive(Context c, Intent intent) {
    		//The result list fragment or fail result fragment
    		if(!intent.getExtras().get("apstate").equals("ok")){
    			UIScanFromProgressToNotReadyState();
    	        c.unregisterReceiver(this);
    			return;
    		}
			context.registerReceiver(listenStartReceiver, new IntentFilter(Constant.BroadcastSend.LISTEN_SETUP));
			context.registerReceiver(connectionStartReceiver, new IntentFilter(Constant.BroadcastSend.CONNECTION_SETUP));
    		sendService.ListenHeartBeat();
    		c.unregisterReceiver(this);
    	}
    }

	public class ListenStartReceiver extends BroadcastReceiver{
    	public void onReceive(Context c, Intent intent) {
    		UIScanFromProgressToReadyState();
    		c.unregisterReceiver(this);
    	}
    }
	
	
	public class ConnectionStartReceiver extends BroadcastReceiver{
    	public void onReceive(Context c, Intent intent) {
    		UIScanFromReadyStateToConnectState();
    		c.unregisterReceiver(this);
    	}
    }
	
	private void UIScanFromShareToProgress(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		send_id_progressbar_button = new WifiProgressBar();
		send_id_progressbar_text = new WifiProgressBar();
		transaction.replace(R.id.send_container, send_id_progressbar_button, "send_id_progressbar_button");
		transaction.replace(R.id.send_stateinfo_container, send_id_progressbar_text, "send_id_progressbar_text");
		transaction.commitAllowingStateLoss();
	}
	
	private void UIScanFromProgressToReadyState(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		send_id_stop_share_button = new SendStopShareButton();
		send_id_stop_share_text = new SendStopShareText();
		transaction.replace(R.id.send_container, send_id_stop_share_button, "send_id_stop_share_button");
		transaction.replace(R.id.send_stateinfo_container, send_id_stop_share_text, "send_id_stop_stateinfo");
		transaction.commitAllowingStateLoss();
	}
	
	private void UIScanFromReadyStateToConnectState(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		send_id_stop_stateinfo = new SendWifiConnectedState();
		transaction.replace(R.id.send_stateinfo_container, send_id_stop_stateinfo, "send_id_stop_stateinfo");
		transaction.commitAllowingStateLoss();
	}
	
	private void UIScanFromProgressToNotReadyState(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		send_id_start_share_fail = new SendStartFailText();
		transaction.replace(R.id.send_container, this, "send_id_start_share_button");
		transaction.replace(R.id.send_stateinfo_container, send_id_start_share_fail, "send_id_stop_stateinfo");
		transaction.commitAllowingStateLoss();
	}
	
}
