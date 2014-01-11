package com.paad.wifi4us.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceHelper {
	private SharedPreferences reader;
	private Editor writer;

	public SharedPreferenceHelper(Context context){
		reader = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE); 
		writer = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit(); 
	}

	public boolean getBoolean(String key){
		return reader.getBoolean(key, false);
	}
	
	public void putBoolean(String key, boolean value){
		writer.putBoolean(key, value);
		writer.commit();
	}
	
	public String getString(String key){
		return reader.getString(key, "NULL");
	}
	
	public void putString(String key, String value){
		writer.putString(key, value);
		writer.commit();
	}
}
