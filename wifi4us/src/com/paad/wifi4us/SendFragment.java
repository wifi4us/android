package com.paad.wifi4us;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SendFragment extends Fragment{
	private FragmentManager fragmentManager;
	private Fragment send_id_start_share_button;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		fragmentManager = getFragmentManager();
		View view_res = inflater.inflate(R.layout.fragment_send, container, false);
		UICreate();
		return view_res;
	}
	
	private void UICreate(){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		
		send_id_start_share_button = new SendStartShareButton();
		transaction.add(R.id.send_container, send_id_start_share_button, "send_id_start_share_button");
		transaction.commitAllowingStateLoss();
		
	}

}
