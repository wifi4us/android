/**
 * @(#)UserInformationFragment.java, 2014-1-23. 
 * 
 */
package com.paad.wifi4us.lottery;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.paad.wifi4us.R;
import com.paad.wifi4us.utility.Constant;


/**
 * fragment for user to record their information for lottery
 * @author yangshi
 *
 */
public class UserInformationFragment extends Fragment{
	
	EditText id,name,alipay,phone;
	SharedPreferences sharedPreference;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rst =  inflater.inflate(R.layout.fragment_lottery_info, container, false);
		sharedPreference = getActivity().getSharedPreferences("lottery", Context.MODE_PRIVATE);
		String phoneValue = sharedPreference.getString(Constant.HttpParas.PHONE, null);
		phone = (EditText)rst.findViewById(R.id.lottery_pref_phone);
		if(phoneValue != null){
			phone.setText(phoneValue);
		}
		String alipayValue = sharedPreference.getString(Constant.HttpParas.ALIPAY_ID, null);
		alipay = (EditText)rst.findViewById(R.id.lottery_pref_alipay);
		if(alipayValue != null){
			alipay.setText(alipayValue);
		}
		
		String nameValue = sharedPreference.getString(Constant.HttpParas.NAME, null);
		name = (EditText)rst.findViewById(R.id.lottery_pref_username);
		if(nameValue != null){
			name.setText(nameValue);
		}
		
		String idValue = sharedPreference.getString(Constant.HttpParas.ID_NUM, null);
		id = (EditText)rst.findViewById(R.id.lottery_pref_id);
		if(idValue != null){
			id.setText(idValue);
		}
		return rst;
	}

	@Override
	public void onPause() {
		savePref(id,Constant.HttpParas.ID_NUM);
		savePref(name,Constant.HttpParas.NAME);
		savePref(alipay,Constant.HttpParas.ALIPAY_ID);
		savePref(phone,Constant.HttpParas.PHONE);
		super.onPause();
	}
	
	void savePref(EditText editText, String key){
		String value = editText.getText().toString();
		if(value != null && value.length()!=0){
			Editor editor = sharedPreference.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}
	
}
