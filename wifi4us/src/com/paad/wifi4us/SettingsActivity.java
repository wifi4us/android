package com.paad.wifi4us;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.paad.wifi4us.utility.SharedPreferenceHelper;

public class SettingsActivity extends Activity {
	
	private CheckBox app_setting_connect_sound;
	private CheckBox app_setting_ad_sound;
	private SharedPreferenceHelper sharedPreference;

	
	protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
       
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);
        sharedPreference = new SharedPreferenceHelper(getApplicationContext());
        
        app_setting_connect_sound = (CheckBox) findViewById(R.id.app_setting_connect_sound);           
        String currentConnectSoundMode = sharedPreference.getString("SOUND_CONNECT");
		if(currentConnectSoundMode.equals("YES")){
			app_setting_connect_sound.setChecked(true);
		}else if(currentConnectSoundMode.equals("NO")){
			app_setting_connect_sound.setChecked(false);
		}else{
			app_setting_connect_sound.setChecked(true);
			sharedPreference.putString("SOUND_CONNECT", "YES");
		}
		app_setting_connect_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if(isChecked){
					sharedPreference.putString("SOUND_CONNECT", "YES");
				}else{
					sharedPreference.putString("SOUND_CONNECT", "NO");
				}
			}

		});
		
		
		
		app_setting_ad_sound = (CheckBox) findViewById(R.id.app_setting_ad_sound);    
        String currentAdSoundMode = sharedPreference.getString("SOUND_AD");
		if(currentAdSoundMode.equals("YES")){
			app_setting_ad_sound.setChecked(true);
		}else if(currentAdSoundMode.equals("NO")){
			app_setting_ad_sound.setChecked(false);
		}else{
			app_setting_ad_sound.setChecked(true);
			sharedPreference.putString("SOUND_AD", "YES");
		}
		app_setting_ad_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if(isChecked){
					sharedPreference.putString("SOUND_AD", "YES");
				}else{
					sharedPreference.putString("SOUND_AD", "NO");
				}
			}

		});
      }
       
    
}
