package com.paad.wifi4us;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.SharedPreferenceHelper;

public class SendStartShareButton extends Fragment{
	private Button startShare;
	private CheckBox adMode;
	private RadioGroup radioGroup;
	private RadioButton radioButton30;
	private RadioButton radioButton60;
	private RadioButton radioButtonUnlimit;

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

	private SharedPreferenceHelper sharedPreference;

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
		sharedPreference = new SharedPreferenceHelper(getActivity().getApplicationContext());

		View view_res = inflater.inflate(R.layout.fragment_send_start_share_button, container, false);
		//init start share button
		startShare = (Button) view_res.findViewById(R.id.send_button_start_share);
		startShare.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				if(Constant.PreventAbuse.DOUBLE_START_SEND){
					Toast toast = Toast.makeText(getActivity().getApplicationContext(), "努力配置分享网络中", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				Constant.PreventAbuse.DOUBLE_START_SEND = true;

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

		//init ad mode checkbox
		adMode = (CheckBox) view_res.findViewById(R.id.send_checkbox);
		String currentAdMode = sharedPreference.getString("SEND_AD_MODE");
		if(currentAdMode.equals("YES")){
			adMode.setChecked(true);
		}else{
			adMode.setChecked(false);
		}
		adMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if(isChecked){
					if(sharedPreference.getString("SEND_LIMIT_MODE").equals("UN")){
						adMode.setChecked(false);
						sharedPreference.putString("SEND_AD_MODE", "NO");
						Toast.makeText(getActivity(), "不限流量无法插播广告", Toast.LENGTH_SHORT).show();
					}else{
						sharedPreference.putString("SEND_AD_MODE", "YES");
					}
				}else{
					sharedPreference.putString("SEND_AD_MODE", "NO");
				}
			}

		});
		
		//init limit mode radio group
		radioGroup = (RadioGroup) view_res.findViewById(R.id.send_radio_group);
		radioButton30 = (RadioButton) view_res.findViewById(R.id.send_radio_button_30);
		radioButton60 = (RadioButton) view_res.findViewById(R.id.send_radio_button_60);
		radioButtonUnlimit = (RadioButton) view_res.findViewById(R.id.send_radio_button_unlimit);
		String currentLimitMode = sharedPreference.getString("SEND_LIMIT_MODE");
		if(currentLimitMode.equals("30")){
			radioButton30.setChecked(true);
		}else if(currentLimitMode.equals("60")){
			radioButton60.setChecked(true);
		}else{
			radioButtonUnlimit.setChecked(true);
		}
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  
            public void onCheckedChanged(RadioGroup group, int checkedId) {  
                // TODO Auto-generated method stub  
                if(checkedId == radioButton30.getId()){  
                	sharedPreference.putString("SEND_LIMIT_MODE", "30");
                }else if(checkedId == radioButton60.getId()){
                	sharedPreference.putString("SEND_LIMIT_MODE", "60");
                }else{
                	sharedPreference.putString("SEND_LIMIT_MODE", "UN");
                	adMode.setChecked(false);
					sharedPreference.putString("SEND_AD_MODE", "NO");
					Toast.makeText(getActivity(), "不限流量无法插播广告", Toast.LENGTH_SHORT).show();
                }
            }  
        });  	
		return view_res;
	}
	
	public class ClickStartShareReceiver extends BroadcastReceiver{
    	public void onReceive(Context c, Intent intent) {
    		try{
        		//The result list fragment or fail result fragment
        		if(!intent.getExtras().get("apstate").equals("ok")){
        			UIScanFromProgressToNotReadyState();
        	        c.unregisterReceiver(this);
        			Constant.PreventAbuse.DOUBLE_START_SEND = false;
        			return;
        		}
				
        		String limitMode = sharedPreference.getString("SEND_LIMIT_MODE");
				if(limitMode.equals("UN")){
	    			Constant.PreventAbuse.DOUBLE_START_SEND = false;
	        		UIScanFromProgressToReadyState();
				}else{
					context.registerReceiver(listenStartReceiver, new IntentFilter(Constant.BroadcastSend.LISTEN_SETUP));
	    			context.registerReceiver(connectionStartReceiver, new IntentFilter(Constant.BroadcastSend.CONNECTION_SETUP));
				}
				
        		sendService.ListenHeartBeat();
        		c.unregisterReceiver(this);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }

	public class ListenStartReceiver extends BroadcastReceiver{
    	public void onReceive(Context c, Intent intent) {
    		try{
        		UIScanFromProgressToReadyState();
        		c.unregisterReceiver(this);
    			Constant.PreventAbuse.DOUBLE_START_SEND = false;
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }
	
	
	public class ConnectionStartReceiver extends BroadcastReceiver{
    	public void onReceive(Context c, Intent intent) {
    		try{
        		UIScanFromReadyStateToConnectState();
        		c.unregisterReceiver(this);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
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
