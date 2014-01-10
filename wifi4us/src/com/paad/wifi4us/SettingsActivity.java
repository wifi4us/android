package com.paad.wifi4us;

import com.paad.wifi4us.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * 
 *
 * @author yangshi
 *
 */
public class SettingsActivity extends PreferenceActivity {

    /* (non-Javadoc)
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_setting);
    }
    
}
