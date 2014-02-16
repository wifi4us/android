package com.paad.wifi4us;

import com.paad.wifi4us.utility.SharedPreferenceHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SendStopShareText extends Fragment{
	private SharedPreferenceHelper sharedPreference;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
    	sharedPreference = new SharedPreferenceHelper(getActivity().getApplicationContext());

		View view_res = inflater.inflate(R.layout.fragment_send_stop_share_text, container, false);
		TextView text = (TextView)view_res.findViewById(R.id.send_id_text_stop_share);
		
		String limitMode = sharedPreference.getString("SEND_LIMIT_MODE");
		if(limitMode.equals("UN")){
			text.setText("当前为不限制流量模式，只能手动断开");
		}else{
			text.setText("已经创建完成，等待接入中");
		}
		
		return view_res;
	}

}
