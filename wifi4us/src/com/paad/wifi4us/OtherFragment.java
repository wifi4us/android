package com.paad.wifi4us;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paad.wifi4us.utility.HttpXmlParser;

public class OtherFragment extends Fragment{
	private TextView other_id_userid_text;
	private static final int MSG_SUCCESS = 0; 
    private static final int MSG_FAILURE = 1;  
    private static final String REGISTER_BASE_HTTPURL= "http://wifi4us.duapp.com/register.php";
	
    private Handler mHandler = new Handler(){  
		public void handleMessage (Message msg){
			switch(msg.what){  
				case MSG_SUCCESS:  
					other_id_userid_text.setText("用户名： " + (String) msg.obj);
					Editor sharedata = getActivity().getSharedPreferences(getActivity().getApplicationContext().getPackageName(), Context.MODE_PRIVATE).edit(); 
	        		sharedata.putString("USER_ID", (String) msg.obj);
	        		sharedata.commit();
	                break;  
	            case MSG_FAILURE:  
	            	other_id_userid_text.setText("获取id失败，无法使用服务，请保持网路畅通，退出软件重新进入");
	                break;  
			}  
		}  
    };    
    
    Runnable getUserIdRunner = new Runnable() {   
    	public void run() { 
    		
    			TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        		String registerUrl = REGISTER_BASE_HTTPURL + "?" + "imei=" + tm.getDeviceId();
        		
        		HttpXmlParser xpp = new HttpXmlParser();
        		SimpleArrayMap<String, String> result = new SimpleArrayMap<String, String>();

        		if(xpp.getResultFromURL(registerUrl, result)){
            		String userid = result.get("userid");
            		mHandler.obtainMessage(MSG_SUCCESS, userid).sendToTarget();             
        		}else{
            		mHandler.obtainMessage(MSG_FAILURE).sendToTarget();             
        		}

			
        }  
    }; 
	 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_other, container, false);
	}
	
	public void onStart(){
		super.onStart();
		other_id_userid_text = (TextView)getActivity().findViewById(R.id.userid_text);
		SharedPreferences sharedata = getActivity().getSharedPreferences(getActivity().getApplicationContext().getPackageName(), Context.MODE_PRIVATE); 
		String userid = sharedata.getString("USER_ID", "NULL");
		other_id_userid_text.setText("用户名： " + userid);
		if(userid.equals("NULL")){
			Thread mThread = new Thread(getUserIdRunner);  
            mThread.start();
		}
	}
	



}
