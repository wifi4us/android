/**
 * @(#)UserInformationFragment.java, 2014-1-23. 
 * 
 */
package com.paad.wifi4us.lottery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.paad.wifi4us.R;
import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.SharedPreferenceHelper;


/**
 * fragment for user to record their information for lottery
 * @author yangshi
 *
 */
public class UserInformationFragment extends Fragment{
	
	EditText id,name,alipay,phone;
	private SharedPreferenceHelper sharedPreference;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rst =  inflater.inflate(R.layout.fragment_lottery_info, container, false);
		sharedPreference = new SharedPreferenceHelper(getActivity()
				.getApplicationContext());
		String phoneValue = sharedPreference.getString(Constant.HttpParas.PHONE);
		phone = (EditText)rst.findViewById(R.id.lottery_pref_phone);
		if(!phoneValue.equals(SharedPreferenceHelper.NULL)){
			phone.setText(phoneValue);
		}
		String alipayValue = sharedPreference.getString(Constant.HttpParas.ALIPAY_ID);
		alipay = (EditText)rst.findViewById(R.id.lottery_pref_alipay);
		if(!alipayValue.equals(SharedPreferenceHelper.NULL)){
			alipay.setText(alipayValue);
		}
		
		String nameValue = sharedPreference.getString(Constant.HttpParas.NAME);
		name = (EditText)rst.findViewById(R.id.lottery_pref_username);
		if(!nameValue.equals(SharedPreferenceHelper.NULL)){
			name.setText(nameValue);
		}
		
		String idValue = sharedPreference.getString(Constant.HttpParas.ID_NUM);
		id = (EditText)rst.findViewById(R.id.lottery_pref_id);
		if(!idValue.equals(SharedPreferenceHelper.NULL)){
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
			sharedPreference.putString(key, value);
		}
	}
	
}
