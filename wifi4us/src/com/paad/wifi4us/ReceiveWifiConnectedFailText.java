package com.paad.wifi4us;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReceiveWifiConnectedFailText extends Fragment{
	private String textWord;
	
	public void setTextWord(String s){
		textWord = s;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View view_res = (View)inflater.inflate(R.layout.fragment_receive_wifi_connected_fail_text, container, false);
		TextView textView = (TextView)view_res.findViewById(R.id.receive_text_fail_reason);
		textView.setText(textWord);
		return view_res;
	}
}