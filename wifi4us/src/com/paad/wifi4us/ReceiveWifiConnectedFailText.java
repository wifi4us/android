package com.paad.wifi4us;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReceiveWifiConnectedFailText extends Fragment{

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		return inflater.inflate(R.layout.fragment_receive_wifi_connected_fail_text, container, false);
	}
}