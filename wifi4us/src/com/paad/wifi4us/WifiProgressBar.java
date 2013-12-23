package com.paad.wifi4us;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WifiProgressBar extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		return inflater.inflate(R.layout.fragment_wifi_progressbar, container, false);
	}
}
